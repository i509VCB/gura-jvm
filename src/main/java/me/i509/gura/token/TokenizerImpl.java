package me.i509.gura.token;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Gura tokenizer.
 */
final class TokenizerImpl {
	static TokenizationResult tokenize(String content) {
		List<Token> tokens = new ArrayList<>();
		Cursor cursor = new Cursor(content);

		while (true) {
			if (!nextToken(cursor, tokens, true)) {
				break;
			}
		}

		return new TokenizationResult(tokens);
	}

	/**
	 * Reads the next token in the file.
	 *
	 * @param cursor the cursor tracking the current position in the content of the file
	 * @param output the output to return tokens to
	 * @param consume whether the cursor should be advanced and the token added to the output list
	 * @return true if there are any more tokens
	 */
	static boolean nextToken(Cursor cursor, List<Token> output, boolean consume) {
		/*
		 * Tokenizing occurs in a three steps:
		 *
		 * 1. Peek forward to determine the type of token.
		 * 2. Determine type of token, and move the cursor if we are consuming.
		 * 3. Peek forward to determine if we have another token after the token that was just consumed.
		 */

		// End of content.
		if (cursor.remaining() == 0) {
			if (consume) {
				output.add(new Token(0, Token.Type.NEWLINE, cursor.line, cursor.column));
			}

			return false;
		}

		Character c = cursor.peek();
		assert c != null;

		// Narrow down the type of token
		switch (c) {
			// Space whitespace
			case ' ':
				if (consume) {
					int length = 1;

					while (cursor.remaining() > length) {
						Character next = cursor.peekBy(length);
						assert next != null;

						if (next != ' ') {
							break;
						}

						length++;
					}

					output.add(new Token(length, Token.Type.SPACE_WS, cursor.line, cursor.column));
					cursor.advanceBy(length);
				}

				break;

			// Tab whitespace
			case '\t':
				throw new UnsupportedOperationException("TODO");

			// Open an array
			case '[':
				if (consume) {
					output.add(new Token(1, Token.Type.ARRAY_START, cursor.line, cursor.column));
					cursor.advanceBy(1);
				}

				break;

			// Close an array
			case ']':
				if (consume) {
					output.add(new Token(1, Token.Type.ARRAY_END, cursor.line, cursor.column));
					cursor.advanceBy(1);
				}

				break;

			// Comma separating entries in an array
			case ',':
				if (consume) {
					output.add(new Token(1, Token.Type.COMMA, cursor.line, cursor.column));
					cursor.advanceBy(1);
				}

				break;

			// Comment
			case '#':
				if (consume) {
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

					output.add(new Token(length, Token.Type.COMMA, cursor.line, cursor.column));
					cursor.advanceBy(length);
				}

				break;

			// Literal string
			case '\'':
				throw new UnsupportedOperationException("TODO");

			// Basic string
			case '"':
				throw new UnsupportedOperationException("TODO");

			// An integer or a floating point number
			case '-':
			case '+':
				throw new UnsupportedOperationException("TODO");

			// Unix style line ending
			case '\n':
				if (consume) {
					output.add(new Token(1, Token.Type.NEWLINE, cursor.line, cursor.column));

					cursor.cursor++;
					cursor.line++;
					cursor.column = 1;
				}

				break;

			// Windows style line ending
			case '\r':
				// There is another character following this `\r`, is it a `\n`?
				if (cursor.remaining() > 1) {
					Character next = cursor.peekBy(1);
					assert next != null;

					// Next character must be `\n` for this to be a valid Windows line ending.
					if (next == '\n') {
						if (consume) {
							output.add(new Token(2, Token.Type.NEWLINE, cursor.line, cursor.column));

							cursor.cursor += 2; // `\r\n` is 2 characters long.
							cursor.line++;
							cursor.column = 1;
						}

						break;
					}
				}

			// Fall-through on only a `\r` with no following `\n` to the default case.

			default:
				// One of the following:
				// - A variable or environment variable, key or value (any type or empty).
				// - An import
				// - Declaration of a field in an object
				// - An invalid token

				if (consume) {
					// Peek forward till we reach a `:`, whitespace, comment, end of line or end of file
					Token.Type tokenType = null;
					int length = 1;

					while (cursor.remaining() > length) {
						Character next = cursor.peekBy(length);
						assert next != null;

						switch (next) {
							case ':':
								tokenType = Token.Type.KEY;
								// Consume the colon also
								length++;
								break;
							case '\n':
							case '\r':
							case '#':
							case ' ':
								tokenType = Token.Type.VALUE_OR_IMPORT;
								break;
						}

						if (tokenType != null) {
							break;
						}

						length++;
					}

					// Finish up the last value as a value or import since it never terminated.
					if (tokenType == null) {
						tokenType = Token.Type.VALUE_OR_IMPORT;
					}

					output.add(new Token(length, tokenType, cursor.line, cursor.column));
					cursor.advanceBy(length);
				}
		}

		// Post-parse: if we are consuming does the next token exist?
		if (consume) {
			// Use peek mode to determine if we have another next token.
			return nextToken(cursor, output, false);
		}

		return true;
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
