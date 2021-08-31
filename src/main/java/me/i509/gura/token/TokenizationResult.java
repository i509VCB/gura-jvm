package me.i509.gura.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public final class TokenizationResult {
	private final List<Token> tokens;
	private final List<Token> invalidTokens;

	TokenizationResult(List<Token> tokens) {
		this.tokens = tokens;
		this.invalidTokens = new ArrayList<>();

		for (var token : this.tokens) {
			if (!token.valid()) {
				this.invalidTokens.add(token);
			}
		}
	}

	/**
	 * Returns a list of all tokens.
	 *
	 * <p>Beware that this list may contain some invalid tokens, make sure to guard any calls to this method with
	 * {@link #success()} if you do not want invalid tokens.
	 *
	 * @return an immutable list of all tokens
	 */
	public List<Token> tokens() {
		return Collections.unmodifiableList(this.tokens);
	}

	/**
	 * Returns a list of all invalid tokens.
	 *
	 * <p>If all tokens were valid, this list would be empty.
	 *
	 * @return an immutable list of invalid tokens
	 */
	public List<Token> invalidTokens() {
		return Collections.unmodifiableList(this.invalidTokens);
	}

	/**
	 * Returns whether the tokenization process was successful.
	 *
	 * <p>If this returns false, some invalid tokens may be in the list of returned tokens.
	 *
	 * @return {@code true} if no invalid tokens were encountered
	 * @see #invalidTokens()
	 */
	public boolean success() {
		return this.invalidTokens.isEmpty();
	}

	@Override
	public String toString() {
		return new StringJoiner(",", TokenizationResult.class.getSimpleName() + "[", "]")
				.add("valid=" + this.success())
				.add("tokens=" + this.tokens)
				.toString();
	}
}
