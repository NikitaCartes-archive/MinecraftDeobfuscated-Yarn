package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;

@Environment(EnvType.CLIENT)
public interface class_3882 {
	Cuboid method_2838();

	default void method_17148(float f) {
		this.method_2838().method_2847(f);
	}
}
