package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity> extends TintableAnimalModel<T> {
	private final ModelPart head;
	private final ModelPart field_20788;
	private final ModelPart field_3623;
	private final ModelPart field_3622;
	private final ModelPart field_3620;
	private final ModelPart field_3618;
	private final ModelPart field_3624;
	private final ModelPart field_3617;
	private final ModelPart field_20789;
	private final ModelPart field_3619;

	public WolfEntityModel() {
		float f = 0.0F;
		float g = 13.5F;
		this.head = new ModelPart(this, 0, 0);
		this.head.setPivot(-1.0F, 13.5F, -7.0F);
		this.field_20788 = new ModelPart(this, 0, 0);
		this.field_20788.addCuboid(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F);
		this.head.addChild(this.field_20788);
		this.field_3623 = new ModelPart(this, 18, 14);
		this.field_3623.addCuboid(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, 0.0F);
		this.field_3623.setPivot(0.0F, 14.0F, 2.0F);
		this.field_3619 = new ModelPart(this, 21, 0);
		this.field_3619.addCuboid(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, 0.0F);
		this.field_3619.setPivot(-1.0F, 14.0F, 2.0F);
		this.field_3622 = new ModelPart(this, 0, 18);
		this.field_3622.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
		this.field_3622.setPivot(-2.5F, 16.0F, 7.0F);
		this.field_3620 = new ModelPart(this, 0, 18);
		this.field_3620.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
		this.field_3620.setPivot(0.5F, 16.0F, 7.0F);
		this.field_3618 = new ModelPart(this, 0, 18);
		this.field_3618.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
		this.field_3618.setPivot(-2.5F, 16.0F, -4.0F);
		this.field_3624 = new ModelPart(this, 0, 18);
		this.field_3624.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
		this.field_3624.setPivot(0.5F, 16.0F, -4.0F);
		this.field_3617 = new ModelPart(this, 9, 18);
		this.field_3617.setPivot(-1.0F, 12.0F, 8.0F);
		this.field_20789 = new ModelPart(this, 9, 18);
		this.field_20789.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
		this.field_3617.addChild(this.field_20789);
		this.field_20788.setTextureOffset(16, 14).addCuboid(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
		this.field_20788.setTextureOffset(16, 14).addCuboid(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
		this.field_20788.setTextureOffset(0, 10).addCuboid(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F, 0.0F);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.field_3623, this.field_3622, this.field_3620, this.field_3618, this.field_3624, this.field_3617, this.field_3619);
	}

	public void method_17131(T wolfEntity, float f, float g, float h) {
		if (wolfEntity.isAngry()) {
			this.field_3617.yaw = 0.0F;
		} else {
			this.field_3617.yaw = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		if (wolfEntity.isSitting()) {
			this.field_3619.setPivot(-1.0F, 16.0F, -3.0F);
			this.field_3619.pitch = (float) (Math.PI * 2.0 / 5.0);
			this.field_3619.yaw = 0.0F;
			this.field_3623.setPivot(0.0F, 18.0F, 0.0F);
			this.field_3623.pitch = (float) (Math.PI / 4);
			this.field_3617.setPivot(-1.0F, 21.0F, 6.0F);
			this.field_3622.setPivot(-2.5F, 22.7F, 2.0F);
			this.field_3622.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.field_3620.setPivot(0.5F, 22.7F, 2.0F);
			this.field_3620.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.field_3618.pitch = 5.811947F;
			this.field_3618.setPivot(-2.49F, 17.0F, -4.0F);
			this.field_3624.pitch = 5.811947F;
			this.field_3624.setPivot(0.51F, 17.0F, -4.0F);
		} else {
			this.field_3623.setPivot(0.0F, 14.0F, 2.0F);
			this.field_3623.pitch = (float) (Math.PI / 2);
			this.field_3619.setPivot(-1.0F, 14.0F, -3.0F);
			this.field_3619.pitch = this.field_3623.pitch;
			this.field_3617.setPivot(-1.0F, 12.0F, 8.0F);
			this.field_3622.setPivot(-2.5F, 16.0F, 7.0F);
			this.field_3620.setPivot(0.5F, 16.0F, 7.0F);
			this.field_3618.setPivot(-2.5F, 16.0F, -4.0F);
			this.field_3624.setPivot(0.5F, 16.0F, -4.0F);
			this.field_3622.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
			this.field_3620.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.field_3618.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.field_3624.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		this.field_20788.roll = wolfEntity.getBegAnimationProgress(h) + wolfEntity.getShakeAnimationProgress(h, 0.0F);
		this.field_3619.roll = wolfEntity.getShakeAnimationProgress(h, -0.08F);
		this.field_3623.roll = wolfEntity.getShakeAnimationProgress(h, -0.16F);
		this.field_20789.roll = wolfEntity.getShakeAnimationProgress(h, -0.2F);
	}

	public void method_17133(T wolfEntity, float f, float g, float h, float i, float j) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.field_3617.pitch = h;
	}
}
