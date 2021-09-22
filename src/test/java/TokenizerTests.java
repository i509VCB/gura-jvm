import java.util.List;

import me.i509.gura.token.Token;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class KeyAndValue {
	@Test
	public void simpleKeyAndValue() {
		var expected = List.of(
			new Token(3, Token.Type.IDENTIFIER, 1, 1),
			new Token(1, Token.Type.COLON, 1, 4),
			new Token(1, Token.Type.SPACE_WS, 1, 5),
			new Token(4, Token.Type.IDENTIFIER, 1, 6)
		);

		assertEquals(expected, Token.tokenize("key: null").tokens());
	}

	@Test
	public void simpleKeyAndVariableValue() {
		var expected = List.of(
				new Token(3, Token.Type.IDENTIFIER, 1, 1),
				new Token(1, Token.Type.COLON, 1, 4),
				new Token(1, Token.Type.SPACE_WS, 1, 5),
				new Token(6, Token.Type.IDENTIFIER, 1, 6)
		);

		assertEquals(expected, Token.tokenize("key: $value").tokens());
	}

	@Test
	public void variableKeyAndValue() {
		var expected = List.of(
				new Token(4, Token.Type.IDENTIFIER, 1, 1),
				new Token(1, Token.Type.COLON, 1, 5),
				new Token(1, Token.Type.SPACE_WS, 1, 6),
				new Token(4, Token.Type.IDENTIFIER, 1, 7)
		);

		assertEquals(expected, Token.tokenize("$key: null").tokens());
	}

	@Test
	public void variableKeyAndVariableValue() {
		var expected = List.of(
				new Token(4, Token.Type.IDENTIFIER, 1, 1),
				new Token(1, Token.Type.COLON, 1, 5),
				new Token(1, Token.Type.SPACE_WS, 1, 6),
				new Token(6, Token.Type.IDENTIFIER, 1, 7)
		);

		assertEquals(expected, Token.tokenize("$key: $value").tokens());
	}

	@Test
	public void variableKeyAndNumberValue() {
		var expected = List.of(
				new Token(15, Token.Type.IDENTIFIER, 1, 1),
				new Token(1, Token.Type.COLON, 1, 16),
				new Token(1, Token.Type.SPACE_WS, 1, 17),
				new Token(2, Token.Type.NUMBER, 1, 18)
		);

		assertEquals(expected, Token.tokenize("meaning_of_life: 42").tokens());
	}
}

final class NumbersWithSign {
	@Test
	public void positiveZero() {
		var expected = List.of(
				new Token(1, Token.Type.PLUS, 1, 1),
				new Token(1, Token.Type.NUMBER, 1, 2)
		);

		assertEquals(expected, Token.tokenize("+0").tokens());
	}

	@Test
	public void negativeZero() {
		var expected = List.of(
				new Token(1, Token.Type.MINUS, 1, 1),
				new Token(1, Token.Type.NUMBER, 1, 2)
		);

		assertEquals(expected, Token.tokenize("-0").tokens());
	}

	@Test
	public void positiveSignedNumber() {
		var expected = List.of(
				new Token(1, Token.Type.PLUS, 1, 1),
				new Token(2, Token.Type.NUMBER, 1, 2)
		);

		assertEquals(expected, Token.tokenize("+42").tokens());
	}
}

final class NumbersWithoutSign {
	@Test
	public void zero() {
		var expected = List.of(
				new Token(1, Token.Type.NUMBER, 1, 1)
		);

		assertEquals(expected, Token.tokenize("0").tokens());
	}

	@Test
	public void five() {
		var expected = List.of(
				new Token(1, Token.Type.NUMBER, 1, 1)
		);

		assertEquals(expected, Token.tokenize("5").tokens());
	}

	@Test
	public void deadbeef() {
		var expected = List.of(
				new Token(10, Token.Type.NUMBER, 1, 1)
		);

		assertEquals(expected, Token.tokenize("0xDEADBEEF").tokens());
	}

	@Test
	public void deadbeefFollowedByWs() {
		var expected = List.of(
				new Token(10, Token.Type.NUMBER, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 11)
		);

		assertEquals(expected, Token.tokenize("0xDEADBEEF ").tokens());
	}

	@Test
	public void octal() {
		var expected = List.of(
				new Token(5, Token.Type.NUMBER, 1, 1)
		);

		assertEquals(expected, Token.tokenize("0o666").tokens());
	}

	@Test
	public void binary() {
		var expected = List.of(
				new Token(6, Token.Type.NUMBER, 1, 1)
		);

		assertEquals(expected, Token.tokenize("0b1001").tokens());
	}

	@Test
	public void fiveHundredAndNine() {
		var expected = List.of(
				new Token(3, Token.Type.NUMBER, 1, 1)
		);

		assertEquals(expected, Token.tokenize("509").tokens());
	}

	@Test
	public void fourtyTwo() {
		var expected = List.of(
				new Token(2, Token.Type.NUMBER, 1, 1)
		);

		assertEquals(expected, Token.tokenize("42").tokens());
	}

	@Test
	public void fourtyTwoFollowedByWs() {
		var expected = List.of(
				new Token(2, Token.Type.NUMBER, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 3)
		);

		assertEquals(expected, Token.tokenize("42 ").tokens());
	}

