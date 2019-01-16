package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityModel<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_3556;

	public ShulkerBulletEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.field_3556 = new Cuboid(this);
		this.field_3556.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -1.0F, 8, 8, 2, 0.0F);
		this.field_3556.setTextureOffset(0, 10).addBox(-1.0F, -4.0F, -4.0F, 2, 8, 8, 0.0F);
		this.field_3556.setTextureOffset(20, 0).addBox(-4.0F, -1.0F, -4.0F, 8, 2, 8, 0.0F);
		this.field_3556.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3556.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(entity, f, g, h, i, j, k);
		this.field_3556.yaw = i * (float) (Math.PI / 180.0);
		this.field_3556.pitch = j * (float) (Math.PI / 180.0);
	}
}
