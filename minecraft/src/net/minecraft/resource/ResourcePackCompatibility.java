package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum ResourcePackCompatibility {
	TOO_OLD("old"),
	TOO_NEW("new"),
	field_14224("compatible");

	private final Text notification;
	private final Text confirmMessage;

	private ResourcePackCompatibility(String string2) {
		this.notification = new TranslatableText("resourcePack.incompatible." + string2);
		this.confirmMessage = new TranslatableText("resourcePack.incompatible.confirm." + string2);
	}

	public boolean isCompatible() {
		return this == field_14224;
	}

	public static ResourcePackCompatibility from(int i) {
		if (i < SharedConstants.getGameVersion().getPackVersion()) {
			return TOO_OLD;
		} else {
			return i > SharedConstants.getGameVersion().getPackVersion() ? TOO_NEW : field_14224;
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
