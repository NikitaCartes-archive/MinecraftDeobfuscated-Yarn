package net.minecraft.util;

import net.minecraft.text.Text;

/**
 * An exception that has a user-friendly {@link Text} attached.
 */
public class TextifiedException extends Exception {
	private final Text messageText;

	public TextifiedException(Text messageText) {
		super(messageText.getString());
		this.messageText = messageText;
	}

	public TextifiedException(Text messageText, Throwable cause) {
		super(messageText.getString(), cause);
		this.messageText = messageText;
	}

	/**
	 * {@return the exception's message text}
	 */
	public Text getMessageText() {
		return this.messageText;
	}
}
