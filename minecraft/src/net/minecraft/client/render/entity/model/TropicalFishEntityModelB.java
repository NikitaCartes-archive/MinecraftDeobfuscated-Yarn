package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityModelB<T extends Entity> extends EntityModel<T> {
	private final ModelPart field_3597;
	private final ModelPart field_3599;
	private final ModelPart field_3598;
	private final ModelPart field_3596;
	private final ModelPart field_3595;
	private final ModelPart field_3600;

	public TropicalFishEntityModelB() {
		this(0.0F);
	}

	public TropicalFishEntityModelB(float f) {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 19;
		this.field_3597 = new ModelPart(this, 0, 20);
		this.field_3597.addCuboid(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, f);
		this.field_3597.setRotationPoint(0.0F, 19.0F, 0.0F);
		this.field_3599 = new ModelPart(this, 21, 16);
		this.field_3599.addCuboid(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 5.0F, f);
		this.field_3599.setRotationPoint(0.0F, 19.0F, 3.0F);
		this.field_3598 = new ModelPart(this, 2, 16);
		this.field_3598.addCuboid(-2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, f);
		this.field_3598.setRotationPoint(-1.0F, 20.0F, 0.0F);
		this.field_3598.yaw = (float) (Math.PI / 4);
		this.field_3596 = new ModelPart(this, 2, 12);
		this.field_3596.addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, f);
		this.field_3596.setRotationPoint(1.0F, 20.0F, 0.0F);
		this.field_3596.yaw = (float) (-Math.PI / 4);
		this.field_3595 = new ModelPart(this, 20, 11);
		this.field_3595.addCuboid(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 6.0F, f);
		this.field_3595.setRotationPoint(0.0F, 16.0F, -3.0F);
		this.field_3600 = new ModelPart(this, 20, 21);
		this.field_3600.addCuboid(0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 6.0F, f);
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
