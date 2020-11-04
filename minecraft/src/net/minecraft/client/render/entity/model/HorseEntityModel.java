package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HorseEntityModel<T extends HorseBaseEntity> extends AnimalModel<T> {
	protected final ModelPart torso;
	protected final ModelPart head;
	private final ModelPart field_27425;
	private final ModelPart field_27426;
	private final ModelPart field_27427;
	private final ModelPart field_27428;
	private final ModelPart field_27429;
	private final ModelPart field_27430;
	private final ModelPart field_27431;
	private final ModelPart field_27432;
	private final ModelPart tail;
	private final ModelPart[] field_3304;
	private final ModelPart[] field_3301;

	public HorseEntityModel(ModelPart modelPart) {
		super(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
		this.torso = modelPart.method_32086("body");
		this.head = modelPart.method_32086("head_parts");
		this.field_27425 = modelPart.method_32086("right_hind_leg");
		this.field_27426 = modelPart.method_32086("left_hind_leg");
		this.field_27427 = modelPart.method_32086("right_front_leg");
		this.field_27428 = modelPart.method_32086("left_front_leg");
		this.field_27429 = modelPart.method_32086("right_hind_baby_leg");
		this.field_27430 = modelPart.method_32086("left_hind_baby_leg");
		this.field_27431 = modelPart.method_32086("right_front_baby_leg");
		this.field_27432 = modelPart.method_32086("left_front_baby_leg");
		this.tail = this.torso.method_32086("tail");
		ModelPart modelPart2 = this.torso.method_32086("saddle");
		ModelPart modelPart3 = this.head.method_32086("left_saddle_mouth");
		ModelPart modelPart4 = this.head.method_32086("right_saddle_mouth");
		ModelPart modelPart5 = this.head.method_32086("left_saddle_line");
		ModelPart modelPart6 = this.head.method_32086("right_saddle_line");
		ModelPart modelPart7 = this.head.method_32086("head_saddle");
		ModelPart modelPart8 = this.head.method_32086("mouth_saddle_wrap");
		this.field_3304 = new ModelPart[]{modelPart2, modelPart3, modelPart4, modelPart7, modelPart8};
		this.field_3301 = new ModelPart[]{modelPart5, modelPart6};
	}

	public static class_5609 method_32010(class_5605 arg) {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		class_5610 lv3 = lv2.method_32117(
			"body",
			class_5606.method_32108().method_32101(0, 32).method_32098(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, new class_5605(0.05F)),
			class_5603.method_32090(0.0F, 11.0F, 5.0F)
		);
		class_5610 lv4 = lv2.method_32117(
			"head_parts",
			class_5606.method_32108().method_32101(0, 35).method_32097(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F),
			class_5603.method_32091(0.0F, 4.0F, -12.0F, (float) (Math.PI / 6), 0.0F, 0.0F)
		);
		class_5610 lv5 = lv4.method_32117(
			"head", class_5606.method_32108().method_32101(0, 13).method_32098(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, arg), class_5603.field_27701
		);
		lv4.method_32117("mane", class_5606.method_32108().method_32101(56, 36).method_32098(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, arg), class_5603.field_27701);
		lv4.method_32117(
			"upper_mouth", class_5606.method_32108().method_32101(0, 25).method_32098(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, arg), class_5603.field_27701
		);
		lv2.method_32117(
			"left_hind_leg",
			class_5606.method_32108().method_32101(48, 21).method_32096().method_32098(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, arg),
			class_5603.method_32090(4.0F, 14.0F, 7.0F)
		);
		lv2.method_32117(
			"right_hind_leg",
			class_5606.method_32108().method_32101(48, 21).method_32098(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, arg),
			class_5603.method_32090(-4.0F, 14.0F, 7.0F)
		);
		lv2.method_32117(
			"left_front_leg",
			class_5606.method_32108().method_32101(48, 21).method_32096().method_32098(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, arg),
			class_5603.method_32090(4.0F, 14.0F, -12.0F)
		);
		lv2.method_32117(
			"right_front_leg",
			class_5606.method_32108().method_32101(48, 21).method_32098(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, arg),
			class_5603.method_32090(-4.0F, 14.0F, -12.0F)
		);
		class_5605 lv6 = arg.method_32095(0.0F, 5.5F, 0.0F);
		lv2.method_32117(
			"left_hind_baby_leg",
			class_5606.method_32108().method_32101(48, 21).method_32096().method_32098(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, lv6),
			class_5603.method_32090(4.0F, 14.0F, 7.0F)
		);
		lv2.method_32117(
			"right_hind_baby_leg",
			class_5606.method_32108().method_32101(48, 21).method_32098(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, lv6),
			class_5603.method_32090(-4.0F, 14.0F, 7.0F)
		);
		lv2.method_32117(
			"left_front_baby_leg",
			class_5606.method_32108().method_32101(48, 21).method_32096().method_32098(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, lv6),
			class_5603.method_32090(4.0F, 14.0F, -12.0F)
		);
		lv2.method_32117(
			"right_front_baby_leg",
			class_5606.method_32108().method_32101(48, 21).method_32098(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, lv6),
			class_5603.method_32090(-4.0F, 14.0F, -12.0F)
		);
		lv3.method_32117(
			"tail",
			class_5606.method_32108().method_32101(42, 36).method_32098(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, arg),
			class_5603.method_32091(0.0F, -5.0F, 2.0F, (float) (Math.PI / 6), 0.0F, 0.0F)
		);
		lv3.method_32117(
			"saddle", class_5606.method_32108().method_32101(26, 0).method_32098(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, new class_5605(0.5F)), class_5603.field_27701
		);
		lv4.method_32117(
			"left_saddle_mouth", class_5606.method_32108().method_32101(29, 5).method_32098(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, arg), class_5603.field_27701
		);
		lv4.method_32117(
			"right_saddle_mouth", class_5606.method_32108().method_32101(29, 5).method_32098(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, arg), class_5603.field_27701
		);
		lv4.method_32117(
			"left_saddle_line",
			class_5606.method_32108().method_32101(32, 2).method_32098(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, arg),
			class_5603.method_32092((float) (-Math.PI / 6), 0.0F, 0.0F)
		);
		lv4.method_32117(
			"right_saddle_line",
			class_5606.method_32108().method_32101(32, 2).method_32098(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, arg),
			class_5603.method_32092((float) (-Math.PI / 6), 0.0F, 0.0F)
		);
		lv4.method_32117(
			"head_saddle",
			class_5606.method_32108().method_32101(1, 1).method_32098(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, new class_5605(0.2F)),
			class_5603.field_27701
		);
		lv4.method_32117(
			"mouth_saddle_wrap",
			class_5606.method_32108().method_32101(19, 0).method_32098(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, new class_5605(0.2F)),
			class_5603.field_27701
		);
		lv5.method_32117(
			"left_ear",
			class_5606.method_32108().method_32101(19, 16).method_32098(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new class_5605(-0.001F)),
			class_5603.field_27701
		);
		lv5.method_32117(
			"right_ear",
			class_5606.method_32108().method_32101(19, 16).method_32098(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new class_5605(-0.001F)),
			class_5603.field_27701
		);
		return lv;
	}

	public void setAngles(T horseBaseEntity, float f, float g, float h, float i, float j) {
		boolean bl = horseBaseEntity.isSaddled();
		boolean bl2 = horseBaseEntity.hasPassengers();

		for (ModelPart modelPart : this.field_3304) {
			modelPart.visible = bl;
		}

		for (ModelPart modelPart : this.field_3301) {
			modelPart.visible = bl2 && bl;
		}

		this.torso.pivotY = 11.0F;
	}

	@Override
	public Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(
			this.torso, this.field_27425, this.field_27426, this.field_27427, this.field_27428, this.field_27429, this.field_27430, this.field_27431, this.field_27432
		);
	}

	public void animateModel(T horseBaseEntity, float f, float g, float h) {
		super.animateModel(horseBaseEntity, f, g, h);
		float i = MathHelper.lerpAngle(horseBaseEntity.prevBodyYaw, horseBaseEntity.bodyYaw, h);
		float j = MathHelper.lerpAngle(horseBaseEntity.prevHeadYaw, horseBaseEntity.headYaw, h);
		float k = MathHelper.lerp(h, horseBaseEntity.prevPitch, horseBaseEntity.pitch);
		float l = j - i;
		float m = k * (float) (Math.PI / 180.0);
		if (l > 20.0F) {
			l = 20.0F;
		}

		if (l < -20.0F) {
			l = -20.0F;
		}

		if (g > 0.2F) {
			m += MathHelper.cos(f * 0.4F) * 0.15F * g;
		}

		float n = horseBaseEntity.getEatingGrassAnimationProgress(h);
		float o = horseBaseEntity.getAngryAnimationProgress(h);
		float p = 1.0F - o;
		float q = horseBaseEntity.getEatingAnimationProgress(h);
		boolean bl = horseBaseEntity.tailWagTicks != 0;
		float r = (float)horseBaseEntity.age + h;
		this.head.pivotY = 4.0F;
		this.head.pivotZ = -12.0F;
		this.torso.pitch = 0.0F;
		this.head.pitch = (float) (Math.PI / 6) + m;
		this.head.yaw = l * (float) (Math.PI / 180.0);
		float s = horseBaseEntity.isTouchingWater() ? 0.2F : 1.0F;
		float t = MathHelper.cos(s * f * 0.6662F + (float) Math.PI);
		float u = t * 0.8F * g;
		float v = (1.0F - Math.max(o, n)) * ((float) (Math.PI / 6) + m + q * MathHelper.sin(r) * 0.05F);
		this.head.pitch = o * ((float) (Math.PI / 12) + m) + n * (2.1816616F + MathHelper.sin(r) * 0.05F) + v;
		this.head.yaw = o * l * (float) (Math.PI / 180.0) + (1.0F - Math.max(o, n)) * this.head.yaw;
		this.head.pivotY = o * -4.0F + n * 11.0F + (1.0F - Math.max(o, n)) * this.head.pivotY;
		this.head.pivotZ = o * -4.0F + n * -12.0F + (1.0F - Math.max(o, n)) * this.head.pivotZ;
		this.torso.pitch = o * (float) (-Math.PI / 4) + p * this.torso.pitch;
		float w = (float) (Math.PI / 12) * o;
		float x = MathHelper.cos(r * 0.6F + (float) Math.PI);
		this.field_27428.pivotY = 2.0F * o + 14.0F * p;
		this.field_27428.pivotZ = -6.0F * o - 10.0F * p;
		this.field_27427.pivotY = this.field_27428.pivotY;
		this.field_27427.pivotZ = this.field_27428.pivotZ;
		float y = ((float) (-Math.PI / 3) + x) * o + u * p;
		float z = ((float) (-Math.PI / 3) - x) * o - u * p;
		this.field_27426.pitch = w - t * 0.5F * g * p;
		this.field_27425.pitch = w + t * 0.5F * g * p;
		this.field_27428.pitch = y;
		this.field_27427.pitch = z;
		this.tail.pitch = (float) (Math.PI / 6) + g * 0.75F;
		this.tail.pivotY = -5.0F + g;
		this.tail.pivotZ = 2.0F + g * 2.0F;
		if (bl) {
			this.tail.yaw = MathHelper.cos(r * 0.7F);
		} else {
			this.tail.yaw = 0.0F;
		}

		this.field_27429.pivotY = this.field_27425.pivotY;
		this.field_27429.pivotZ = this.field_27425.pivotZ;
		this.field_27429.pitch = this.field_27425.pitch;
		this.field_27430.pivotY = this.field_27426.pivotY;
		this.field_27430.pivotZ = this.field_27426.pivotZ;
		this.field_27430.pitch = this.field_27426.pitch;
		this.field_27431.pivotY = this.field_27427.pivotY;
		this.field_27431.pivotZ = this.field_27427.pivotZ;
		this.field_27431.pitch = this.field_27427.pitch;
		this.field_27432.pivotY = this.field_27428.pivotY;
		this.field_27432.pivotZ = this.field_27428.pivotZ;
		this.field_27432.pitch = this.field_27428.pitch;
		boolean bl2 = horseBaseEntity.isBaby();
		this.field_27425.visible = !bl2;
		this.field_27426.visible = !bl2;
		this.field_27427.visible = !bl2;
		this.field_27428.visible = !bl2;
		this.field_27429.visible = bl2;
		this.field_27430.visible = bl2;
		this.field_27431.visible = bl2;
		this.field_27432.visible = bl2;
		this.torso.pivotY = bl2 ? 10.8F : 0.0F;
	}
}
