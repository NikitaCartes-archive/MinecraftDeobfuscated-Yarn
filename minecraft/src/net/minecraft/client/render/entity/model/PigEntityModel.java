package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class PigEntityModel<T extends Entity> extends QuadrupedEntityModel<T> {
	public PigEntityModel() {
		this(0.0F);
	}

	public PigEntityModel(float f) {
		super(6, f);
		this.head.setTextureOffset(16, 16).addCuboid(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F, f);
		this.field_3540 = 4.0F;
	}
}
