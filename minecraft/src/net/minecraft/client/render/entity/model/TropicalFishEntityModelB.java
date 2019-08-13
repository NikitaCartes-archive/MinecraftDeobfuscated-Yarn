package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityModelB<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_3597;
	private final Cuboid field_3599;
	private final Cuboid field_3598;
	private final Cuboid field_3596;
	private final Cuboid field_3595;
	private final Cuboid field_3600;

	public TropicalFishEntityModelB() {
		this(0.0F);
	}

	public TropicalFishEntityModelB(float f) {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 19;
		this.field_3597 = new Cuboid(this, 0, 20);
		this.field_3597.addBox(-1.0F, -3.0F, -3.0F, 2, 6, 6, f);
		this.field_3597.setRotationPoint(0.0F, 19.0F, 0.0F);
		this.field_3599 = new Cuboid(this, 21, 16);
		this.field_3599.addBox(0.0F, -3.0F, 0.0F, 0, 6, 5, f);
		this.field_3599.setRotationPoint(0.0F, 19.0F, 3.0F);
		this.field_3598 = new Cuboid(this, 2, 16);
		this.field_3598.addBox(-2.0F, 0.0F, 0.0F, 2, 2, 0, f);
		this.field_3598.setRotationPoint(-1.0F, 20.0F, 0.0F);
		this.field_3598.yaw = (float) (Math.PI / 4);
		this.field_3596 = new Cuboid(this, 2, 12);
		this.field_3596.addBox(0.0F, 0.0F, 0.0F, 2, 2, 0, f);
		this.field_3596.setRotationPoint(1.0F, 20.0F, 0.0F);
		this.field_3596.yaw = (float) (-Math.PI / 4);
		this.field_3595 = new Cuboid(this, 20, 11);
		this.field_3595.addBox(0.0F, -4.0F, 0.0F, 0, 4, 6, f);
		this.field_3595.setRotationPoint(0.0F, 16.0F, -3.0F);
		this.field_3600 = new Cuboid(this, 20, 21);
		this.field_3600.addBox(0.0F, 0.0F, 0.0F, 0, 4, 6, f);
		this.field_3600.setRotationPoint(0.0F, 22.0F, -3.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3597.render(k);
		this.field_3599.render(k);
		this.field_3598.render(k);
		this.field_3596.render(k);
		this.field_3595.render(k);
		this.field_3600.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		float l = 1.0F;
		if (!entity.isInsideWater()) {
			l = 1.5F;
		}

		this.field_3599.yaw = -l * 0.45F * MathHelper.sin(0.6F * h);
	}
}
