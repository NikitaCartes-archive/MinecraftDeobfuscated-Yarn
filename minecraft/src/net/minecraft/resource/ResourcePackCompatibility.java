package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public enum ResourcePackCompatibility {
	field_14223("old"),
	field_14220("new"),
	field_14224("compatible");

	private final TextComponent notification;
	private final TextComponent confirmMessage;

	private ResourcePackCompatibility(String string2) {
		this.notification = new TranslatableTextComponent("resourcePack.incompatible." + string2);
		this.confirmMessage = new TranslatableTextComponent("resourcePack.incompatible.confirm." + string2);
	}

	public boolean isCompatible() {
		return this == field_14224;
	}

	public static ResourcePackCompatibility from(int i) {
		if (i < SharedConstants.getGameVersion().getPackVersion()) {
			return field_14223;
		} else {
			return i > SharedConstants.getGameVersion().getPackVersion() ? field_14220 : field_14224;
		}
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getNotification() {
		return this.notification;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getConfirmMessage() {
		return this.confirmMessage;
	}
}
