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
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StriderEntityModel<T extends StriderEntity> extends class_5597<T> {
	private final ModelPart field_27514;
	private final ModelPart field_23353;
	private final ModelPart field_23354;
	private final ModelPart field_23355;
	private final ModelPart field_27515;
	private final ModelPart field_27516;
	private final ModelPart field_27517;
	private final ModelPart field_27518;
	private final ModelPart field_27519;
	private final ModelPart field_27520;

	public StriderEntityModel(ModelPart modelPart) {
		this.field_27514 = modelPart;
		this.field_23353 = modelPart.method_32086("right_leg");
		this.field_23354 = modelPart.method_32086("left_leg");
		this.field_23355 = modelPart.method_32086("body");
		this.field_27515 = this.field_23355.method_32086("right_bottom_bristle");
		this.field_27516 = this.field_23355.method_32086("right_middle_bristle");
		this.field_27517 = this.field_23355.method_32086("right_top_bristle");
		this.field_27518 = this.field_23355.method_32086("left_top_bristle");
		this.field_27519 = this.field_23355.method_32086("left_middle_bristle");
		this.field_27520 = this.field_23355.method_32086("left_bottom_bristle");
	}

	public static class_5607 method_32058() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"right_leg", class_5606.method_32108().method_32101(0, 32).method_32097(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), class_5603.method_32090(-4.0F, 8.0F, 0.0F)
		);
		lv2.method_32117(
			"left_leg", class_5606.method_32108().method_32101(0, 55).method_32097(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), class_5603.method_32090(4.0F, 8.0F, 0.0F)
		);
		class_5610 lv3 = lv2.method_32117(
			"body", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F), class_5603.method_32090(0.0F, 1.0F, 0.0F)
		);
		lv3.method_32117(
			"right_bottom_bristle",
			class_5606.method_32108().method_32101(16, 65).method_32100(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
			class_5603.method_32091(-8.0F, 4.0F, -8.0F, 0.0F, 0.0F, -1.2217305F)
		);
		lv3.method_32117(
			"right_middle_bristle",
			class_5606.method_32108().method_32101(16, 49).method_32100(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
			class_5603.method_32091(-8.0F, -1.0F, -8.0F, 0.0F, 0.0F, -1.134464F)
		);
		lv3.method_32117(
			"right_top_bristle",
			class_5606.method_32108().method_32101(16, 33).method_32100(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
			class_5603.method_32091(-8.0F, -5.0F, -8.0F, 0.0F, 0.0F, -0.87266463F)
		);
		lv3.method_32117(
			"left_top_bristle",
			class_5606.method_32108().method_32101(16, 33).method_32097(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
			class_5603.method_32091(8.0F, -6.0F, -8.0F, 0.0F, 0.0F, 0.87266463F)
		);
		lv3.method_32117(
			"left_middle_bristle",
			class_5606.method_32108().method_32101(16, 49).method_32097(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
			class_5603.method_32091(8.0F, -2.0F, -8.0F, 0.0F, 0.0F, 1.134464F)
		);
		lv3.method_32117(
			"left_bottom_bristle",
			class_5606.method_32108().method_32101(16, 65).method_32097(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
			class_5603.method_32091(8.0F, 3.0F, -8.0F, 0.0F, 0.0F, 1.2217305F)
		);
		return class_5607.method_32110(lv, 64, 128);
	}

	public void setAngles(StriderEntity striderEntity, float f, float g, float h, float i, float j) {
		g = Math.min(0.25F, g);
		if (!striderEntity.hasPassengers()) {
			this.field_23355.pitch = j * (float) (Math.PI / 180.0);
			this.field_23355.yaw = i * (float) (Math.PI / 180.0);
		} else {
			this.field_23355.pitch = 0.0F;
			this.field_23355.yaw = 0.0F;
		}

		float k = 1.5F;
		this.field_23355.roll = 0.1F * MathHelper.sin(f * 1.5F) * 4.0F * g;
		this.field_23355.pivotY = 2.0F;
		this.field_23355.pivotY = this.field_23355.pivotY - 2.0F * MathHelper.cos(f * 1.5F) * 2.0F * g;
		this.field_23354.pitch = MathHelper.sin(f * 1.5F * 0.5F) * 2.0F * g;
		this.field_23353.pitch = MathHelper.sin(f * 1.5F * 0.5F + (float) Math.PI) * 2.0F * g;
		this.field_23354.roll = (float) (Math.PI / 18) * MathHelper.cos(f * 1.5F * 0.5F) * g;
		this.field_23353.roll = (float) (Math.PI / 18) * MathHelper.cos(f * 1.5F * 0.5F + (float) Math.PI) * g;
		this.field_23354.pivotY = 8.0F + 2.0F * MathHelper.sin(f * 1.5F * 0.5F + (float) Math.PI) * 2.0F * g;
		this.field_23353.pivotY = 8.0F + 2.0F * MathHelper.sin(f * 1.5F * 0.5F) * 2.0F * g;
		this.field_27515.roll = -1.2217305F;
		this.field_27516.roll = -1.134464F;
		this.field_27517.roll = -0.87266463F;
		this.field_27518.roll = 0.87266463F;
		this.field_27519.roll = 1.134464F;
		this.field_27520.roll = 1.2217305F;
		float l = MathHelper.cos(f * 1.5F + (float) Math.PI) * g;
		this.field_27515.roll += l * 1.3F;
		this.field_27516.roll += l * 1.2F;
		this.field_27517.roll += l * 0.6F;
		this.field_27518.roll += l * 0.6F;
		this.field_27519.roll += l * 1.2F;
		this.field_27520.roll += l * 1.3F;
		float m = 1.0F;
		float n = 1.0F;
		this.field_27515.roll = this.field_27515.roll + 0.05F * MathHelper.sin(h * 1.0F * -0.4F);
		this.field_27516.roll = this.field_27516.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.2F);
		this.field_27517.roll = this.field_27517.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.4F);
		this.field_27518.roll = this.field_27518.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.4F);
		this.field_27519.roll = this.field_27519.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.2F);
		this.field_27520.roll = this.field_27520.roll + 0.05F * MathHelper.sin(h * 1.0F * -0.4F);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27514;
	}
}
