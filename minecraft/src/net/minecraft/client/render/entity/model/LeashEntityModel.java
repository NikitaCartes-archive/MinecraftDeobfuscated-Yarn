package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LeashEntityModel extends Model {
	private final Cuboid field_3431;

	public LeashEntityModel() {
		this(0, 0, 32, 32);
	}

	public LeashEntityModel(int i, int j, int k, int l) {
		this.textureWidth = k;
		this.textureHeight = l;
		this.field_3431 = new Cuboid(this, i, j);
		this.field_3431.addBox(-3.0F, -6.0F, -3.0F, 6, 8, 6, 0.0F);
		this.field_3431.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.setRotationAngles(f, g, h, i, j, k, entity);
		this.field_3431.render(k);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		super.setRotationAngles(f, g, h, i, j, k, entity);
		this.field_3431.yaw = i * (float) (Math.PI / 180.0);
		this.field_3431.pitch = j * (float) (Math.PI / 180.0);
	}
}
