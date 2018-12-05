package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BlankGlyph;

@Environment(EnvType.CLIENT)
public class class_376 implements class_390 {
	@Nullable
	@Override
	public class_383 method_2040(char c) {
		return BlankGlyph.INSTANCE;
	}
}
