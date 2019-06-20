package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_317 {
	public int field_1972;
	public float field_1970;
	public float field_1969;
	private long field_1971;
	private final float field_1968;

	public class_317(float f, long l) {
		this.field_1968 = 1000.0F / f;
		this.field_1971 = l;
	}

	public void method_1658(long l) {
		this.field_1969 = (float)(l - this.field_1971) / this.field_1968;
		this.field_1971 = l;
		this.field_1970 = this.field_1970 + this.field_1969;
		this.field_1972 = (int)this.field_1970;
		this.field_1970 = this.field_1970 - (float)this.field_1972;
	}
}
