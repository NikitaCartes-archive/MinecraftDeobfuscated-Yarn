package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_7413 {
	private static final String[] field_38992 = new String[]{"O o o", "o O o", "o o O", "o O o"};
	private static final long field_38993 = 300L;

	public static String method_43449(long l) {
		int i = (int)(l / 300L % (long)field_38992.length);
		return field_38992[i];
	}
}
