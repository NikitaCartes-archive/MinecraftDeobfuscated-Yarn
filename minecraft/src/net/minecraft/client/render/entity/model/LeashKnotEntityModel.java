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
public class LeashKnotEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart field_27442;
	private final ModelPart leashKnot;

	public LeashKnotEntityModel(ModelPart modelPart) {
		this.field_27442 = modelPart;
		this.leashKnot = modelPart.method_32086("knot");
	}

	public static class_5607 method_32017() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("knot", class_5606.method_32108().method_32101(0, 0).method_32097(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F), class_5603.field_27701);
		return class_5607.method_32110(lv, 32, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27442;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.leashKnot.yaw = headYaw * (float) (Math.PI / 180.0);
		this.leashKnot.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
