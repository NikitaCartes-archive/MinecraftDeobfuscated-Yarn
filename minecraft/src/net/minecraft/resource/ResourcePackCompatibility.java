package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum ResourcePackCompatibility {
	TOO_OLD("old"),
	TOO_NEW("new"),
	COMPATIBLE("compatible");

	private final Text notification;
	private final Text confirmMessage;

	private ResourcePackCompatibility(String translationSuffix) {
		this.notification = new TranslatableText("pack.incompatible." + translationSuffix);
		this.confirmMessage = new TranslatableText("pack.incompatible.confirm." + translationSuffix);
	}

	public boolean isCompatible() {
		return this == COMPATIBLE;
	}

	public static ResourcePackCompatibility from(int packVersion) {
		if (packVersion < SharedConstants.getGameVersion().getPackVersion()) {
			return TOO_OLD;
		} else {
			return packVersion > SharedConstants.getGameVersion().getPackVersion() ? TOO_NEW : COMPATIBLE;
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
