package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FoxEntityModel<T extends FoxEntity> extends AnimalModel<T> {
	public final ModelPart head;
	private final ModelPart torso;
	private final ModelPart field_27415;
	private final ModelPart field_27416;
	private final ModelPart field_27417;
	private final ModelPart field_27418;
	private final ModelPart tail;
	private float legPitchModifier;

	public FoxEntityModel(ModelPart modelPart) {
		super(true, 8.0F, 3.35F);
		this.head = modelPart.method_32086("head");
		this.torso = modelPart.method_32086("body");
		this.field_27415 = modelPart.method_32086("right_hind_leg");
		this.field_27416 = modelPart.method_32086("left_hind_leg");
		this.field_27417 = modelPart.method_32086("right_front_leg");
		this.field_27418 = modelPart.method_32086("left_front_leg");
		this.tail = this.torso.method_32086("tail");
	}

	public static class_5607 method_31999() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		class_5610 lv3 = lv2.method_32117(
			"head", class_5606.method_32108().method_32101(1, 5).method_32097(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F), class_5603.method_32090(-1.0F, 16.5F, -3.0F)
		);
		lv3.method_32117("right_ear", class_5606.method_32108().method_32101(8, 1).method_32097(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), class_5603.field_27701);
		lv3.method_32117("left_ear", class_5606.method_32108().method_32101(15, 1).method_32097(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), class_5603.field_27701);
		lv3.method_32117("nose", class_5606.method_32108().method_32101(6, 18).method_32097(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F), class_5603.field_27701);
		class_5610 lv4 = lv2.method_32117(
			"body",
			class_5606.method_32108().method_32101(24, 15).method_32097(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F),
			class_5603.method_32091(0.0F, 16.0F, -6.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		class_5605 lv5 = new class_5605(0.001F);
		class_5606 lv6 = class_5606.method_32108().method_32101(4, 24).method_32098(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, lv5);
		class_5606 lv7 = class_5606.method_32108().method_32101(13, 24).method_32098(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, lv5);
		lv2.method_32117("right_hind_leg", lv7, class_5603.method_32090(-5.0F, 17.5F, 7.0F));
		lv2.method_32117("left_hind_leg", lv6, class_5603.method_32090(-1.0F, 17.5F, 7.0F));
		lv2.method_32117("right_front_leg", lv7, class_5603.method_32090(-5.0F, 17.5F, 0.0F));
		lv2.method_32117("left_front_leg", lv6, class_5603.method_32090(-1.0F, 17.5F, 0.0F));
		lv4.method_32117(
			"tail",
			class_5606.method_32108().method_32101(30, 0).method_32097(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F),
			class_5603.method_32091(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 48, 32);
	}

	public void animateModel(T foxEntity, float f, float g, float h) {
		this.torso.pitch = (float) (Math.PI / 2);
		this.tail.pitch = -0.05235988F;
		this.field_27415.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.field_27416.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_27417.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_27418.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.head.setPivot(-1.0F, 16.5F, -3.0F);
		this.head.yaw = 0.0F;
		this.head.roll = foxEntity.getHeadRoll(h);
		this.field_27415.visible = true;
		this.field_27416.visible = true;
		this.field_27417.visible = true;
		this.field_27418.visible = true;
		this.torso.setPivot(0.0F, 16.0F, -6.0F);
		this.torso.roll = 0.0F;
		this.field_27415.setPivot(-5.0F, 17.5F, 7.0F);
		this.field_27416.setPivot(-1.0F, 17.5F, 7.0F);
		if (foxEntity.isInSneakingPose()) {
			this.torso.pitch = 1.6755161F;
			float i = foxEntity.getBodyRotationHeightOffset(h);
			this.torso.setPivot(0.0F, 16.0F + foxEntity.getBodyRotationHeightOffset(h), -6.0F);
			this.head.setPivot(-1.0F, 16.5F + i, -3.0F);
			this.head.yaw = 0.0F;
		} else if (foxEntity.isSleeping()) {
			this.torso.roll = (float) (-Math.PI / 2);
			this.torso.setPivot(0.0F, 21.0F, -6.0F);
			this.tail.pitch = (float) (-Math.PI * 5.0 / 6.0);
			if (this.child) {
				this.tail.pitch = -2.1816616F;
				this.torso.setPivot(0.0F, 21.0F, -2.0F);
			}

			this.head.setPivot(1.0F, 19.49F, -3.0F);
			this.head.pitch = 0.0F;
			this.head.yaw = (float) (-Math.PI * 2.0 / 3.0);
			this.head.roll = 0.0F;
			this.field_27415.visible = false;
			this.field_27416.visible = false;
			this.field_27417.visible = false;
			this.field_27418.visible = false;
		} else if (foxEntity.isSitting()) {
			this.torso.pitch = (float) (Math.PI / 6);
			this.torso.setPivot(0.0F, 9.0F, -3.0F);
			this.tail.pitch = (float) (Math.PI / 4);
			this.tail.setPivot(-4.0F, 15.0F, -2.0F);
			this.head.setPivot(-1.0F, 10.0F, -0.25F);
			this.head.pitch = 0.0F;
			this.head.yaw = 0.0F;
			if (this.child) {
				this.head.setPivot(-1.0F, 13.0F, -3.75F);
			}

			this.field_27415.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.field_27415.setPivot(-5.0F, 21.5F, 6.75F);
			this.field_27416.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.field_27416.setPivot(-1.0F, 21.5F, 6.75F);
			this.field_27417.pitch = (float) (-Math.PI / 12);
			this.field_27418.pitch = (float) (-Math.PI / 12);
		}
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.field_27415, this.field_27416, this.field_27417, this.field_27418);
	}

	public void setAngles(T foxEntity, float f, float g, float h, float i, float j) {
		if (!foxEntity.isSleeping() && !foxEntity.isWalking() && !foxEntity.isInSneakingPose()) {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = i * (float) (Math.PI / 180.0);
		}

		if (foxEntity.isSleeping()) {
			this.head.pitch = 0.0F;
			this.head.yaw = (float) (-Math.PI * 2.0 / 3.0);
			this.head.roll = MathHelper.cos(h * 0.027F) / 22.0F;
		}

		if (foxEntity.isInSneakingPose()) {
			float k = MathHelper.cos(h) * 0.01F;
			this.torso.yaw = k;
			this.field_27415.roll = k;
			this.field_27416.roll = k;
			this.field_27417.roll = k / 2.0F;
			this.field_27418.roll = k / 2.0F;
		}

		if (foxEntity.isWalking()) {
			float k = 0.1F;
			this.legPitchModifier += 0.67F;
			this.field_27415.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F) * 0.1F;
			this.field_27416.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F + (float) Math.PI) * 0.1F;
			this.field_27417.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F + (float) Math.PI) * 0.1F;
			this.field_27418.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F) * 0.1F;
		}
	}
}
