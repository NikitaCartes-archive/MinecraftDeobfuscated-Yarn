package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class PillagerEntityModel extends EvilVillagerEntityModel {
	public PillagerEntityModel(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.setRotationAngles(f, g, h, i, j, k, entity);
		this.field_3422.render(k);
		this.field_3425.render(k);
		this.field_3420.render(k);
		this.field_3418.render(k);
		this.field_3426.render(k);
		this.field_3417.render(k);
	}
}
