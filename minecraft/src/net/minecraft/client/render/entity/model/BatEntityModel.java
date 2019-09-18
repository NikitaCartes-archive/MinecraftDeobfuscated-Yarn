package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BatEntityModel extends EntityModel<BatEntity> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart rightWingTip;
	private final ModelPart leftWingTip;

	public BatEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
		ModelPart modelPart = new ModelPart(this, 24, 0);
		modelPart.addCuboid(-4.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F);
		this.head.addChild(modelPart);
		ModelPart modelPart2 = new ModelPart(this, 24, 0);
		modelPart2.mirror = true;
		modelPart2.addCuboid(1.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F);
		this.head.addChild(modelPart2);
		this.body = new ModelPart(this, 0, 16);
		this.body.addCuboid(-3.0F, 4.0F, -3.0F, 6.0F, 12.0F, 6.0F);
		this.body.setTextureOffset(0, 34).addCuboid(-5.0F, 16.0F, 0.0F, 10.0F, 6.0F, 1.0F);
		this.rightWing = new ModelPart(this, 42, 0);
		this.rightWing.addCuboid(-12.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F);
		this.rightWingTip = new ModelPart(this, 24, 16);
		this.rightWingTip.setRotationPoint(-12.0F, 1.0F, 1.5F);
		this.rightWingTip.addCuboid(-8.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F);
		this.leftWing = new ModelPart(this, 42, 0);
		this.leftWing.mirror = true;
		this.leftWing.addCuboid(2.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F);
		this.leftWingTip = new ModelPart(this, 24, 16);
		this.leftWingTip.mirror = true;
		this.leftWingTip.setRotationPoint(12.0F, 1.0F, 1.5F);
		this.leftWingTip.addCuboid(0.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F);
		this.body.addChild(this.rightWing);
		this.body.addChild(this.leftWing);
		this.rightWing.addChild(this.rightWingTip);
		this.leftWing.addChild(this.leftWingTip);
	}

	public void method_17068(BatEntity batEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17069(batEntity, f, g, h, i, j, k);
		this.head.render(k);
		this.body.render(k);
	}

	public void method_17069(BatEntity batEntity, float f, float g, float h, float i, float j, float k) {
		if (batEntity.isRoosting()) {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = (float) Math.PI - i * (float) (Math.PI / 180.0);
			this.head.roll = (float) Math.PI;
			this.head.setRotationPoint(0.0F, -2.0F, 0.0F);
			this.rightWing.setRotationPoint(-3.0F, 0.0F, 3.0F);
			this.leftWing.setRotationPoint(3.0F, 0.0F, 3.0F);
			this.body.pitch = (float) Math.PI;
			this.rightWing.pitch = (float) (-Math.PI / 20);
			this.rightWing.yaw = (float) (-Math.PI * 2.0 / 5.0);
			this.rightWingTip.yaw = -1.7278761F;
			this.leftWing.pitch = this.rightWing.pitch;
			this.leftWing.yaw = -this.rightWing.yaw;
			this.leftWingTip.yaw = -this.rightWingTip.yaw;
		} else {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = i * (float) (Math.PI / 180.0);
			this.head.roll = 0.0F;
			this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.rightWing.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.leftWing.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.body.pitch = (float) (Math.PI / 4) + MathHelper.cos(h * 0.1F) * 0.15F;
			this.body.yaw = 0.0F;
			this.rightWing.yaw = MathHelper.cos(h * 1.3F) * (float) Math.PI * 0.25F;
			this.leftWing.yaw = -this.rightWing.yaw;
			this.rightWingTip.yaw = this.rightWing.yaw * 0.5F;
			this.leftWingTip.yaw = -this.rightWing.yaw * 0.5F;
		}
	}
}
