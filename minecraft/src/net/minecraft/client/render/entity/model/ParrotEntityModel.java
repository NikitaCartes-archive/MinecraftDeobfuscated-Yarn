package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ParrotEntityModel extends CompositeEntityModel<ParrotEntity> {
	private final ModelPart field_3458;
	private final ModelPart field_3460;
	private final ModelPart field_3459;
	private final ModelPart field_3455;
	private final ModelPart field_3452;
	private final ModelPart field_3461;
	private final ModelPart field_3451;
	private final ModelPart field_3453;
	private final ModelPart field_3456;
	private final ModelPart field_3450;
	private final ModelPart field_3457;

	public ParrotEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.field_3458 = new ModelPart(this, 2, 8);
		this.field_3458.addCuboid(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F);
		this.field_3458.setPivot(0.0F, 16.5F, -3.0F);
		this.field_3460 = new ModelPart(this, 22, 1);
		this.field_3460.addCuboid(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F);
		this.field_3460.setPivot(0.0F, 21.07F, 1.16F);
		this.field_3459 = new ModelPart(this, 19, 8);
		this.field_3459.addCuboid(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F);
		this.field_3459.setPivot(1.5F, 16.94F, -2.76F);
		this.field_3455 = new ModelPart(this, 19, 8);
		this.field_3455.addCuboid(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F);
		this.field_3455.setPivot(-1.5F, 16.94F, -2.76F);
		this.field_3452 = new ModelPart(this, 2, 2);
		this.field_3452.addCuboid(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F);
		this.field_3452.setPivot(0.0F, 15.69F, -2.76F);
		this.field_3461 = new ModelPart(this, 10, 0);
		this.field_3461.addCuboid(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F);
		this.field_3461.setPivot(0.0F, -2.0F, -1.0F);
		this.field_3452.addChild(this.field_3461);
		this.field_3451 = new ModelPart(this, 11, 7);
		this.field_3451.addCuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.field_3451.setPivot(0.0F, -0.5F, -1.5F);
		this.field_3452.addChild(this.field_3451);
		this.field_3453 = new ModelPart(this, 16, 7);
		this.field_3453.addCuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.field_3453.setPivot(0.0F, -1.75F, -2.45F);
		this.field_3452.addChild(this.field_3453);
		this.field_3456 = new ModelPart(this, 2, 18);
		this.field_3456.addCuboid(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F);
		this.field_3456.setPivot(0.0F, -2.15F, 0.15F);
		this.field_3452.addChild(this.field_3456);
		this.field_3450 = new ModelPart(this, 14, 18);
		this.field_3450.addCuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.field_3450.setPivot(1.0F, 22.0F, -1.05F);
		this.field_3457 = new ModelPart(this, 14, 18);
		this.field_3457.addCuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.field_3457.setPivot(-1.0F, 22.0F, -1.05F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_3458, this.field_3459, this.field_3455, this.field_3460, this.field_3452, this.field_3450, this.field_3457);
	}

	public void method_17112(ParrotEntity parrotEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17111(getPose(parrotEntity), parrotEntity.age, f, g, h, i, j);
	}

	public void method_17108(ParrotEntity parrotEntity, float f, float g, float h) {
		this.method_17110(getPose(parrotEntity));
	}

	public void method_17106(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k, float l, int m) {
		this.method_17110(ParrotEntityModel.Pose.ON_SHOULDER);
		this.method_17111(ParrotEntityModel.Pose.ON_SHOULDER, m, f, g, 0.0F, h, k);
		this.getParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, l, i, j, null));
	}

	private void method_17111(ParrotEntityModel.Pose pose, int i, float f, float g, float h, float j, float k) {
		this.field_3452.pitch = k * (float) (Math.PI / 180.0);
		this.field_3452.yaw = j * (float) (Math.PI / 180.0);
		this.field_3452.roll = 0.0F;
		this.field_3452.pivotX = 0.0F;
		this.field_3458.pivotX = 0.0F;
		this.field_3460.pivotX = 0.0F;
		this.field_3455.pivotX = -1.5F;
		this.field_3459.pivotX = 1.5F;
		switch (pose) {
			case SITTING:
				break;
			case PARTY:
				float l = MathHelper.cos((float)i);
				float m = MathHelper.sin((float)i);
				this.field_3452.pivotX = l;
				this.field_3452.pivotY = 15.69F + m;
				this.field_3452.pitch = 0.0F;
				this.field_3452.yaw = 0.0F;
				this.field_3452.roll = MathHelper.sin((float)i) * 0.4F;
				this.field_3458.pivotX = l;
				this.field_3458.pivotY = 16.5F + m;
				this.field_3459.roll = -0.0873F - h;
				this.field_3459.pivotX = 1.5F + l;
				this.field_3459.pivotY = 16.94F + m;
				this.field_3455.roll = 0.0873F + h;
				this.field_3455.pivotX = -1.5F + l;
				this.field_3455.pivotY = 16.94F + m;
				this.field_3460.pivotX = l;
				this.field_3460.pivotY = 21.07F + m;
				break;
			case STANDING:
				this.field_3450.pitch = this.field_3450.pitch + MathHelper.cos(f * 0.6662F) * 1.4F * g;
				this.field_3457.pitch = this.field_3457.pitch + MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			case FLYING:
			case ON_SHOULDER:
			default:
				float n = h * 0.3F;
				this.field_3452.pivotY = 15.69F + n;
				this.field_3460.pitch = 1.015F + MathHelper.cos(f * 0.6662F) * 0.3F * g;
				this.field_3460.pivotY = 21.07F + n;
				this.field_3458.pivotY = 16.5F + n;
				this.field_3459.roll = -0.0873F - h;
				this.field_3459.pivotY = 16.94F + n;
				this.field_3455.roll = 0.0873F + h;
				this.field_3455.pivotY = 16.94F + n;
				this.field_3450.pivotY = 22.0F + n;
				this.field_3457.pivotY = 22.0F + n;
		}
	}

	private void method_17110(ParrotEntityModel.Pose pose) {
		this.field_3456.pitch = -0.2214F;
		this.field_3458.pitch = 0.4937F;
		this.field_3459.pitch = -0.6981F;
		this.field_3459.yaw = (float) -Math.PI;
		this.field_3455.pitch = -0.6981F;
		this.field_3455.yaw = (float) -Math.PI;
		this.field_3450.pitch = -0.0299F;
		this.field_3457.pitch = -0.0299F;
		this.field_3450.pivotY = 22.0F;
		this.field_3457.pivotY = 22.0F;
		this.field_3450.roll = 0.0F;
		this.field_3457.roll = 0.0F;
		switch (pose) {
			case SITTING:
				float f = 1.9F;
				this.field_3452.pivotY = 17.59F;
				this.field_3460.pitch = 1.5388988F;
				this.field_3460.pivotY = 22.97F;
				this.field_3458.pivotY = 18.4F;
				this.field_3459.roll = -0.0873F;
				this.field_3459.pivotY = 18.84F;
				this.field_3455.roll = 0.0873F;
				this.field_3455.pivotY = 18.84F;
				this.field_3450.pivotY++;
				this.field_3457.pivotY++;
				this.field_3450.pitch++;
				this.field_3457.pitch++;
				break;
			case PARTY:
				this.field_3450.roll = (float) (-Math.PI / 9);
				this.field_3457.roll = (float) (Math.PI / 9);
			case STANDING:
			case ON_SHOULDER:
			default:
				break;
			case FLYING:
				this.field_3450.pitch += (float) (Math.PI * 2.0 / 9.0);
				this.field_3457.pitch += (float) (Math.PI * 2.0 / 9.0);
		}
	}

	private static ParrotEntityModel.Pose getPose(ParrotEntity parrotEntity) {
		if (parrotEntity.getSongPlaying()) {
			return ParrotEntityModel.Pose.PARTY;
		} else if (parrotEntity.isSitting()) {
			return ParrotEntityModel.Pose.SITTING;
		} else {
			return parrotEntity.isInAir() ? ParrotEntityModel.Pose.FLYING : ParrotEntityModel.Pose.STANDING;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Pose {
		FLYING,
		STANDING,
		SITTING,
		PARTY,
		ON_SHOULDER;
	}
}
