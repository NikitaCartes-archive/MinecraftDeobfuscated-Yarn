package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SmallPufferfishEntityModel<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_3505;
	private final Cuboid field_3507;
	private final Cuboid field_3506;
	private final Cuboid field_3504;
	private final Cuboid field_3503;
	private final Cuboid field_3508;

	public SmallPufferfishEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 23;
		this.field_3505 = new Cuboid(this, 0, 27);
		this.field_3505.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3);
		this.field_3505.setRotationPoint(0.0F, 23.0F, 0.0F);
		this.field_3507 = new Cuboid(this, 24, 6);
		this.field_3507.addBox(-1.5F, 0.0F, -1.5F, 1, 1, 1);
		this.field_3507.setRotationPoint(0.0F, 20.0F, 0.0F);
		this.field_3506 = new Cuboid(this, 28, 6);
		this.field_3506.addBox(0.5F, 0.0F, -1.5F, 1, 1, 1);
		this.field_3506.setRotationPoint(0.0F, 20.0F, 0.0F);
		this.field_3508 = new Cuboid(this, -3, 0);
		this.field_3508.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3);
		this.field_3508.setRotationPoint(0.0F, 22.0F, 1.5F);
		this.field_3504 = new Cuboid(this, 25, 0);
		this.field_3504.addBox(-1.0F, 0.0F, 0.0F, 1, 0, 2);
		this.field_3504.setRotationPoint(-1.5F, 22.0F, -1.5F);
		this.field_3503 = new Cuboid(this, 25, 0);
		this.field_3503.addBox(0.0F, 0.0F, 0.0F, 1, 0, 2);
		this.field_3503.setRotationPoint(1.5F, 22.0F, -1.5F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3505.render(k);
		this.field_3507.render(k);
		this.field_3506.render(k);
		this.field_3508.render(k);
		this.field_3504.render(k);
		this.field_3503.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_3504.roll = -0.2F + 0.4F * MathHelper.sin(h * 0.2F);
		this.field_3503.roll = 0.2F - 0.4F * MathHelper.sin(h * 0.2F);
	}
}
