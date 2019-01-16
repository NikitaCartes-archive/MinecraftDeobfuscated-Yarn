package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class class_522 extends class_521 {
	public class_522(MinecraftClient minecraftClient, int i, int j) {
		super(minecraftClient, i, j);
	}

	@Override
	protected String method_2689() {
		return I18n.translate("resourcePack.available.title");
	}
}
