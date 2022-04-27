package net.minecraft.world.level.storage;

import net.minecraft.text.Text;

public class LevelStorageException extends RuntimeException {
	private final Text messageText;

	public LevelStorageException(Text messageText) {
		super(messageText.getString());
		this.messageText = messageText;
	}

	public Text getMessageText() {
		return this.messageText;
	}
}
