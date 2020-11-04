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
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WitherEntityModel<T extends WitherEntity> extends class_5597<T> {
	private final ModelPart field_27532;
	private final ModelPart field_27533;
	private final ModelPart field_27534;
	private final ModelPart field_27535;
	private final ModelPart field_27536;
	private final ModelPart field_27537;

	public WitherEntityModel(ModelPart modelPart) {
		this.field_27532 = modelPart;
		this.field_27536 = modelPart.method_32086("ribcage");
		this.field_27537 = modelPart.method_32086("tail");
		this.field_27533 = modelPart.method_32086("center_head");
		this.field_27534 = modelPart.method_32086("right_head");
		this.field_27535 = modelPart.method_32086("left_head");
	}

	public static class_5607 method_32067(class_5605 arg) {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("shoulders", class_5606.method_32108().method_32101(0, 16).method_32098(-10.0F, 3.9F, -0.5F, 20.0F, 3.0F, 3.0F, arg), class_5603.field_27701);
		float f = 0.20420352F;
		lv2.method_32117(
			"ribcage",
			class_5606.method_32108()
				.method_32101(0, 22)
				.method_32098(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, arg)
				.method_32101(24, 22)
				.method_32098(-4.0F, 1.5F, 0.5F, 11.0F, 2.0F, 2.0F, arg)
				.method_32101(24, 22)
				.method_32098(-4.0F, 4.0F, 0.5F, 11.0F, 2.0F, 2.0F, arg)
				.method_32101(24, 22)
				.method_32098(-4.0F, 6.5F, 0.5F, 11.0F, 2.0F, 2.0F, arg),
			class_5603.method_32091(-2.0F, 6.9F, -0.5F, 0.20420352F, 0.0F, 0.0F)
		);
		lv2.method_32117(
			"tail",
			class_5606.method_32108().method_32101(12, 22).method_32098(0.0F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F, arg),
			class_5603.method_32091(-2.0F, 6.9F + MathHelper.cos(0.20420352F) * 10.0F, -0.5F + MathHelper.sin(0.20420352F) * 10.0F, 0.83252203F, 0.0F, 0.0F)
		);
		lv2.method_32117("center_head", class_5606.method_32108().method_32101(0, 0).method_32098(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, arg), class_5603.field_27701);
		class_5606 lv3 = class_5606.method_32108().method_32101(32, 0).method_32098(-4.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, arg);
		lv2.method_32117("right_head", lv3, class_5603.method_32090(-8.0F, 4.0F, 0.0F));
		lv2.method_32117("left_head", lv3, class_5603.method_32090(10.0F, 4.0F, 0.0F));
		return class_5607.method_32110(lv, 64, 64);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27532;
	}

	public void setAngles(T witherEntity, float f, float g, float h, float i, float j) {
		float k = MathHelper.cos(h * 0.1F);
		this.field_27536.pitch = (0.065F + 0.05F * k) * (float) Math.PI;
		this.field_27537.setPivot(-2.0F, 6.9F + MathHelper.cos(this.field_27536.pitch) * 10.0F, -0.5F + MathHelper.sin(this.field_27536.pitch) * 10.0F);
		this.field_27537.pitch = (0.265F + 0.1F * k) * (float) Math.PI;
		this.field_27533.yaw = i * (float) (Math.PI / 180.0);
		this.field_27533.pitch = j * (float) (Math.PI / 180.0);
	}

	public void animateModel(T witherEntity, float f, float g, float h) {
		method_32066(witherEntity, this.field_27534, 0);
		method_32066(witherEntity, this.field_27535, 1);
	}

	private static <T extends WitherEntity> void method_32066(T witherEntity, ModelPart modelPart, int i) {
		modelPart.yaw = (witherEntity.getHeadYaw(i) - witherEntity.bodyYaw) * (float) (Math.PI / 180.0);
		modelPart.pitch = witherEntity.getHeadPitch(i) * (float) (Math.PI / 180.0);
	}
}
