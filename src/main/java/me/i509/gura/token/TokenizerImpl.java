package me.i509.gura.token;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

/**
 * Implementation of the Gura tokenizer.
 */
final class TokenizerImpl {
	static TokenizationResult tokenize(String content) {
		List<Token> tokens = new ArrayList<>();
		Cursor cursor = new Cursor(content);

		while (true) {
			Token nextToken;

			if ((nextToken = nextToken(cursor, true)) == null) {
				break;
			}

			tokens.add(nextToken);
		}

		return new TokenizationResult(tokens);
	}

	/**
	 * @param c the character
	 * @return true if the character may be part of the digits of a number
	 */
	static boolean isValidNumberDigit(char c) {
		return (c >= '0' && c <= '9')
				|| c == 'e' // Exponent
				|| c == 'E' // 0e6 is a redundant but still valid floating point value
				|| c == '.' // Decimal point
				|| c == '_'; // Spacing
	}

	/**
	 * @param c the character
	 * @return true if the character may be part of the encoding base or digits of a number
	 */
	static boolean isValidEncodingBaseOrNumberDigit(char c) {
		return (c >= '0' && c <= '9')
				|| c == 'b' // Binary
				|| c == 'o' // Octal
				|| c == 'x' // Hexadecimal
				|| c == 'e' // Exponent
				|| c == 'E' // 0e6 is a redundant but still valid floating point value
				|| c == '.' // Decimal point
				|| c == '+' // Sign of an exponent
				|| c == '-'
				|| c == '_'; // Spacing
	}

	/**
	 * @param c the character
	 * @return true if the character may be part of the latter digits of a regular number or hexadecimal number
	 */
	static boolean isValidHexadecimalOrOctalOrBinaryDigit(char c) {
		return (c >= '0' && c <= '9')
				|| (c >= 'a' && c <= 'f') // Exponents also get covered within this range
				|| (c >= 'A' && c <= 'F')
				|| c == '_'; // Spacing;
	}

