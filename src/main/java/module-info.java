/**
 * The Gura configuration language.
 *
 * <p>This module defines the tokenizer, parser, ast and a writer for Gura configuration language on the Java SE
 * platform.
 */
module gura.jvm {
	requires java.base;

	exports me.i509.gura.token;
}
