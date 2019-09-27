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
		super(6, f, false, 4.0F, 4.0F, 2.0F, 2.0F, 24);
		this.head.setTextureOffset(16, 16).addCuboid(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F, f);
	}
}
