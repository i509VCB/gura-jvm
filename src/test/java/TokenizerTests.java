import java.util.List;

import me.i509.gura.token.Token;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class KeyAndValue {
	@Test
	public void simpleKeyAndValue() {
		var expected = List.of(
			new Token(4, Token.Type.KEY, 1, 1),
			new Token(1, Token.Type.SPACE_WS, 1, 5),
			new Token(5, Token.Type.VALUE_OR_IMPORT, 1, 6)
		);

		assertEquals(expected, Token.tokenize("key: value").tokens());
	}

	@Test
	public void simpleKeyAndVariableValue() {
		var expected = List.of(
				new Token(4, Token.Type.KEY, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 5),
				new Token(6, Token.Type.VALUE_OR_IMPORT, 1, 6)
		);

		assertEquals(expected, Token.tokenize("key: $value").tokens());
	}

	@Test
	public void variableKeyAndValue() {
		var expected = List.of(
				new Token(5, Token.Type.KEY, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 6),
				new Token(5, Token.Type.VALUE_OR_IMPORT, 1, 7)
		);

		assertEquals(expected, Token.tokenize("$key: value").tokens());
	}

	@Test
	public void variableKeyAndVariableValue() {
		var expected = List.of(
				new Token(5, Token.Type.KEY, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 6),
				new Token(6, Token.Type.VALUE_OR_IMPORT, 1, 7)
		);

		assertEquals(expected, Token.tokenize("$key: $value").tokens());
	}

	@Test
	public void variableKeyAndNumberValue() {
		var expected = List.of(
				new Token(16, Token.Type.KEY, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 17),
				new Token(2, Token.Type.VALUE_OR_IMPORT, 1, 18)
		);

		assertEquals(expected, Token.tokenize("meaning_of_life: 42").tokens());
	}
}

final class SignedNumbers {
	@Disabled("Not implemented")
	@Test
	public void positiveSignedNumber() {
		var expected = List.of(
				new Token(16, Token.Type.KEY, 1, 1),
				new Token(1, Token.Type.SPACE_WS, 1, 17),
				new Token(3, Token.Type.INTEGER_OR_FLOATING_WITH_SIGN, 1, 18)
		);

		assertEquals(expected, Token.tokenize("meaning_of_life: +42").tokens());
	}
}