	/**
	 * Reads the next token in the file.
	 *
	 * @param cursor the cursor tracking the current position in the content of the file
	 * @param advanceCursor whether the cursor should be advanced after obtaining a token
	 * @return the next token or null
	 */
	@Nullable
	static Token nextToken(Cursor cursor, boolean advanceCursor) {
		/*
		 * Tokenizing occurs in a two steps:
		 *
		 * 1. Peek forward to determine the type of token.
		 * 2. Determine type of token, and move the cursor if we are consuming.
		 */

		// End of content.
		if (cursor.remaining() <= 0) {
			return null;
		}

		Character c = cursor.peek();
		assert c != null;

		// Narrow down the type of token
		switch (c) {
			// Space whitespace
			case ' ': {
				int length = 1;

				while (cursor.remaining() > length) {
					Character next = cursor.peekBy(length);
					assert next != null;

					if (next != ' ') {
						break;
					}

					length++;
				}

				Token token = new Token(length, Token.Type.SPACE_WS, cursor.line, cursor.column);

				if (advanceCursor) {
					cursor.advanceBy(length);
				}

				return token;
			}

			// Tab whitespace
			case '\t': {
				int length = 1;

				while (cursor.remaining() > length) {
					Character next = cursor.peekBy(length);
					assert next != null;

					if (next != '\t') {
						break;
					}

					length++;
				}

				Token token = new Token(length, Token.Type.TAB_WS, cursor.line, cursor.column);

				if (advanceCursor) {
					cursor.advanceBy(length);
				}

				return token;
			}

			// Open an array
			case '[': {
				Token token = new Token(1, Token.Type.LEFT_BRACKET, cursor.line, cursor.column);

				if (advanceCursor) {
					cursor.advanceBy(1);
				}

				return token;
			}

			// Close an array
			case ']': {
				Token token = new Token(1, Token.Type.RIGHT_BRACKET, cursor.line, cursor.column);

				if (advanceCursor) {
					cursor.advanceBy(1);
				}

				return token;
			}

			// Comma separating entries in an array
			case ',': {
				Token token = new Token(1, Token.Type.COMMA, cursor.line, cursor.column);

				if (advanceCursor) {
					cursor.advanceBy(1);
				}

				return token;
			}

			// Comment
			case '#': {
				int length = 1;

				while (cursor.remaining() > length) {
					Character next = cursor.peekBy(length);
					assert next != null;

					// Find the start of the newline and terminate once encountered.
					if (next == '\n' || next == '\r') {
						break;
					}

					length++;
				}

				Token token = new Token(length, Token.Type.COMMENT, cursor.line, cursor.column);

				if (advanceCursor) {
					cursor.advanceBy(length);
				}

				return token;
			}

			// Literal string
			case '\'': {
				throw new UnsupportedOperationException("TODO");
			}

			// String
			case '"': {
				throw new UnsupportedOperationException("TODO");
			}

			case ':': {
				Token token = new Token(1, Token.Type.COLON, cursor.line, cursor.column);

				if (advanceCursor) {
					cursor.advanceBy(1);
				}

				return token;
			}

			// Unix style line ending
			case '\n': {
				Token token = new Token(1, Token.Type.NEWLINE, cursor.line, cursor.column);

				if (advanceCursor) {
					cursor.cursor++;
					cursor.line++;
					cursor.column = 1;
				}

				return token;
			}

			// An integer or a floating point number
			//
			// Must start with a sign or number character
			case '-': {
				Token token = new Token(1, Token.Type.MINUS, cursor.line, cursor.column);

				if (advanceCursor) {
					cursor.advanceBy(1);
				}

				return token;
			}

			case '+': {
				Token token = new Token(1, Token.Type.PLUS, cursor.line, cursor.column);

				if (advanceCursor) {
					cursor.advanceBy(1);
				}

				return token;
			}

			// Windows style line ending
			case '\r': {
				// There is another character following this `\r`, is it a `\n`?
				if (cursor.remaining() > 1) {
					Character next = cursor.peekBy(1);
					assert next != null;

					// Next character must be `\n` for this to be a valid Windows line ending.
					if (next == '\n') {
						Token token = new Token(2, Token.Type.NEWLINE, cursor.line, cursor.column);

						if (advanceCursor) {
							cursor.cursor += 2; // `\r\n` is 2 characters long.
							cursor.line++;
							cursor.column = 1;
						}

						return token;
					}
				}
			}

			// Fall-through on only a `\r` with no following `\n` to the default case.

			default:
				// "inf" and "nan" are handled as identifiers after this if block.
				if (c >= '0' && c <= '9') {
					// Try to parse numbers
					if (cursor.remaining() <= 1) {
						// Just an integer
						Token token = new Token(1, Token.Type.NUMBER, cursor.line, cursor.column);

						if (advanceCursor) {
							cursor.advanceBy(1);
						}

						return token;
					}

					boolean firstCharacterIsZero = c == '0';

					Character next = cursor.peekBy(1);
					assert next != null;

					if (firstCharacterIsZero) {
						// Try to parse encoding base
						if (isValidEncodingBaseOrNumberDigit(next)) {
							// We have an encoding base with no value, return a number albeit an invalid one
							if (cursor.remaining() == 2) {
								Token token = new Token(2, Token.Type.NUMBER, cursor.line, cursor.column);

								if (advanceCursor) {
									cursor.advanceBy(2);
								}

								return token;
							}

							int length = 3;

							while (cursor.remaining() >= length) {
								next = cursor.peekBy(length - 1);
								assert next != null;

				 				if (!isValidHexadecimalOrOctalOrBinaryDigit(next)) {
									// Have we reached the end of the token?
									if (next == ' ' || next == '\t' || next == '\r' || next == '\n' || next == '#') {
										Token token = new Token(length - 1, Token.Type.NUMBER, cursor.line, cursor.column);

										if (advanceCursor) {
											cursor.advanceBy(length - 1);
										}

										return token;
									}

									// exit since this is not a valid number.
									break;
								}

								length++;
							}

							// Reached end of stream
							if (cursor.remaining() - length <= 0) {
								Character last = cursor.peekBy(length - 2);
								assert last != null;

								if (isValidHexadecimalOrOctalOrBinaryDigit(last)) {
									Token token = new Token(length - 1, Token.Type.NUMBER, cursor.line, cursor.column);

									if (advanceCursor) {
										cursor.advanceBy(length);
									}

									return token;
								}

								// fall-through since this is not a valid number.
							}
						}
					} else {
						if (isValidNumberDigit(next)) {
							int length = 2;

							while (cursor.remaining() >= length) {
								next = cursor.peekBy(length - 1);
								assert next != null;

								if (!isValidNumberDigit(next)) {
									// Have we reached the end of the token?
									if (next == ' ' || next == '\t' || next == '\r' || next == '\n' || next == '#') {
										Token token = new Token(length - 1, Token.Type.NUMBER, cursor.line, cursor.column);

										if (advanceCursor) {
											cursor.advanceBy(length - 1);
										}

										return token;
									}

									// exit since this is not a valid number.
									break;
								}

								length++;
							}

							// Reached end of stream
							if (cursor.remaining() - length <= 0) {
								Character last = cursor.peekBy(length - 2);
								assert last != null;

								if (isValidNumberDigit(last)) {
									Token token = new Token(length - 1, Token.Type.NUMBER, cursor.line, cursor.column);

									if (advanceCursor) {
										cursor.advanceBy(length);
									}

									return token;
								}

								// fall-through since this is not a valid number.
							}
						}
					}
				}

				// The number we were looking at is likely not a number, fall-through to tokenize as an identifier.

				// If we reach here, we have one of the following:
				// Some sort of keyword, such as "import", "empty", "inf", "nan"
				// An invalid number, such as a number with invalid characters.
				// Some sort of identifier, such as the key in a key value entry.
				// Some sort of invalid characters in the file.

				// Validate the character a valid Gura character?
				// TODO: return INVALID if this is not a valid character

				// Peek forward until we find a token type which differs from an identifier.
				// Store past position of the cursor for length cursor state
				int previousRawCursor = cursor.cursor;
				int line = cursor.line;
				int column = cursor.column;

				Token nextToken;

				// TODO: Avoid as much recursion

				// do while loop here is intentional since we want to advance the cursor and then check the exit condition
				do {
					cursor.advanceBy(1);
				} while ((nextToken = nextToken(cursor, false)) != null && nextToken.type() == Token.Type.IDENTIFIER);

				// We have either reached the end of the file or the next token is not an identifier, so we can end this
				// token.

				Token token = new Token(
						cursor.cursor - previousRawCursor, // Length of the token
						Token.Type.IDENTIFIER,
						line, // Use start line and column
						column
				);

				if (!advanceCursor) {
					// Roll back the cursor state since we are not advancing the cursor
					cursor.cursor = previousRawCursor;
					cursor.line = line;
					cursor.column = column;
				}

				return token;
		}
	}

	private TokenizerImpl() {}

	/**
	 * A cursor tracking the current position of the tokenizer in a file.
	 */
	private static final class Cursor {
		private final String content;
		private final int length;
		private int cursor;
		int line;
		int column;

		Cursor(String content) {
			this.content = content;
			this.length = content.length();
			this.line = 1;
			this.column = 1;
		}

		/**
		 * Peek forward by one character.
		 *
		 * @return the next character, or null
		 */
		public Character peek() {
			if (this.remaining() == 0) {
				return null;
			}

			return this.content.charAt(this.cursor);
		}

		/**
		 * Peeks forward by a specified amount of characters.
		 *
		 * @param amount the amount of characters to peek forward relative to the cursor
		 * @return the character, or null
		 */
		public Character peekBy(int amount) {
			if (this.remaining() < amount) {
				return null;
			}

			return this.content.charAt(this.cursor + amount);
		}

		public void advanceBy(int amount) {
			this.cursor += amount;
			this.column += amount;
		}

		public int remaining() {
			return this.length - this.cursor;
		}
	}
}