	/**
	 * Attempt to parse a number with an incomplete encoding base.
	 *
	 * <p>For the purposes of tokenizing, we treat a number with an incomplete encoding base as a number and deal with
	 * errors at parse time.
	 */
	@Test
	public void incompleteEncodingBase() {
		var expected = List.of(
				new Token(2, Token.Type.NUMBER, 1, 1)
		);

		assertEquals(expected, Token.tokenize("0x").tokens());
	}
}

final class Whitespace {
	@Test
	public void emptyInput() {
		var expected = List.of();

		assertEquals(expected, Token.tokenize("").tokens());
	}

	@Test
	public void oneSpace() {
		var expected = List.of(
				new Token(1, Token.Type.SPACE_WS, 1, 1)
		);

		assertEquals(expected, Token.tokenize(" ").tokens());
	}

	@Test
	public void spaceNewLineSpace() {
		var expected = List.of(
				new Token(1, Token.Type.SPACE_WS, 1, 1),
				new Token(1, Token.Type.NEWLINE, 1, 2),
				new Token(1, Token.Type.SPACE_WS, 2, 1)
		);

		assertEquals(expected, Token.tokenize(" \n ").tokens());
	}

	@Test
	public void fourSpace() {
		var expected = List.of(
				new Token(4, Token.Type.SPACE_WS, 1, 1)
		);

		assertEquals(expected, Token.tokenize("    ").tokens());
	}

	@Test
	public void oneTab() {
		var expected = List.of(
				new Token(1, Token.Type.TAB_WS, 1, 1)
		);

		assertEquals(expected, Token.tokenize("\t").tokens());
	}

	@Test
	public void oneTabThenSpace() {
		var expected = List.of(
				new Token(1, Token.Type.TAB_WS, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 2)
		);

		assertEquals(expected, Token.tokenize("\t ").tokens());
	}
}

final class Comments {
	@Test
	public void onlyComment() {
		var expected = List.of(
				new Token(1, Token.Type.COMMENT, 1, 1)
		);

		assertEquals(expected, Token.tokenize("#").tokens());
	}

	@Test
	public void twoCommentSymbolsOnlyOneComment() {
		var expected = List.of(
				new Token(3, Token.Type.COMMENT, 1, 1)
		);

		assertEquals(expected, Token.tokenize("##a").tokens());
	}

	@Test
	public void endOfLineComment() {
		var expected = List.of(
				new Token(4, Token.Type.NUMBER, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 5),
				new Token(3, Token.Type.COMMENT, 1, 6)
		);

		assertEquals(expected, Token.tokenize("0xFF ##a").tokens());
	}

	@Test
	public void commentThenNewline() {
		var expected = List.of(
				new Token(14, Token.Type.COMMENT, 1, 1),
				new Token(1, Token.Type.NEWLINE, 1, 15)
		);

		assertEquals(expected, Token.tokenize("# some comment\n").tokens());
	}

	@Test
	public void commentThenNewlineThenComment() {
		var expected = List.of(
				new Token(14, Token.Type.COMMENT, 1, 1),
				new Token(1, Token.Type.NEWLINE, 1, 15),
				new Token(17, Token.Type.COMMENT, 2, 1)
		);

		assertEquals(expected, Token.tokenize("# some comment\n# another comment").tokens());
	}

	@Test
	public void commentThenNewlineWindows() {
		var expected = List.of(
				new Token(14, Token.Type.COMMENT, 1, 1),
				new Token(2, Token.Type.NEWLINE, 1, 15)
		);

		assertEquals(expected, Token.tokenize("# some comment\r\n").tokens());
	}
}

final class Arrays {
	@Test
	public void tenCommas() {
		var expected = List.of(
				new Token(1, Token.Type.COMMA, 1, 1),
				new Token(1, Token.Type.COMMA, 1, 2),
				new Token(1, Token.Type.COMMA, 1, 3),
				new Token(1, Token.Type.COMMA, 1, 4),
				new Token(1, Token.Type.COMMA, 1, 5),
				new Token(1, Token.Type.COMMA, 1, 6),
				new Token(1, Token.Type.COMMA, 1, 7),
				new Token(1, Token.Type.COMMA, 1, 8),
				new Token(1, Token.Type.COMMA, 1, 9),
				new Token(1, Token.Type.COMMA, 1, 10)
		);

		assertEquals(expected, Token.tokenize(",,,,,,,,,,").tokens());
	}

	@Test
	public void unclosedEmptyArray() {
		var expected = List.of(
				new Token(1, Token.Type.LEFT_BRACKET, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 2)
		);

		assertEquals(expected, Token.tokenize("[ ").tokens());
	}

	@Test
	public void closedEmptyArray() {
		var expected = List.of(
				new Token(1, Token.Type.LEFT_BRACKET, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 2),
				new Token(1, Token.Type.RIGHT_BRACKET, 1, 3)
		);

		assertEquals(expected, Token.tokenize("[ ]").tokens());
	}

	@Test
	public void openTwiceAndCloseArray() {
		var expected = List.of(
				new Token(1, Token.Type.LEFT_BRACKET, 1, 1),
				new Token(1, Token.Type.LEFT_BRACKET, 1, 2),
				new Token(1, Token.Type.RIGHT_BRACKET, 1, 3),
				new Token(1, Token.Type.RIGHT_BRACKET, 1, 4)
		);

		assertEquals(expected, Token.tokenize("[[]]").tokens());
	}
}
