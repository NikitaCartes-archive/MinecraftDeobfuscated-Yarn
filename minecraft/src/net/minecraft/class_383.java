package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Glyph;

@Environment(EnvType.CLIENT)
public interface class_383 extends Glyph {
	int method_2031();

	int method_2032();

	void method_2030(int i, int j);

	boolean method_2033();

	float method_2035();

	default float method_2034() {
		return this.getBearingX();
	}

	default float method_2027() {
		return this.method_2034() + (float)this.method_2031() / this.method_2035();
	}

	default float method_2028() {
		return this.method_15976();
	}

	default float method_2029() {
		return this.method_2028() + (float)this.method_2032() / this.method_2035();
	}

	default float method_15976() {
		return 3.0F;
	}
}
