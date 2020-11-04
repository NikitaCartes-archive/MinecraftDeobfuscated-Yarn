package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.SheepEntity;

@Environment(EnvType.CLIENT)
public class SheepWoolEntityModel<T extends SheepEntity> extends QuadrupedEntityModel<T> {
	private float field_3541;

	public SheepWoolEntityModel(ModelPart modelPart) {
		super(modelPart, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
	}

	public static class_5607 method_32037() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"head",
			class_5606.method_32108().method_32101(0, 0).method_32098(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, new class_5605(0.6F)),
			class_5603.method_32090(0.0F, 6.0F, -8.0F)
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108().method_32101(28, 8).method_32098(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, new class_5605(1.75F)),
			class_5603.method_32091(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		class_5606 lv3 = class_5606.method_32108().method_32101(0, 16).method_32098(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new class_5605(0.5F));
		lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-3.0F, 12.0F, 7.0F));
		lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(3.0F, 12.0F, 7.0F));
		lv2.method_32117("right_front_leg", lv3, class_5603.method_32090(-3.0F, 12.0F, -5.0F));
		lv2.method_32117("left_front_leg", lv3, class_5603.method_32090(3.0F, 12.0F, -5.0F));
		return class_5607.method_32110(lv, 64, 32);
	}

	public void animateModel(T sheepEntity, float f, float g, float h) {
		super.animateModel(sheepEntity, f, g, h);
		this.head.pivotY = 6.0F + sheepEntity.getNeckAngle(h) * 9.0F;
		this.field_3541 = sheepEntity.getHeadAngle(h);
	}

	public void setAngles(T sheepEntity, float f, float g, float h, float i, float j) {
		super.setAngles(sheepEntity, f, g, h, i, j);
		this.head.pitch = this.field_3541;
	}
}
