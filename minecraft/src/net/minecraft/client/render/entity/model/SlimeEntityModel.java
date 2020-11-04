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

@Environment(EnvType.CLIENT)
public class SlimeEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart field_27499;

	public SlimeEntityModel(ModelPart modelPart) {
		this.field_27499 = modelPart;
	}

	public static class_5607 method_32051() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("cube", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F), class_5603.field_27701);
		return class_5607.method_32110(lv, 64, 32);
	}

	public static class_5607 method_32052() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("cube", class_5606.method_32108().method_32101(0, 16).method_32097(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F), class_5603.field_27701);
		lv2.method_32117("right_eye", class_5606.method_32108().method_32101(32, 0).method_32097(-3.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F), class_5603.field_27701);
		lv2.method_32117("left_eye", class_5606.method_32108().method_32101(32, 4).method_32097(1.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F), class_5603.field_27701);
		lv2.method_32117("mouth", class_5606.method_32108().method_32101(32, 8).method_32097(0.0F, 21.0F, -3.5F, 1.0F, 1.0F, 1.0F), class_5603.field_27701);
		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27499;
	}
}
