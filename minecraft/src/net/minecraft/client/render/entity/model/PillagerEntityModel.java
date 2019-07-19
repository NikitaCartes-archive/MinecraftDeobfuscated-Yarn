package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.IllagerEntity;

@Environment(EnvType.CLIENT)
public class PillagerEntityModel<T extends IllagerEntity> extends IllagerEntityModel<T> {
	public PillagerEntityModel(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	@Override
	public void render(T illagerEntity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(illagerEntity, f, g, h, i, j, k);
		this.field_3422.render(k);
		this.field_3425.render(k);
		this.field_3420.render(k);
		this.field_3418.render(k);
		this.field_3426.render(k);
		this.field_3417.render(k);
	}
}
