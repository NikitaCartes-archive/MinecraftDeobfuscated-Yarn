package net.minecraft;

import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_7406 {
	public static final TagKey<PaintingMotive> PLACEABLE = method_43384("placeable");

	private class_7406() {
	}

	private static TagKey<PaintingMotive> method_43384(String string) {
		return TagKey.of(Registry.MOTIVE_KEY, new Identifier(string));
	}
}
