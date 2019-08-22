package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BatEntityModel extends EntityModel<BatEntity> {
	private final ModelPart field_3321;
	private final ModelPart field_3323;
	private final ModelPart field_3322;
	private final ModelPart field_3320;
	private final ModelPart field_3319;
	private final ModelPart field_3324;

	public BatEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3321 = new ModelPart(this, 0, 0);
		this.field_3321.addCuboid(-3.0F, -3.0F, -3.0F, 6, 6, 6);
		ModelPart modelPart = new ModelPart(this, 24, 0);
		modelPart.addCuboid(-4.0F, -6.0F, -2.0F, 3, 4, 1);
		this.field_3321.addChild(modelPart);
		ModelPart modelPart2 = new ModelPart(this, 24, 0);
		modelPart2.mirror = true;
		modelPart2.addCuboid(1.0F, -6.0F, -2.0F, 3, 4, 1);
		this.field_3321.addChild(modelPart2);
		this.field_3323 = new ModelPart(this, 0, 16);
		this.field_3323.addCuboid(-3.0F, 4.0F, -3.0F, 6, 12, 6);
		this.field_3323.setTextureOffset(0, 34).addCuboid(-5.0F, 16.0F, 0.0F, 10, 6, 1);
		this.field_3322 = new ModelPart(this, 42, 0);
		this.field_3322.addCuboid(-12.0F, 1.0F, 1.5F, 10, 16, 1);
		this.field_3319 = new ModelPart(this, 24, 16);
		this.field_3319.setRotationPoint(-12.0F, 1.0F, 1.5F);
		this.field_3319.addCuboid(-8.0F, 1.0F, 0.0F, 8, 12, 1);
		this.field_3320 = new ModelPart(this, 42, 0);
		this.field_3320.mirror = true;
		this.field_3320.addCuboid(2.0F, 1.0F, 1.5F, 10, 16, 1);
		this.field_3324 = new ModelPart(this, 24, 16);
		this.field_3324.mirror = true;
		this.field_3324.setRotationPoint(12.0F, 1.0F, 1.5F);
		this.field_3324.addCuboid(0.0F, 1.0F, 0.0F, 8, 12, 1);
		this.field_3323.addChild(this.field_3322);
		this.field_3323.addChild(this.field_3320);
		this.field_3322.addChild(this.field_3319);
		this.field_3320.addChild(this.field_3324);
	}

	public void method_17068(BatEntity batEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17069(batEntity, f, g, h, i, j, k);
		this.field_3321.render(k);
		this.field_3323.render(k);
	}

	public void method_17069(BatEntity batEntity, float f, float g, float h, float i, float j, float k) {
		if (batEntity.isRoosting()) {
			this.field_3321.pitch = j * (float) (Math.PI / 180.0);
			this.field_3321.yaw = (float) Math.PI - i * (float) (Math.PI / 180.0);
			this.field_3321.roll = (float) Math.PI;
			this.field_3321.setRotationPoint(0.0F, -2.0F, 0.0F);
			this.field_3322.setRotationPoint(-3.0F, 0.0F, 3.0F);
			this.field_3320.setRotationPoint(3.0F, 0.0F, 3.0F);
			this.field_3323.pitch = (float) Math.PI;
			this.field_3322.pitch = (float) (-Math.PI / 20);
			this.field_3322.yaw = (float) (-Math.PI * 2.0 / 5.0);
			this.field_3319.yaw = -1.7278761F;
			this.field_3320.pitch = this.field_3322.pitch;
			this.field_3320.yaw = -this.field_3322.yaw;
			this.field_3324.yaw = -this.field_3319.yaw;
		} else {
			this.field_3321.pitch = j * (float) (Math.PI / 180.0);
			this.field_3321.yaw = i * (float) (Math.PI / 180.0);
			this.field_3321.roll = 0.0F;
			this.field_3321.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.field_3322.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.field_3320.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.field_3323.pitch = (float) (Math.PI / 4) + MathHelper.cos(h * 0.1F) * 0.15F;
			this.field_3323.yaw = 0.0F;
			this.field_3322.yaw = MathHelper.cos(h * 1.3F) * (float) Math.PI * 0.25F;
			this.field_3320.yaw = -this.field_3322.yaw;
			this.field_3319.yaw = this.field_3322.yaw * 0.5F;
			this.field_3324.yaw = -this.field_3322.yaw * 0.5F;
		}
	}
}
