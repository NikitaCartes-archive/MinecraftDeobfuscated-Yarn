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
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpiderEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart field_27504;
	private final ModelPart head;
	private final ModelPart field_27505;
	private final ModelPart field_27506;
	private final ModelPart field_27507;
	private final ModelPart field_27508;
	private final ModelPart field_27509;
	private final ModelPart field_27510;
	private final ModelPart field_27511;
	private final ModelPart field_27512;

	public SpiderEntityModel(ModelPart modelPart) {
		this.field_27504 = modelPart;
		this.head = modelPart.method_32086("head");
		this.field_27505 = modelPart.method_32086("right_hind_leg");
		this.field_27506 = modelPart.method_32086("left_hind_leg");
		this.field_27507 = modelPart.method_32086("right_middle_hind_leg");
		this.field_27508 = modelPart.method_32086("left_middle_hind_leg");
		this.field_27509 = modelPart.method_32086("right_middle_front_leg");
		this.field_27510 = modelPart.method_32086("left_middle_front_leg");
		this.field_27511 = modelPart.method_32086("right_front_leg");
		this.field_27512 = modelPart.method_32086("left_front_leg");
	}

	public static class_5607 method_32054() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		int i = 15;
		lv2.method_32117(
			"head", class_5606.method_32108().method_32101(32, 4).method_32097(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), class_5603.method_32090(0.0F, 15.0F, -3.0F)
		);
		lv2.method_32117(
			"body0", class_5606.method_32108().method_32101(0, 0).method_32097(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), class_5603.method_32090(0.0F, 15.0F, 0.0F)
		);
		lv2.method_32117(
			"body1", class_5606.method_32108().method_32101(0, 12).method_32097(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), class_5603.method_32090(0.0F, 15.0F, 9.0F)
		);
		class_5606 lv3 = class_5606.method_32108().method_32101(18, 0).method_32097(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
		class_5606 lv4 = class_5606.method_32108().method_32101(18, 0).method_32097(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
		lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-4.0F, 15.0F, 2.0F));
		lv2.method_32117("left_hind_leg", lv4, class_5603.method_32090(4.0F, 15.0F, 2.0F));
		lv2.method_32117("right_middle_hind_leg", lv3, class_5603.method_32090(-4.0F, 15.0F, 1.0F));
		lv2.method_32117("left_middle_hind_leg", lv4, class_5603.method_32090(4.0F, 15.0F, 1.0F));
		lv2.method_32117("right_middle_front_leg", lv3, class_5603.method_32090(-4.0F, 15.0F, 0.0F));
		lv2.method_32117("left_middle_front_leg", lv4, class_5603.method_32090(4.0F, 15.0F, 0.0F));
		lv2.method_32117("right_front_leg", lv3, class_5603.method_32090(-4.0F, 15.0F, -1.0F));
		lv2.method_32117("left_front_leg", lv4, class_5603.method_32090(4.0F, 15.0F, -1.0F));
		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27504;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		float f = (float) (Math.PI / 4);
		this.field_27505.roll = (float) (-Math.PI / 4);
		this.field_27506.roll = (float) (Math.PI / 4);
		this.field_27507.roll = -0.58119464F;
		this.field_27508.roll = 0.58119464F;
		this.field_27509.roll = -0.58119464F;
		this.field_27510.roll = 0.58119464F;
		this.field_27511.roll = (float) (-Math.PI / 4);
		this.field_27512.roll = (float) (Math.PI / 4);
		float g = -0.0F;
		float h = (float) (Math.PI / 8);
		this.field_27505.yaw = (float) (Math.PI / 4);
		this.field_27506.yaw = (float) (-Math.PI / 4);
		this.field_27507.yaw = (float) (Math.PI / 8);
		this.field_27508.yaw = (float) (-Math.PI / 8);
		this.field_27509.yaw = (float) (-Math.PI / 8);
		this.field_27510.yaw = (float) (Math.PI / 8);
		this.field_27511.yaw = (float) (-Math.PI / 4);
		this.field_27512.yaw = (float) (Math.PI / 4);
		float i = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbDistance;
		float j = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbDistance;
		float k = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * limbDistance;
		float l = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance;
		float m = Math.abs(MathHelper.sin(limbAngle * 0.6662F + 0.0F) * 0.4F) * limbDistance;
		float n = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) Math.PI) * 0.4F) * limbDistance;
		float o = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * limbDistance;
		float p = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance;
		this.field_27505.yaw += i;
		this.field_27506.yaw += -i;
		this.field_27507.yaw += j;
		this.field_27508.yaw += -j;
		this.field_27509.yaw += k;
		this.field_27510.yaw += -k;
		this.field_27511.yaw += l;
		this.field_27512.yaw += -l;
		this.field_27505.roll += m;
		this.field_27506.roll += -m;
		this.field_27507.roll += n;
		this.field_27508.roll += -n;
		this.field_27509.roll += o;
		this.field_27510.roll += -o;
		this.field_27511.roll += p;
		this.field_27512.roll += -p;
	}
}
