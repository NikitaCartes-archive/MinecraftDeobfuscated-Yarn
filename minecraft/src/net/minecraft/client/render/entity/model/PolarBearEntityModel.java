package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.PolarBearEntity;

@Environment(EnvType.CLIENT)
public class PolarBearEntityModel<T extends PolarBearEntity> extends QuadrupedEntityModel<T> {
	public PolarBearEntityModel(ModelPart modelPart) {
		super(modelPart, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);
	}

	public static class_5607 method_32029() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"head",
			class_5606.method_32108()
				.method_32101(0, 0)
				.method_32097(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F)
				.method_32101(0, 44)
				.method_32102("mouth", -2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F)
				.method_32101(26, 0)
				.method_32102("right_ear", -4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F)
				.method_32101(26, 0)
				.method_32096()
				.method_32102("left_ear", 2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F),
			class_5603.method_32090(0.0F, 10.0F, -16.0F)
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108()
				.method_32101(0, 19)
				.method_32097(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F)
				.method_32101(39, 0)
				.method_32097(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F),
			class_5603.method_32091(-2.0F, 9.0F, 12.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		int i = 10;
		class_5606 lv3 = class_5606.method_32108().method_32101(50, 22).method_32097(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F);
		lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-4.5F, 14.0F, 6.0F));
		lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(4.5F, 14.0F, 6.0F));
		class_5606 lv4 = class_5606.method_32108().method_32101(50, 40).method_32097(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F);
		lv2.method_32117("right_front_leg", lv4, class_5603.method_32090(-3.5F, 14.0F, -8.0F));
		lv2.method_32117("left_front_leg", lv4, class_5603.method_32090(3.5F, 14.0F, -8.0F));
		return class_5607.method_32110(lv, 128, 64);
	}

	public void setAngles(T polarBearEntity, float f, float g, float h, float i, float j) {
		super.setAngles(polarBearEntity, f, g, h, i, j);
		float k = h - (float)polarBearEntity.age;
		float l = polarBearEntity.getWarningAnimationProgress(k);
		l *= l;
		float m = 1.0F - l;
		this.torso.pitch = (float) (Math.PI / 2) - l * (float) Math.PI * 0.35F;
		this.torso.pivotY = 9.0F * m + 11.0F * l;
		this.field_27478.pivotY = 14.0F * m - 6.0F * l;
		this.field_27478.pivotZ = -8.0F * m - 4.0F * l;
		this.field_27478.pitch -= l * (float) Math.PI * 0.45F;
		this.field_27479.pivotY = this.field_27478.pivotY;
		this.field_27479.pivotZ = this.field_27478.pivotZ;
		this.field_27479.pitch -= l * (float) Math.PI * 0.45F;
		if (this.child) {
			this.head.pivotY = 10.0F * m - 9.0F * l;
			this.head.pivotZ = -16.0F * m - 7.0F * l;
		} else {
			this.head.pivotY = 10.0F * m - 14.0F * l;
			this.head.pivotZ = -16.0F * m - 3.0F * l;
		}

		this.head.pitch += l * (float) Math.PI * 0.15F;
	}
}
