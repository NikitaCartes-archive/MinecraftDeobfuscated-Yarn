package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_623<T extends class_1642> extends class_3968<T> {
	public class_623() {
		this(0.0F, false);
	}

	public class_623(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	protected class_623(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	public boolean method_17793(T arg) {
		return arg.method_6510();
	}
}
