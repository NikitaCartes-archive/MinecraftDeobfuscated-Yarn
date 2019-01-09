package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_379 {
	float getAdvance();

	default float method_16798(boolean bl) {
		return this.getAdvance() + (bl ? this.method_16799() : 0.0F);
	}

	default float method_16797() {
		return 0.0F;
	}

	default float method_16799() {
		return 1.0F;
	}

	default float method_16800() {
		return 1.0F;
	}
}
