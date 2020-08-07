package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum ResourcePackCompatibility {
	field_14223("old"),
	field_14220("new"),
	field_14224("compatible");

	private final Text notification;
	private final Text confirmMessage;

	private ResourcePackCompatibility(String translationSuffix) {
		this.notification = new TranslatableText("pack.incompatible." + translationSuffix);
		this.confirmMessage = new TranslatableText("pack.incompatible.confirm." + translationSuffix);
	}

	public boolean isCompatible() {
		return this == field_14224;
	}

	public static ResourcePackCompatibility from(int packVersion) {
		if (packVersion < SharedConstants.getGameVersion().getPackVersion()) {
			return field_14223;
		} else {
			return packVersion > SharedConstants.getGameVersion().getPackVersion() ? field_14220 : field_14224;
		}
	}

	@Environment(EnvType.CLIENT)
	public Text getNotification() {
		return this.notification;
	}

	@Environment(EnvType.CLIENT)
	public Text getConfirmMessage() {
		return this.confirmMessage;
	}
}
