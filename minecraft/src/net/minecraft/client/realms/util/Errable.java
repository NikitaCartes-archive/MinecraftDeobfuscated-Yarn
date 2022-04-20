package net.minecraft.client.realms.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface Errable {
	void error(Text errorMessage);

	default void error(String errorMessage) {
		this.error(Text.literal(errorMessage));
	}
}
