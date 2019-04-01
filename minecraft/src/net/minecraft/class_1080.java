package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1080 {
	private final int field_5341;
	private final int field_5340;

	public class_1080(int i) {
		this(i, -1);
	}

	public class_1080(int i, int j) {
		this.field_5341 = i;
		this.field_5340 = j;
	}

	public boolean method_4689() {
		return this.field_5340 == -1;
	}

	public int method_4691() {
		return this.field_5340;
	}

	public int method_4690() {
		return this.field_5341;
	}
}
