package net.minecraft.util;

import net.minecraft.text.Text;

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

	public Text getMessageText() {
		return this.messageText;
	}
}
