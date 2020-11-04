package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_5603 {
	public static final class_5603 field_27701 = method_32091(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	public final float field_27702;
	public final float field_27703;
	public final float field_27704;
	public final float field_27705;
	public final float field_27706;
	public final float field_27707;

	private class_5603(float f, float g, float h, float i, float j, float k) {
		this.field_27702 = f;
		this.field_27703 = g;
		this.field_27704 = h;
		this.field_27705 = i;
		this.field_27706 = j;
		this.field_27707 = k;
	}

	public static class_5603 method_32090(float f, float g, float h) {
		return method_32091(f, g, h, 0.0F, 0.0F, 0.0F);
	}

	public static class_5603 method_32092(float f, float g, float h) {
		return method_32091(0.0F, 0.0F, 0.0F, f, g, h);
	}

	public static class_5603 method_32091(float f, float g, float h, float i, float j, float k) {
		return new class_5603(f, g, h, i, j, k);
	}
}
