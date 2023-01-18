package net.minecraft.resource;

import net.minecraft.SharedConstants;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum ResourcePackCompatibility {
	TOO_OLD("old"),
	TOO_NEW("new"),
	COMPATIBLE("compatible");

	private final Text notification;
	private final Text confirmMessage;

	private ResourcePackCompatibility(String translationSuffix) {
		this.notification = Text.translatable("pack.incompatible." + translationSuffix).formatted(Formatting.GRAY);
		this.confirmMessage = Text.translatable("pack.incompatible.confirm." + translationSuffix);
	}

	public boolean isCompatible() {
		return this == COMPATIBLE;
	}

	public static ResourcePackCompatibility from(int packVersion, ResourceType type) {
		int i = SharedConstants.getGameVersion().getResourceVersion(type);
		if (packVersion < i) {
			return TOO_OLD;
		} else {
			return packVersion > i ? TOO_NEW : COMPATIBLE;
		}
	}

	public Text getNotification() {
		return this.notification;
	}

	public Text getConfirmMessage() {
		return this.confirmMessage;
	}
}
