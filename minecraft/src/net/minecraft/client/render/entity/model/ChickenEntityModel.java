package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChickenEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart leftWing;
	private final ModelPart rightWing;
	private final ModelPart beak;
	private final ModelPart wattle;

	public ChickenEntityModel() {
		int i = 16;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
		this.head.setRotationPoint(0.0F, 15.0F, -4.0F);
		this.beak = new ModelPart(this, 14, 0);
		this.beak.addCuboid(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
		this.beak.setRotationPoint(0.0F, 15.0F, -4.0F);
		this.wattle = new ModelPart(this, 14, 4);
		this.wattle.addCuboid(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
		this.wattle.setRotationPoint(0.0F, 15.0F, -4.0F);
		this.body = new ModelPart(this, 0, 9);
		this.body.addCuboid(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
		this.body.setRotationPoint(0.0F, 16.0F, 0.0F);
		this.leftLeg = new ModelPart(this, 26, 0);
		this.leftLeg.addCuboid(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.leftLeg.setRotationPoint(-2.0F, 19.0F, 1.0F);
		this.rightLeg = new ModelPart(this, 26, 0);
		this.rightLeg.addCuboid(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.rightLeg.setRotationPoint(1.0F, 19.0F, 1.0F);
		this.leftWing = new ModelPart(this, 24, 13);
		this.leftWing.addCuboid(0.0F, 0.0F, -3.0F, 1, 4, 6);
		this.leftWing.setRotationPoint(-4.0F, 13.0F, 0.0F);
		this.rightWing = new ModelPart(this, 24, 13);
		this.rightWing.addCuboid(-1.0F, 0.0F, -3.0F, 1, 4, 6);
		this.rightWing.setRotationPoint(4.0F, 13.0F, 0.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 2.0F;
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, 5.0F * k, 2.0F * k);
			this.head.render(k);
			this.beak.render(k);
			this.wattle.render(k);
			RenderSystem.popMatrix();
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.5F, 0.5F, 0.5F);
			RenderSystem.translatef(0.0F, 24.0F * k, 0.0F);
			this.body.render(k);
			this.leftLeg.render(k);
			this.rightLeg.render(k);
			this.leftWing.render(k);
			this.rightWing.render(k);
			RenderSystem.popMatrix();
		} else {
			this.head.render(k);
			this.beak.render(k);
			this.wattle.render(k);
			this.body.render(k);
			this.leftLeg.render(k);
			this.rightLeg.render(k);
			this.leftWing.render(k);
			this.rightWing.render(k);
		}
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.beak.pitch = this.head.pitch;
		this.beak.yaw = this.head.yaw;
		this.wattle.pitch = this.head.pitch;
		this.wattle.yaw = this.head.yaw;
		this.body.pitch = (float) (Math.PI / 2);
		this.leftLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.rightLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.leftWing.roll = h;
		this.rightWing.roll = -h;
	}
}
