package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TurtleEntityModel<T extends TurtleEntity> extends QuadrupedEntityModel<T> {
	private final ModelPart field_3594;

	public TurtleEntityModel(float f) {
		super(12, f);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 3, 0);
		this.head.addCuboid(-3.0F, -1.0F, -3.0F, 6.0F, 5.0F, 6.0F, 0.0F);
		this.head.setRotationPoint(0.0F, 19.0F, -10.0F);
		this.body = new ModelPart(this);
		this.body.setTextureOffset(7, 37).addCuboid(-9.5F, 3.0F, -10.0F, 19.0F, 20.0F, 6.0F, 0.0F);
		this.body.setTextureOffset(31, 1).addCuboid(-5.5F, 3.0F, -13.0F, 11.0F, 18.0F, 3.0F, 0.0F);
		this.body.setRotationPoint(0.0F, 11.0F, -10.0F);
		this.field_3594 = new ModelPart(this);
		this.field_3594.setTextureOffset(70, 33).addCuboid(-4.5F, 3.0F, -14.0F, 9.0F, 18.0F, 1.0F, 0.0F);
		this.field_3594.setRotationPoint(0.0F, 11.0F, -10.0F);
		int i = 1;
		this.leg1 = new ModelPart(this, 1, 23);
		this.leg1.addCuboid(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F, 0.0F);
		this.leg1.setRotationPoint(-3.5F, 22.0F, 11.0F);
		this.leg2 = new ModelPart(this, 1, 12);
		this.leg2.addCuboid(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F, 0.0F);
		this.leg2.setRotationPoint(3.5F, 22.0F, 11.0F);
		this.leg3 = new ModelPart(this, 27, 30);
		this.leg3.addCuboid(-13.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F, 0.0F);
		this.leg3.setRotationPoint(-5.0F, 21.0F, -4.0F);
		this.leg4 = new ModelPart(this, 27, 24);
		this.leg4.addCuboid(0.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F, 0.0F);
		this.leg4.setRotationPoint(5.0F, 21.0F, -4.0F);
	}

	public void method_17124(T turtleEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17125(turtleEntity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 6.0F;
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.16666667F, 0.16666667F, 0.16666667F);
			RenderSystem.translatef(0.0F, 120.0F * k, 0.0F);
			this.head.render(k);
			this.body.render(k);
			this.leg1.render(k);
			this.leg2.render(k);
			this.leg3.render(k);
			this.leg4.render(k);
			RenderSystem.popMatrix();
		} else {
			RenderSystem.pushMatrix();
			if (turtleEntity.hasEgg()) {
				RenderSystem.translatef(0.0F, -0.08F, 0.0F);
			}

			this.head.render(k);
			this.body.render(k);
			RenderSystem.pushMatrix();
			this.leg1.render(k);
			this.leg2.render(k);
			RenderSystem.popMatrix();
			this.leg3.render(k);
			this.leg4.render(k);
			if (turtleEntity.hasEgg()) {
				this.field_3594.render(k);
			}

			RenderSystem.popMatrix();
		}
	}

	public void method_17125(T turtleEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(turtleEntity, f, g, h, i, j, k);
		this.leg1.pitch = MathHelper.cos(f * 0.6662F * 0.6F) * 0.5F * g;
		this.leg2.pitch = MathHelper.cos(f * 0.6662F * 0.6F + (float) Math.PI) * 0.5F * g;
		this.leg3.roll = MathHelper.cos(f * 0.6662F * 0.6F + (float) Math.PI) * 0.5F * g;
		this.leg4.roll = MathHelper.cos(f * 0.6662F * 0.6F) * 0.5F * g;
		this.leg3.pitch = 0.0F;
		this.leg4.pitch = 0.0F;
		this.leg3.yaw = 0.0F;
		this.leg4.yaw = 0.0F;
		this.leg1.yaw = 0.0F;
		this.leg2.yaw = 0.0F;
		this.field_3594.pitch = (float) (Math.PI / 2);
		if (!turtleEntity.isInsideWater() && turtleEntity.onGround) {
			float l = turtleEntity.isDiggingSand() ? 4.0F : 1.0F;
			float m = turtleEntity.isDiggingSand() ? 2.0F : 1.0F;
			float n = 5.0F;
			this.leg3.yaw = MathHelper.cos(l * f * 5.0F + (float) Math.PI) * 8.0F * g * m;
			this.leg3.roll = 0.0F;
			this.leg4.yaw = MathHelper.cos(l * f * 5.0F) * 8.0F * g * m;
			this.leg4.roll = 0.0F;
			this.leg1.yaw = MathHelper.cos(f * 5.0F + (float) Math.PI) * 3.0F * g;
			this.leg1.pitch = 0.0F;
			this.leg2.yaw = MathHelper.cos(f * 5.0F) * 3.0F * g;
			this.leg2.pitch = 0.0F;
		}
	}
}
