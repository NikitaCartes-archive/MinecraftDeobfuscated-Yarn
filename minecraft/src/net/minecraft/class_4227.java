package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.openal.AL10;

@Environment(EnvType.CLIENT)
public class class_4227 {
	public static final class_243 field_18905 = new class_243(0.0, 1.0, 0.0);
	private float field_18906 = 1.0F;

	public void method_19671(class_243 arg) {
		AL10.alListener3f(4100, (float)arg.field_1352, (float)arg.field_1351, (float)arg.field_1350);
	}

	public void method_19672(class_243 arg, class_243 arg2) {
		AL10.alListenerfv(
			4111,
			new float[]{(float)arg.field_1352, (float)arg.field_1351, (float)arg.field_1350, (float)arg2.field_1352, (float)arg2.field_1351, (float)arg2.field_1350}
		);
	}

	public void method_19670(float f) {
		AL10.alListenerf(4106, f);
		this.field_18906 = f;
	}

	public float method_19669() {
		return this.field_18906;
	}

	public void method_19673() {
		this.method_19671(class_243.field_1353);
		this.method_19672(new class_243(0.0, 0.0, -1.0), field_18905);
	}
}
