package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PigEntityModel extends QuadrupedEntityModel {
	public PigEntityModel() {
		this(0.0F);
	}

	public PigEntityModel(float f) {
		super(6, f);
		this.head.setTextureOffset(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1, f);
		this.field_3540 = 4.0F;
	}
}
