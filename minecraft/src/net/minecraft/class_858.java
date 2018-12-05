package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BoundingBox;

@Environment(EnvType.CLIENT)
public class class_858 implements class_856 {
	private final class_857 field_4504;
	private double field_4503;
	private double field_4502;
	private double field_4501;

	public class_858() {
		this(class_855.method_3696());
	}

	public class_858(class_857 arg) {
		this.field_4504 = arg;
	}

	@Override
	public void method_3700(double d, double e, double f) {
		this.field_4503 = d;
		this.field_4502 = e;
		this.field_4501 = f;
	}

	public boolean method_3703(double d, double e, double f, double g, double h, double i) {
		return this.field_4504
			.method_3702(d - this.field_4503, e - this.field_4502, f - this.field_4501, g - this.field_4503, h - this.field_4502, i - this.field_4501);
	}

	@Override
	public boolean method_3699(BoundingBox boundingBox) {
		return this.method_3703(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	}
}
