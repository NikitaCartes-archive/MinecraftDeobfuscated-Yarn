package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity> extends TintableAnimalModel<T> {
	private final ModelPart head;
	private final ModelPart field_20788;
	private final ModelPart torso;
	private final ModelPart field_27538;
	private final ModelPart field_27539;
	private final ModelPart field_27540;
	private final ModelPart field_27541;
	private final ModelPart tail;
	private final ModelPart field_20789;
	private final ModelPart neck;

	public WolfEntityModel(ModelPart modelPart) {
		this.head = modelPart.method_32086("head");
		this.field_20788 = this.head.method_32086("real_head");
		this.torso = modelPart.method_32086("body");
		this.neck = modelPart.method_32086("upper_body");
		this.field_27538 = modelPart.method_32086("right_hind_leg");
		this.field_27539 = modelPart.method_32086("left_hind_leg");
		this.field_27540 = modelPart.method_32086("right_front_leg");
		this.field_27541 = modelPart.method_32086("left_front_leg");
		this.tail = modelPart.method_32086("tail");
		this.field_20789 = this.tail.method_32086("real_tail");
	}

	public static class_5607 method_32068() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		float f = 13.5F;
		class_5610 lv3 = lv2.method_32117("head", class_5606.method_32108(), class_5603.method_32090(-1.0F, 13.5F, -7.0F));
		lv3.method_32117(
			"real_head",
			class_5606.method_32108()
				.method_32101(0, 0)
				.method_32097(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F)
				.method_32101(16, 14)
				.method_32097(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F)
				.method_32101(16, 14)
				.method_32097(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F)
				.method_32101(0, 10)
				.method_32097(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F),
			class_5603.field_27701
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108().method_32101(18, 14).method_32097(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F),
			class_5603.method_32091(0.0F, 14.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		lv2.method_32117(
			"upper_body",
			class_5606.method_32108().method_32101(21, 0).method_32097(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F),
			class_5603.method_32091(-1.0F, 14.0F, -3.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		class_5606 lv4 = class_5606.method_32108().method_32101(0, 18).method_32097(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F);
		lv2.method_32117("right_hind_leg", lv4, class_5603.method_32090(-2.5F, 16.0F, 7.0F));
		lv2.method_32117("left_hind_leg", lv4, class_5603.method_32090(0.5F, 16.0F, 7.0F));
		lv2.method_32117("right_front_leg", lv4, class_5603.method_32090(-2.5F, 16.0F, -4.0F));
		lv2.method_32117("left_front_leg", lv4, class_5603.method_32090(0.5F, 16.0F, -4.0F));
		class_5610 lv5 = lv2.method_32117("tail", class_5606.method_32108(), class_5603.method_32091(-1.0F, 12.0F, 8.0F, (float) (Math.PI / 5), 0.0F, 0.0F));
		lv5.method_32117("real_tail", class_5606.method_32108().method_32101(9, 18).method_32097(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), class_5603.field_27701);
		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.field_27538, this.field_27539, this.field_27540, this.field_27541, this.tail, this.neck);
	}

	public void animateModel(T wolfEntity, float f, float g, float h) {
		if (wolfEntity.hasAngerTime()) {
			this.tail.yaw = 0.0F;
		} else {
			this.tail.yaw = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		if (wolfEntity.isInSittingPose()) {
			this.neck.setPivot(-1.0F, 16.0F, -3.0F);
			this.neck.pitch = (float) (Math.PI * 2.0 / 5.0);
			this.neck.yaw = 0.0F;
			this.torso.setPivot(0.0F, 18.0F, 0.0F);
			this.torso.pitch = (float) (Math.PI / 4);
			this.tail.setPivot(-1.0F, 21.0F, 6.0F);
			this.field_27538.setPivot(-2.5F, 22.7F, 2.0F);
			this.field_27538.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.field_27539.setPivot(0.5F, 22.7F, 2.0F);
			this.field_27539.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.field_27540.pitch = 5.811947F;
			this.field_27540.setPivot(-2.49F, 17.0F, -4.0F);
			this.field_27541.pitch = 5.811947F;
			this.field_27541.setPivot(0.51F, 17.0F, -4.0F);
		} else {
			this.torso.setPivot(0.0F, 14.0F, 2.0F);
			this.torso.pitch = (float) (Math.PI / 2);
			this.neck.setPivot(-1.0F, 14.0F, -3.0F);
			this.neck.pitch = this.torso.pitch;
			this.tail.setPivot(-1.0F, 12.0F, 8.0F);
			this.field_27538.setPivot(-2.5F, 16.0F, 7.0F);
			this.field_27539.setPivot(0.5F, 16.0F, 7.0F);
			this.field_27540.setPivot(-2.5F, 16.0F, -4.0F);
			this.field_27541.setPivot(0.5F, 16.0F, -4.0F);
			this.field_27538.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
			this.field_27539.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.field_27540.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.field_27541.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		this.field_20788.roll = wolfEntity.getBegAnimationProgress(h) + wolfEntity.getShakeAnimationProgress(h, 0.0F);
		this.neck.roll = wolfEntity.getShakeAnimationProgress(h, -0.08F);
		this.torso.roll = wolfEntity.getShakeAnimationProgress(h, -0.16F);
		this.field_20789.roll = wolfEntity.getShakeAnimationProgress(h, -0.2F);
	}

	public void setAngles(T wolfEntity, float f, float g, float h, float i, float j) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.tail.pitch = h;
	}
}
