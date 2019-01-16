package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.GiantEntity;

@Environment(EnvType.CLIENT)
public class class_3969 extends class_3968<GiantEntity> {
	public class_3969() {
		this(0.0F, false);
	}

	public class_3969(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	public boolean method_17792(GiantEntity giantEntity) {
		return false;
	}
}
