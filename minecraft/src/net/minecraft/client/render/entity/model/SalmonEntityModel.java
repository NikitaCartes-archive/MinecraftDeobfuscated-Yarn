package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SalmonEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart field_3546;
	private final ModelPart field_3548;
	private final ModelPart field_3547;
	private final ModelPart field_3545;
	private final ModelPart field_3543;
	private final ModelPart field_3549;
	private final ModelPart field_3542;
	private final ModelPart field_3544;

	public SalmonEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 20;
		this.field_3546 = new ModelPart(this, 0, 0);
		this.field_3546.addCuboid(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
		this.field_3546.setRotationPoint(0.0F, 20.0F, 0.0F);
		this.field_3548 = new ModelPart(this, 0, 13);
		this.field_3548.addCuboid(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
		this.field_3548.setRotationPoint(0.0F, 20.0F, 8.0F);
		this.field_3547 = new ModelPart(this, 22, 0);
		this.field_3547.addCuboid(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F);
		this.field_3547.setRotationPoint(0.0F, 20.0F, 0.0F);
		this.field_3549 = new ModelPart(this, 20, 10);
		this.field_3549.addCuboid(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F);
		this.field_3549.setRotationPoint(0.0F, 0.0F, 8.0F);
		this.field_3548.addChild(this.field_3549);
		this.field_3545 = new ModelPart(this, 2, 1);
		this.field_3545.addCuboid(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F);
		this.field_3545.setRotationPoint(0.0F, -4.5F, 5.0F);
		this.field_3546.addChild(this.field_3545);
		this.field_3543 = new ModelPart(this, 0, 2);
		this.field_3543.addCuboid(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 4.0F);
		this.field_3543.setRotationPoint(0.0F, -4.5F, -1.0F);
		this.field_3548.addChild(this.field_3543);
		this.field_3542 = new ModelPart(this, -4, 0);
		this.field_3542.addCuboid(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
		this.field_3542.setRotationPoint(-1.5F, 21.5F, 0.0F);
		this.field_3542.roll = (float) (-Math.PI / 4);
		this.field_3544 = new ModelPart(this, 0, 0);
		this.field_3544.addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
		this.field_3544.setRotationPoint(1.5F, 21.5F, 0.0F);
		this.field_3544.roll = (float) (Math.PI / 4);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3546.render(k);
		this.field_3548.render(k);
		this.field_3547.render(k);
		this.field_3542.render(k);
		this.field_3544.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		float l = 1.0F;
		float m = 1.0F;
		if (!entity.isInsideWater()) {
			l = 1.3F;
			m = 1.7F;
		}

		this.field_3548.yaw = -l * 0.25F * MathHelper.sin(m * 0.6F * h);
	}
}
