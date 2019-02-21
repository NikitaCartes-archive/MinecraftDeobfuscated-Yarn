package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3968;
import net.minecraft.entity.mob.GiantEntity;

@Environment(EnvType.CLIENT)
public class GiantEntityModel extends class_3968<GiantEntity> {
	public GiantEntityModel() {
		this(0.0F, false);
	}

	public GiantEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	public boolean method_17792(GiantEntity giantEntity) {
		return false;
	}
}
