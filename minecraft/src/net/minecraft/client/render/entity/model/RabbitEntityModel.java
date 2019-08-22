package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RabbitEntityModel<T extends RabbitEntity> extends EntityModel<T> {
	private final ModelPart field_3525 = new ModelPart(this, 26, 24);
	private final ModelPart field_3532;
	private final ModelPart field_3526;
	private final ModelPart field_3522;
	private final ModelPart field_3528;
	private final ModelPart field_3527;
	private final ModelPart field_3521;
	private final ModelPart field_3529;
	private final ModelPart field_3523;
	private final ModelPart field_3520;
	private final ModelPart field_3524;
	private final ModelPart field_3530;
	private float field_3531;

	public RabbitEntityModel() {
		this.field_3525.addCuboid(-1.0F, 5.5F, -3.7F, 2, 1, 7);
		this.field_3525.setRotationPoint(3.0F, 17.5F, 3.7F);
		this.field_3525.mirror = true;
		this.method_2827(this.field_3525, 0.0F, 0.0F, 0.0F);
		this.field_3532 = new ModelPart(this, 8, 24);
		this.field_3532.addCuboid(-1.0F, 5.5F, -3.7F, 2, 1, 7);
		this.field_3532.setRotationPoint(-3.0F, 17.5F, 3.7F);
		this.field_3532.mirror = true;
		this.method_2827(this.field_3532, 0.0F, 0.0F, 0.0F);
		this.field_3526 = new ModelPart(this, 30, 15);
		this.field_3526.addCuboid(-1.0F, 0.0F, 0.0F, 2, 4, 5);
		this.field_3526.setRotationPoint(3.0F, 17.5F, 3.7F);
		this.field_3526.mirror = true;
		this.method_2827(this.field_3526, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.field_3522 = new ModelPart(this, 16, 15);
		this.field_3522.addCuboid(-1.0F, 0.0F, 0.0F, 2, 4, 5);
		this.field_3522.setRotationPoint(-3.0F, 17.5F, 3.7F);
		this.field_3522.mirror = true;
		this.method_2827(this.field_3522, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.field_3528 = new ModelPart(this, 0, 0);
		this.field_3528.addCuboid(-3.0F, -2.0F, -10.0F, 6, 5, 10);
		this.field_3528.setRotationPoint(0.0F, 19.0F, 8.0F);
		this.field_3528.mirror = true;
		this.method_2827(this.field_3528, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.field_3527 = new ModelPart(this, 8, 15);
		this.field_3527.addCuboid(-1.0F, 0.0F, -1.0F, 2, 7, 2);
		this.field_3527.setRotationPoint(3.0F, 17.0F, -1.0F);
		this.field_3527.mirror = true;
		this.method_2827(this.field_3527, (float) (-Math.PI / 18), 0.0F, 0.0F);
		this.field_3521 = new ModelPart(this, 0, 15);
		this.field_3521.addCuboid(-1.0F, 0.0F, -1.0F, 2, 7, 2);
		this.field_3521.setRotationPoint(-3.0F, 17.0F, -1.0F);
		this.field_3521.mirror = true;
		this.method_2827(this.field_3521, (float) (-Math.PI / 18), 0.0F, 0.0F);
		this.field_3529 = new ModelPart(this, 32, 0);
		this.field_3529.addCuboid(-2.5F, -4.0F, -5.0F, 5, 4, 5);
		this.field_3529.setRotationPoint(0.0F, 16.0F, -1.0F);
		this.field_3529.mirror = true;
		this.method_2827(this.field_3529, 0.0F, 0.0F, 0.0F);
		this.field_3523 = new ModelPart(this, 52, 0);
		this.field_3523.addCuboid(-2.5F, -9.0F, -1.0F, 2, 5, 1);
		this.field_3523.setRotationPoint(0.0F, 16.0F, -1.0F);
		this.field_3523.mirror = true;
		this.method_2827(this.field_3523, 0.0F, (float) (-Math.PI / 12), 0.0F);
		this.field_3520 = new ModelPart(this, 58, 0);
		this.field_3520.addCuboid(0.5F, -9.0F, -1.0F, 2, 5, 1);
		this.field_3520.setRotationPoint(0.0F, 16.0F, -1.0F);
		this.field_3520.mirror = true;
		this.method_2827(this.field_3520, 0.0F, (float) (Math.PI / 12), 0.0F);
		this.field_3524 = new ModelPart(this, 52, 6);
		this.field_3524.addCuboid(-1.5F, -1.5F, 0.0F, 3, 3, 2);
		this.field_3524.setRotationPoint(0.0F, 20.0F, 7.0F);
		this.field_3524.mirror = true;
		this.method_2827(this.field_3524, -0.3490659F, 0.0F, 0.0F);
		this.field_3530 = new ModelPart(this, 32, 9);
		this.field_3530.addCuboid(-0.5F, -2.5F, -5.5F, 1, 1, 1);
		this.field_3530.setRotationPoint(0.0F, 16.0F, -1.0F);
		this.field_3530.mirror = true;
		this.method_2827(this.field_3530, 0.0F, 0.0F, 0.0F);
	}

	private void method_2827(ModelPart modelPart, float f, float g, float h) {
		modelPart.pitch = f;
		modelPart.yaw = g;
		modelPart.roll = h;
	}

	public void method_17116(T rabbitEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17117(rabbitEntity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 1.5F;
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.56666666F, 0.56666666F, 0.56666666F);
			RenderSystem.translatef(0.0F, 22.0F * k, 2.0F * k);
			this.field_3529.render(k);
			this.field_3520.render(k);
			this.field_3523.render(k);
			this.field_3530.render(k);
			RenderSystem.popMatrix();
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.4F, 0.4F, 0.4F);
			RenderSystem.translatef(0.0F, 36.0F * k, 0.0F);
			this.field_3525.render(k);
			this.field_3532.render(k);
			this.field_3526.render(k);
			this.field_3522.render(k);
			this.field_3528.render(k);
			this.field_3527.render(k);
			this.field_3521.render(k);
			this.field_3524.render(k);
			RenderSystem.popMatrix();
		} else {
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.6F, 0.6F, 0.6F);
			RenderSystem.translatef(0.0F, 16.0F * k, 0.0F);
			this.field_3525.render(k);
			this.field_3532.render(k);
			this.field_3526.render(k);
			this.field_3522.render(k);
			this.field_3528.render(k);
			this.field_3527.render(k);
			this.field_3521.render(k);
			this.field_3529.render(k);
			this.field_3523.render(k);
			this.field_3520.render(k);
			this.field_3524.render(k);
			this.field_3530.render(k);
			RenderSystem.popMatrix();
		}
	}

	public void method_17117(T rabbitEntity, float f, float g, float h, float i, float j, float k) {
		float l = h - (float)rabbitEntity.age;
		this.field_3530.pitch = j * (float) (Math.PI / 180.0);
		this.field_3529.pitch = j * (float) (Math.PI / 180.0);
		this.field_3523.pitch = j * (float) (Math.PI / 180.0);
		this.field_3520.pitch = j * (float) (Math.PI / 180.0);
		this.field_3530.yaw = i * (float) (Math.PI / 180.0);
		this.field_3529.yaw = i * (float) (Math.PI / 180.0);
		this.field_3523.yaw = this.field_3530.yaw - (float) (Math.PI / 12);
		this.field_3520.yaw = this.field_3530.yaw + (float) (Math.PI / 12);
		this.field_3531 = MathHelper.sin(rabbitEntity.method_6605(l) * (float) Math.PI);
		this.field_3526.pitch = (this.field_3531 * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
		this.field_3522.pitch = (this.field_3531 * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
		this.field_3525.pitch = this.field_3531 * 50.0F * (float) (Math.PI / 180.0);
		this.field_3532.pitch = this.field_3531 * 50.0F * (float) (Math.PI / 180.0);
		this.field_3527.pitch = (this.field_3531 * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
		this.field_3521.pitch = (this.field_3531 * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
	}

	public void method_17115(T rabbitEntity, float f, float g, float h) {
		super.animateModel(rabbitEntity, f, g, h);
		this.field_3531 = MathHelper.sin(rabbitEntity.method_6605(h) * (float) Math.PI);
	}
}
