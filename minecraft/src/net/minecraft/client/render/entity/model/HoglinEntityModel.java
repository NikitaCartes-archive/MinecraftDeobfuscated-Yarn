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
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HoglinEntityModel<T extends MobEntity & Hoglin> extends AnimalModel<T> {
	private final ModelPart head;
	private final ModelPart rightEar;
	private final ModelPart leftEar;
	private final ModelPart torso;
	private final ModelPart field_27421;
	private final ModelPart field_27422;
	private final ModelPart field_27423;
	private final ModelPart field_27424;
	private final ModelPart field_25484;

	public HoglinEntityModel(ModelPart modelPart) {
		super(true, 8.0F, 6.0F, 1.9F, 2.0F, 24.0F);
		this.torso = modelPart.method_32086("body");
		this.field_25484 = this.torso.method_32086("mane");
		this.head = modelPart.method_32086("head");
		this.rightEar = this.head.method_32086("right_ear");
		this.leftEar = this.head.method_32086("left_ear");
		this.field_27421 = modelPart.method_32086("right_front_leg");
		this.field_27422 = modelPart.method_32086("left_front_leg");
		this.field_27423 = modelPart.method_32086("right_hind_leg");
		this.field_27424 = modelPart.method_32086("left_hind_leg");
	}

	public static class_5607 method_32009() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		class_5610 lv3 = lv2.method_32117(
			"body", class_5606.method_32108().method_32101(1, 1).method_32097(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F), class_5603.method_32090(0.0F, 7.0F, 0.0F)
		);
		lv3.method_32117(
			"mane",
			class_5606.method_32108().method_32101(90, 33).method_32098(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, new class_5605(0.001F)),
			class_5603.method_32090(0.0F, -14.0F, -5.0F)
		);
		class_5610 lv4 = lv2.method_32117(
			"head",
			class_5606.method_32108().method_32101(61, 1).method_32097(-7.0F, -3.0F, -19.0F, 14.0F, 6.0F, 19.0F),
			class_5603.method_32091(0.0F, 2.0F, -12.0F, 0.87266463F, 0.0F, 0.0F)
		);
		lv4.method_32117(
			"right_ear",
			class_5606.method_32108().method_32101(1, 1).method_32097(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F),
			class_5603.method_32091(-6.0F, -2.0F, -3.0F, 0.0F, 0.0F, (float) (-Math.PI * 2.0 / 9.0))
		);
		lv4.method_32117(
			"left_ear",
			class_5606.method_32108().method_32101(1, 6).method_32097(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F),
			class_5603.method_32091(6.0F, -2.0F, -3.0F, 0.0F, 0.0F, (float) (Math.PI * 2.0 / 9.0))
		);
		lv4.method_32117(
			"right_horn",
			class_5606.method_32108().method_32101(10, 13).method_32097(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F),
			class_5603.method_32090(-7.0F, 2.0F, -12.0F)
		);
		lv4.method_32117(
			"left_horn",
			class_5606.method_32108().method_32101(1, 13).method_32097(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F),
			class_5603.method_32090(7.0F, 2.0F, -12.0F)
		);
		int i = 14;
		int j = 11;
		lv2.method_32117(
			"right_front_leg",
			class_5606.method_32108().method_32101(66, 42).method_32097(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F),
			class_5603.method_32090(-4.0F, 10.0F, -8.5F)
		);
		lv2.method_32117(
			"left_front_leg",
			class_5606.method_32108().method_32101(41, 42).method_32097(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F),
			class_5603.method_32090(4.0F, 10.0F, -8.5F)
		);
		lv2.method_32117(
			"right_hind_leg",
			class_5606.method_32108().method_32101(21, 45).method_32097(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F),
			class_5603.method_32090(-5.0F, 13.0F, 10.0F)
		);
		lv2.method_32117(
			"left_hind_leg",
			class_5606.method_32108().method_32101(0, 45).method_32097(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F),
			class_5603.method_32090(5.0F, 13.0F, 10.0F)
		);
		return class_5607.method_32110(lv, 128, 64);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.field_27421, this.field_27422, this.field_27423, this.field_27424);
	}

	public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
		this.rightEar.roll = (float) (-Math.PI * 2.0 / 9.0) - g * MathHelper.sin(f);
		this.leftEar.roll = (float) (Math.PI * 2.0 / 9.0) + g * MathHelper.sin(f);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		int k = mobEntity.getMovementCooldownTicks();
		float l = 1.0F - (float)MathHelper.abs(10 - 2 * k) / 10.0F;
		this.head.pitch = MathHelper.lerp(l, 0.87266463F, (float) (-Math.PI / 9));
		if (mobEntity.isBaby()) {
			this.head.pivotY = MathHelper.lerp(l, 2.0F, 5.0F);
			this.field_25484.pivotZ = -3.0F;
		} else {
			this.head.pivotY = 2.0F;
			this.field_25484.pivotZ = -7.0F;
		}

		float m = 1.2F;
		this.field_27421.pitch = MathHelper.cos(f) * 1.2F * g;
		this.field_27422.pitch = MathHelper.cos(f + (float) Math.PI) * 1.2F * g;
		this.field_27423.pitch = this.field_27422.pitch;
		this.field_27424.pitch = this.field_27421.pitch;
	}
}
