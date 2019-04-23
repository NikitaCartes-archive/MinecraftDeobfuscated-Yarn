package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum ResourcePackCompatibility {
	TOO_OLD("old"),
	TOO_NEW("new"),
	field_14224("compatible");

	private final Component notification;
	private final Component confirmMessage;

	private ResourcePackCompatibility(String string2) {
		this.notification = new TranslatableComponent("resourcePack.incompatible." + string2);
		this.confirmMessage = new TranslatableComponent("resourcePack.incompatible.confirm." + string2);
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
	public Component getNotification() {
		return this.notification;
	}

	@Environment(EnvType.CLIENT)
	public Component getConfirmMessage() {
		return this.confirmMessage;
	}
}
