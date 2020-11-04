package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class IronGolemEntityModel<T extends IronGolemEntity> extends class_5597<T> {
	private final ModelPart field_27436;
	private final ModelPart head;
	private final ModelPart field_27437;
	private final ModelPart field_27438;
	private final ModelPart field_27439;
	private final ModelPart field_27440;

	public IronGolemEntityModel(ModelPart modelPart) {
		this.field_27436 = modelPart;
		this.head = modelPart.method_32086("head");
		this.field_27437 = modelPart.method_32086("right_arm");
		this.field_27438 = modelPart.method_32086("left_arm");
		this.field_27439 = modelPart.method_32086("right_leg");
		this.field_27440 = modelPart.method_32086("left_leg");
	}

	public static class_5607 method_32013() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"head",
			class_5606.method_32108()
				.method_32101(0, 0)
				.method_32097(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F)
				.method_32101(24, 0)
				.method_32097(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F),
			class_5603.method_32090(0.0F, -7.0F, -2.0F)
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108()
				.method_32101(0, 40)
				.method_32097(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F)
				.method_32101(0, 70)
				.method_32098(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, new class_5605(0.5F)),
			class_5603.method_32090(0.0F, -7.0F, 0.0F)
		);
		lv2.method_32117(
			"right_arm",
			class_5606.method_32108().method_32101(60, 21).method_32097(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F),
			class_5603.method_32090(0.0F, -7.0F, 0.0F)
		);
		lv2.method_32117(
			"left_arm", class_5606.method_32108().method_32101(60, 58).method_32097(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F), class_5603.method_32090(0.0F, -7.0F, 0.0F)
		);
		lv2.method_32117(
			"right_leg", class_5606.method_32108().method_32101(37, 0).method_32097(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), class_5603.method_32090(-4.0F, 11.0F, 0.0F)
		);
		lv2.method_32117(
			"left_leg",
			class_5606.method_32108().method_32101(60, 0).method_32096().method_32097(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F),
			class_5603.method_32090(5.0F, 11.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 128, 128);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27436;
	}

	public void setAngles(T ironGolemEntity, float f, float g, float h, float i, float j) {
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.field_27439.pitch = -1.5F * MathHelper.method_24504(f, 13.0F) * g;
		this.field_27440.pitch = 1.5F * MathHelper.method_24504(f, 13.0F) * g;
		this.field_27439.yaw = 0.0F;
		this.field_27440.yaw = 0.0F;
	}

	public void animateModel(T ironGolemEntity, float f, float g, float h) {
		int i = ironGolemEntity.getAttackTicksLeft();
		if (i > 0) {
			this.field_27437.pitch = -2.0F + 1.5F * MathHelper.method_24504((float)i - h, 10.0F);
			this.field_27438.pitch = -2.0F + 1.5F * MathHelper.method_24504((float)i - h, 10.0F);
		} else {
			int j = ironGolemEntity.getLookingAtVillagerTicks();
			if (j > 0) {
				this.field_27437.pitch = -0.8F + 0.025F * MathHelper.method_24504((float)j, 70.0F);
				this.field_27438.pitch = 0.0F;
			} else {
				this.field_27437.pitch = (-0.2F + 1.5F * MathHelper.method_24504(f, 13.0F)) * g;
				this.field_27438.pitch = (-0.2F - 1.5F * MathHelper.method_24504(f, 13.0F)) * g;
			}
		}
	}

	public ModelPart getRightArm() {
		return this.field_27437;
	}
}
