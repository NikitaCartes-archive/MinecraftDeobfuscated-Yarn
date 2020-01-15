package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RabbitEntityModel<T extends RabbitEntity> extends EntityModel<T> {
	private final ModelPart leftFoot = new ModelPart(this, 26, 24);
	private final ModelPart rightFoot;
	private final ModelPart leftBackLeg;
	private final ModelPart rightBackLeg;
	private final ModelPart torso;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart head;
	private final ModelPart rightEar;
	private final ModelPart leftEar;
	private final ModelPart tail;
	private final ModelPart nose;
	private float field_3531;

	public RabbitEntityModel() {
		this.leftFoot.addCuboid(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F);
		this.leftFoot.setPivot(3.0F, 17.5F, 3.7F);
		this.leftFoot.mirror = true;
		this.method_2827(this.leftFoot, 0.0F, 0.0F, 0.0F);
		this.rightFoot = new ModelPart(this, 8, 24);
		this.rightFoot.addCuboid(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F);
		this.rightFoot.setPivot(-3.0F, 17.5F, 3.7F);
		this.rightFoot.mirror = true;
		this.method_2827(this.rightFoot, 0.0F, 0.0F, 0.0F);
		this.leftBackLeg = new ModelPart(this, 30, 15);
		this.leftBackLeg.addCuboid(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F);
		this.leftBackLeg.setPivot(3.0F, 17.5F, 3.7F);
		this.leftBackLeg.mirror = true;
		this.method_2827(this.leftBackLeg, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.rightBackLeg = new ModelPart(this, 16, 15);
		this.rightBackLeg.addCuboid(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F);
		this.rightBackLeg.setPivot(-3.0F, 17.5F, 3.7F);
		this.rightBackLeg.mirror = true;
		this.method_2827(this.rightBackLeg, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.torso = new ModelPart(this, 0, 0);
		this.torso.addCuboid(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F);
		this.torso.setPivot(0.0F, 19.0F, 8.0F);
		this.torso.mirror = true;
		this.method_2827(this.torso, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.leftFrontLeg = new ModelPart(this, 8, 15);
		this.leftFrontLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F);
		this.leftFrontLeg.setPivot(3.0F, 17.0F, -1.0F);
		this.leftFrontLeg.mirror = true;
		this.method_2827(this.leftFrontLeg, (float) (-Math.PI / 18), 0.0F, 0.0F);
		this.rightFrontLeg = new ModelPart(this, 0, 15);
		this.rightFrontLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F);
		this.rightFrontLeg.setPivot(-3.0F, 17.0F, -1.0F);
		this.rightFrontLeg.mirror = true;
		this.method_2827(this.rightFrontLeg, (float) (-Math.PI / 18), 0.0F, 0.0F);
		this.head = new ModelPart(this, 32, 0);
		this.head.addCuboid(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F);
		this.head.setPivot(0.0F, 16.0F, -1.0F);
		this.head.mirror = true;
		this.method_2827(this.head, 0.0F, 0.0F, 0.0F);
		this.rightEar = new ModelPart(this, 52, 0);
		this.rightEar.addCuboid(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F);
		this.rightEar.setPivot(0.0F, 16.0F, -1.0F);
		this.rightEar.mirror = true;
		this.method_2827(this.rightEar, 0.0F, (float) (-Math.PI / 12), 0.0F);
		this.leftEar = new ModelPart(this, 58, 0);
		this.leftEar.addCuboid(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F);
		this.leftEar.setPivot(0.0F, 16.0F, -1.0F);
		this.leftEar.mirror = true;
		this.method_2827(this.leftEar, 0.0F, (float) (Math.PI / 12), 0.0F);
		this.tail = new ModelPart(this, 52, 6);
		this.tail.addCuboid(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F);
		this.tail.setPivot(0.0F, 20.0F, 7.0F);
		this.tail.mirror = true;
		this.method_2827(this.tail, -0.3490659F, 0.0F, 0.0F);
		this.nose = new ModelPart(this, 32, 9);
		this.nose.addCuboid(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F);
		this.nose.setPivot(0.0F, 16.0F, -1.0F);
		this.nose.mirror = true;
		this.method_2827(this.nose, 0.0F, 0.0F, 0.0F);
	}

	private void method_2827(ModelPart modelPart, float f, float g, float h) {
		modelPart.pitch = f;
		modelPart.yaw = g;
		modelPart.roll = h;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		if (this.child) {
			float f = 1.5F;
			matrices.push();
			matrices.scale(0.56666666F, 0.56666666F, 0.56666666F);
			matrices.translate(0.0, 1.375, 0.125);
			ImmutableList.of(this.head, this.leftEar, this.rightEar, this.nose)
				.forEach(modelPart -> modelPart.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha));
			matrices.pop();
			matrices.push();
			matrices.scale(0.4F, 0.4F, 0.4F);
			matrices.translate(0.0, 2.25, 0.0);
			ImmutableList.of(this.leftFoot, this.rightFoot, this.leftBackLeg, this.rightBackLeg, this.torso, this.leftFrontLeg, this.rightFrontLeg, this.tail)
				.forEach(modelPart -> modelPart.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha));
			matrices.pop();
		} else {
			matrices.push();
			matrices.scale(0.6F, 0.6F, 0.6F);
			matrices.translate(0.0, 1.0, 0.0);
			ImmutableList.of(
					this.leftFoot,
					this.rightFoot,
					this.leftBackLeg,
					this.rightBackLeg,
					this.torso,
					this.leftFrontLeg,
					this.rightFrontLeg,
					this.head,
					this.rightEar,
					this.leftEar,
					this.tail,
					this.nose
				)
				.forEach(modelPart -> modelPart.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha));
			matrices.pop();
		}
	}

	public void setAngles(T rabbitEntity, float f, float g, float h, float i, float j) {
		float k = h - (float)rabbitEntity.age;
		this.nose.pitch = j * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.rightEar.pitch = j * (float) (Math.PI / 180.0);
		this.leftEar.pitch = j * (float) (Math.PI / 180.0);
		this.nose.yaw = i * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.rightEar.yaw = this.nose.yaw - (float) (Math.PI / 12);
		this.leftEar.yaw = this.nose.yaw + (float) (Math.PI / 12);
		this.field_3531 = MathHelper.sin(rabbitEntity.method_6605(k) * (float) Math.PI);
		this.leftBackLeg.pitch = (this.field_3531 * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
		this.rightBackLeg.pitch = (this.field_3531 * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
		this.leftFoot.pitch = this.field_3531 * 50.0F * (float) (Math.PI / 180.0);
		this.rightFoot.pitch = this.field_3531 * 50.0F * (float) (Math.PI / 180.0);
		this.leftFrontLeg.pitch = (this.field_3531 * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
		this.rightFrontLeg.pitch = (this.field_3531 * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
	}

	public void animateModel(T rabbitEntity, float f, float g, float h) {
		super.animateModel(rabbitEntity, f, g, h);
		this.field_3531 = MathHelper.sin(rabbitEntity.method_6605(h) * (float) Math.PI);
	}
}
