package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RavagerEntityModel extends class_5597<RavagerEntity> {
	private final ModelPart field_27489;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart field_27490;
	private final ModelPart field_27491;
	private final ModelPart field_27492;
	private final ModelPart field_27493;
	private final ModelPart neck;

	public RavagerEntityModel(ModelPart modelPart) {
		this.field_27489 = modelPart;
		this.neck = modelPart.method_32086("neck");
		this.head = this.neck.method_32086("head");
		this.jaw = this.head.method_32086("mouth");
		this.field_27490 = modelPart.method_32086("right_hind_leg");
		this.field_27491 = modelPart.method_32086("left_hind_leg");
		this.field_27492 = modelPart.method_32086("right_front_leg");
		this.field_27493 = modelPart.method_32086("left_front_leg");
	}

	public static class_5607 method_32035() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		int i = 16;
		class_5610 lv3 = lv2.method_32117(
			"neck", class_5606.method_32108().method_32101(68, 73).method_32097(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F), class_5603.method_32090(0.0F, -7.0F, 5.5F)
		);
		class_5610 lv4 = lv3.method_32117(
			"head",
			class_5606.method_32108()
				.method_32101(0, 0)
				.method_32097(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F)
				.method_32101(0, 0)
				.method_32097(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F),
			class_5603.method_32090(0.0F, 16.0F, -17.0F)
		);
		lv4.method_32117(
			"right_horn",
			class_5606.method_32108().method_32101(74, 55).method_32097(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F),
			class_5603.method_32091(-10.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F)
		);
		lv4.method_32117(
			"left_horn",
			class_5606.method_32108().method_32101(74, 55).method_32096().method_32097(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F),
			class_5603.method_32091(8.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F)
		);
		lv4.method_32117(
			"mouth", class_5606.method_32108().method_32101(0, 36).method_32097(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F), class_5603.method_32090(0.0F, -2.0F, 2.0F)
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108()
				.method_32101(0, 55)
				.method_32097(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F)
				.method_32101(0, 91)
				.method_32097(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F),
			class_5603.method_32091(0.0F, 1.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		lv2.method_32117(
			"right_hind_leg",
			class_5606.method_32108().method_32101(96, 0).method_32097(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
			class_5603.method_32090(-8.0F, -13.0F, 18.0F)
		);
		lv2.method_32117(
			"left_hind_leg",
			class_5606.method_32108().method_32101(96, 0).method_32096().method_32097(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
			class_5603.method_32090(8.0F, -13.0F, 18.0F)
		);
		lv2.method_32117(
			"right_front_leg",
			class_5606.method_32108().method_32101(64, 0).method_32097(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
			class_5603.method_32090(-8.0F, -13.0F, -5.0F)
		);
		lv2.method_32117(
			"left_front_leg",
			class_5606.method_32108().method_32101(64, 0).method_32096().method_32097(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F),
			class_5603.method_32090(8.0F, -13.0F, -5.0F)
		);
		return class_5607.method_32110(lv, 128, 128);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27489;
	}

	public void setAngles(RavagerEntity ravagerEntity, float f, float g, float h, float i, float j) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		float k = 0.4F * g;
		this.field_27490.pitch = MathHelper.cos(f * 0.6662F) * k;
		this.field_27491.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * k;
		this.field_27492.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * k;
		this.field_27493.pitch = MathHelper.cos(f * 0.6662F) * k;
	}

	public void animateModel(RavagerEntity ravagerEntity, float f, float g, float h) {
		super.animateModel(ravagerEntity, f, g, h);
		int i = ravagerEntity.getStunTick();
		int j = ravagerEntity.getRoarTick();
		int k = 20;
		int l = ravagerEntity.getAttackTick();
		int m = 10;
		if (l > 0) {
			float n = MathHelper.method_24504((float)l - h, 10.0F);
			float o = (1.0F + n) * 0.5F;
			float p = o * o * o * 12.0F;
			float q = p * MathHelper.sin(this.neck.pitch);
			this.neck.pivotZ = -6.5F + p;
			this.neck.pivotY = -7.0F - q;
			float r = MathHelper.sin(((float)l - h) / 10.0F * (float) Math.PI * 0.25F);
			this.jaw.pitch = (float) (Math.PI / 2) * r;
			if (l > 5) {
				this.jaw.pitch = MathHelper.sin(((float)(-4 + l) - h) / 4.0F) * (float) Math.PI * 0.4F;
			} else {
				this.jaw.pitch = (float) (Math.PI / 20) * MathHelper.sin((float) Math.PI * ((float)l - h) / 10.0F);
			}
		} else {
			float n = -1.0F;
			float o = -1.0F * MathHelper.sin(this.neck.pitch);
			this.neck.pivotX = 0.0F;
			this.neck.pivotY = -7.0F - o;
			this.neck.pivotZ = 5.5F;
			boolean bl = i > 0;
			this.neck.pitch = bl ? 0.21991149F : 0.0F;
			this.jaw.pitch = (float) Math.PI * (bl ? 0.05F : 0.01F);
			if (bl) {
				double d = (double)i / 40.0;
				this.neck.pivotX = (float)Math.sin(d * 10.0) * 3.0F;
			} else if (j > 0) {
				float q = MathHelper.sin(((float)(20 - j) - h) / 20.0F * (float) Math.PI * 0.25F);
				this.jaw.pitch = (float) (Math.PI / 2) * q;
			}
		}
	}
}
