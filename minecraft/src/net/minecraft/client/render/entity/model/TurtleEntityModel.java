package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TurtleEntityModel<T extends TurtleEntity> extends QuadrupedEntityModel<T> {
	private final ModelPart field_3594;

	public TurtleEntityModel(float f) {
		super(12, f, true, 120.0F, 0.0F, 9.0F, 6.0F, 120);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 3, 0);
		this.head.addCuboid(-3.0F, -1.0F, -3.0F, 6.0F, 5.0F, 6.0F, 0.0F);
		this.head.setPivot(0.0F, 19.0F, -10.0F);
		this.body = new ModelPart(this);
		this.body.setTextureOffset(7, 37).addCuboid(-9.5F, 3.0F, -10.0F, 19.0F, 20.0F, 6.0F, 0.0F);
		this.body.setTextureOffset(31, 1).addCuboid(-5.5F, 3.0F, -13.0F, 11.0F, 18.0F, 3.0F, 0.0F);
		this.body.setPivot(0.0F, 11.0F, -10.0F);
		this.field_3594 = new ModelPart(this);
		this.field_3594.setTextureOffset(70, 33).addCuboid(-4.5F, 3.0F, -14.0F, 9.0F, 18.0F, 1.0F, 0.0F);
		this.field_3594.setPivot(0.0F, 11.0F, -10.0F);
		int i = 1;
		this.leg1 = new ModelPart(this, 1, 23);
		this.leg1.addCuboid(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F, 0.0F);
		this.leg1.setPivot(-3.5F, 22.0F, 11.0F);
		this.leg2 = new ModelPart(this, 1, 12);
		this.leg2.addCuboid(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F, 0.0F);
		this.leg2.setPivot(3.5F, 22.0F, 11.0F);
		this.leg3 = new ModelPart(this, 27, 30);
		this.leg3.addCuboid(-13.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F, 0.0F);
		this.leg3.setPivot(-5.0F, 21.0F, -4.0F);
		this.leg4 = new ModelPart(this, 27, 24);
		this.leg4.addCuboid(0.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F, 0.0F);
		this.leg4.setPivot(5.0F, 21.0F, -4.0F);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.field_3594));
	}

	public void method_17125(T turtleEntity, float f, float g, float h, float i, float j) {
		super.setAngles(turtleEntity, f, g, h, i, j);
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
			float k = turtleEntity.isDiggingSand() ? 4.0F : 1.0F;
			float l = turtleEntity.isDiggingSand() ? 2.0F : 1.0F;
			float m = 5.0F;
			this.leg3.yaw = MathHelper.cos(k * f * 5.0F + (float) Math.PI) * 8.0F * g * l;
			this.leg3.roll = 0.0F;
			this.leg4.yaw = MathHelper.cos(k * f * 5.0F) * 8.0F * g * l;
			this.leg4.roll = 0.0F;
			this.leg1.yaw = MathHelper.cos(f * 5.0F + (float) Math.PI) * 3.0F * g;
			this.leg1.pitch = 0.0F;
			this.leg2.yaw = MathHelper.cos(f * 5.0F) * 3.0F * g;
			this.leg2.pitch = 0.0F;
		}

		this.field_3594.visible = !this.isChild && turtleEntity.hasEgg();
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float r, float g, float b) {
		boolean bl = this.field_3594.visible;
		if (bl) {
			matrixStack.push();
			matrixStack.translate(0.0, -0.08F, 0.0);
		}

		super.render(matrixStack, vertexConsumer, i, j, r, g, b);
		if (bl) {
			matrixStack.pop();
		}
	}
}
