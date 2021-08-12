package me.i509.gura.token;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * A token in a Gura file.
 *
 * <p>A token is simply a length and a type. Although for convince, the row and column of the token within a file is
 * also included.
 *
 * <p>To get a list of tokens, use the {@link Token#tokenize(String)} method.
 */
public final class Token {
	/**
	 * Generates a list of tokens from the content of a gura file.
	 *
	 * @param content the content of the Gura file.
	 * @return an immutable list of tokens
	 */
	public static TokenizationResult tokenize(String content) {
		return TokenizerImpl.tokenize(content);
	}

	private final int length;
	private final Type type;
	private final int row;
	private final int column;

	public Token(int length, Type type, int row, int column) {
		this.length = length;
		this.type = type;
		this.row = row;
		this.column = column;
	}

	/**
	 * @return the length of this token
	 */
	public int length() {
		return this.length;
	}

	/**
	 * @return the type of this token
	 */
	public Type type() {
		return this.type;
	}

	/**
	 * @return the row of this token in a file
	 */
	public int row() {
		return this.row;
	}

	/**
	 * @return the column of where this token starts in a file
	 */
	public int column() {
		return this.column;
	}

	/**
	 * @return true if this token is valid
	 */
	public boolean valid() {
		return this.type.valid();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Token token = (Token) o;
		return this.length == token.length
				&& this.row == token.row
				&& this.column == token.column
				&& this.type == token.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.length, this.type, this.row, this.column);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Token.class.getSimpleName() + "[", "]")
				.add("length=" + this.length)
				.add("type=" + this.type)
				.add("row=" + this.row)
				.add("column=" + this.column)
				.toString();
	}

	/**
	 * A type of token that may be encountered.
	 */
	public enum Type {
		/**
		 * Space whitespace character.
		 */
		SPACE_WS,

		/**
		 * Tab character whitespace.
		 */
		TAB_WS,

		/**
		 * A newline character, LF or CRLF.
		 */
		NEWLINE,

		/**
		 * A comment.
		 */
		COMMENT,

		/**
		 * The key half of a key-value pair.
 		 */
		KEY,

		/**
		 * A floating point or integer number with a specified sign.
		 */
		INTEGER_OR_FLOATING_WITH_SIGN,

		/**
		 * A value of an undetermined type or an import.
		 *
		 * <p>The type of this value may be one of the following: null, integer, floating point number, boolean, empty value,
		 */
		VALUE_OR_IMPORT,

		/**
		 * A basic string value to a key.
		 */
		BASIC_STRING_VALUE,

		/**
		 * A multiline string value to a key.
		 */
		MULTILINE_STRING_VALUE,

		/**
		 * A literal string value to a key.
		 */
		LITERAL_STRING_VALUE,

		/**
		 * A multiline literal string value to a key.
		 */
		MULTILINE_LITERAL_STRING_VALUE,

		/**
		 * The start of an array value.
		 */
		ARRAY_START,

		/**
		 * The end of an array value.
		 */
		ARRAY_END,

		/**
		 * A comma separating array entries.
		 */
		COMMA,

		/**
		 * End of file.
		 */
		EOF,

		/**
		 * An unknown, invalid token.
		 */
		UNKNOWN;

		/**
		 * @return true if the token is valid
		 */
		public boolean valid() {
			return this != UNKNOWN;
		}

		/**
		 * @return true if this token is whitespace
		 */
		public boolean whitespace() {
			return this == SPACE_WS || this == TAB_WS;
		}
	}
}
