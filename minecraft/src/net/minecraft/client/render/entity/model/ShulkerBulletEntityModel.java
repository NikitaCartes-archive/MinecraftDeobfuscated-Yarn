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
public class ShulkerBulletEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart field_27496;
	private final ModelPart bullet;

	public ShulkerBulletEntityModel(ModelPart modelPart) {
		this.field_27496 = modelPart;
		this.bullet = modelPart.method_32086("main");
	}

	public static class_5607 method_32040() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"main",
			class_5606.method_32108()
				.method_32101(0, 0)
				.method_32097(-4.0F, -4.0F, -1.0F, 8.0F, 8.0F, 2.0F)
				.method_32101(0, 10)
				.method_32097(-1.0F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F)
				.method_32101(20, 0)
				.method_32097(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F),
			class_5603.field_27701
		);
		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27496;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.bullet.yaw = headYaw * (float) (Math.PI / 180.0);
		this.bullet.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
