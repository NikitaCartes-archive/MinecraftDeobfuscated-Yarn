package net.minecraft.world.level.storage;

import net.minecraft.text.Text;

public class LevelStorageException extends RuntimeException {
	private final Text field_38981;

	public LevelStorageException(Text text) {
		super(text.getString());
		this.field_38981 = text;
	}

	public Text method_43416() {
		return this.field_38981;
	}
}
