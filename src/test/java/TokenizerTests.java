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

	@Disabled("Not implemented")
	@Test
	public void variableKeyAndNumberValue() {
		var expected = List.of(
				new Token(16, Token.Type.IDENTIFIER, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 17)
				//new Token(2, Token.Type.VALUE_OR_IMPORT, 1, 18)
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

	@Disabled("Not implemented")
	@Test
	public void positiveSignedNumber() {
		var expected = List.of(
				new Token(1, Token.Type.PLUS, 1, 1),
				new Token(3, Token.Type.NUMBER, 1, 2)
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

	@Disabled("Plain numbers not implemented yet")
	@Test
	public void fiveHundredAndNine() {
		var expected = List.of(
				new Token(3, Token.Type.NUMBER, 1, 1)
		);

		assertEquals(expected, Token.tokenize("509").tokens());
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
