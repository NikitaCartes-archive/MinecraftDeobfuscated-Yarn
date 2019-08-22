package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CodEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart field_3353;
	private final ModelPart field_3355;
	private final ModelPart field_3354;
	private final ModelPart field_3352;
	private final ModelPart field_3351;
	private final ModelPart field_3356;
	private final ModelPart field_3350;

	public CodEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 22;
		this.field_3353 = new ModelPart(this, 0, 0);
		this.field_3353.addCuboid(-1.0F, -2.0F, 0.0F, 2, 4, 7);
		this.field_3353.setRotationPoint(0.0F, 22.0F, 0.0F);
		this.field_3354 = new ModelPart(this, 11, 0);
		this.field_3354.addCuboid(-1.0F, -2.0F, -3.0F, 2, 4, 3);
		this.field_3354.setRotationPoint(0.0F, 22.0F, 0.0F);
		this.field_3352 = new ModelPart(this, 0, 0);
		this.field_3352.addCuboid(-1.0F, -2.0F, -1.0F, 2, 3, 1);
		this.field_3352.setRotationPoint(0.0F, 22.0F, -3.0F);
		this.field_3351 = new ModelPart(this, 22, 1);
		this.field_3351.addCuboid(-2.0F, 0.0F, -1.0F, 2, 0, 2);
		this.field_3351.setRotationPoint(-1.0F, 23.0F, 0.0F);
		this.field_3351.roll = (float) (-Math.PI / 4);
		this.field_3356 = new ModelPart(this, 22, 4);
		this.field_3356.addCuboid(0.0F, 0.0F, -1.0F, 2, 0, 2);
		this.field_3356.setRotationPoint(1.0F, 23.0F, 0.0F);
		this.field_3356.roll = (float) (Math.PI / 4);
		this.field_3350 = new ModelPart(this, 22, 3);
		this.field_3350.addCuboid(0.0F, -2.0F, 0.0F, 0, 4, 4);
		this.field_3350.setRotationPoint(0.0F, 22.0F, 7.0F);
		this.field_3355 = new ModelPart(this, 20, -6);
		this.field_3355.addCuboid(0.0F, -1.0F, -1.0F, 0, 1, 6);
		this.field_3355.setRotationPoint(0.0F, 20.0F, 0.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3353.render(k);
		this.field_3354.render(k);
		this.field_3352.render(k);
		this.field_3351.render(k);
		this.field_3356.render(k);
		this.field_3350.render(k);
		this.field_3355.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		float l = 1.0F;
		if (!entity.isInsideWater()) {
			l = 1.5F;
		}

		this.field_3350.yaw = -l * 0.45F * MathHelper.sin(0.6F * h);
	}
}
