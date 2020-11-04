package net.minecraft.client.render.entity.model;

import java.util.Arrays;
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
public class BlazeEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart field_27394;
	private final ModelPart[] rods;
	private final ModelPart field_27395;

	public BlazeEntityModel(ModelPart modelPart) {
		this.field_27394 = modelPart;
		this.field_27395 = modelPart.method_32086("head");
		this.rods = new ModelPart[12];
		Arrays.setAll(this.rods, i -> modelPart.method_32086(method_31983(i)));
	}

	private static String method_31983(int i) {
		return "part" + i;
	}

	public static class_5607 method_31982() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), class_5603.field_27701);
		float f = 0.0F;
		class_5606 lv3 = class_5606.method_32108().method_32101(0, 16).method_32097(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);

		for (int i = 0; i < 4; i++) {
			float g = MathHelper.cos(f) * 9.0F;
			float h = -2.0F + MathHelper.cos((float)(i * 2) * 0.25F);
			float j = MathHelper.sin(f) * 9.0F;
			lv2.method_32117(method_31983(i), lv3, class_5603.method_32090(g, h, j));
			f++;
		}

		f = (float) (Math.PI / 4);

		for (int i = 4; i < 8; i++) {
			float g = MathHelper.cos(f) * 7.0F;
			float h = 2.0F + MathHelper.cos((float)(i * 2) * 0.25F);
			float j = MathHelper.sin(f) * 7.0F;
			lv2.method_32117(method_31983(i), lv3, class_5603.method_32090(g, h, j));
			f++;
		}

		f = 0.47123894F;

		for (int i = 8; i < 12; i++) {
			float g = MathHelper.cos(f) * 5.0F;
			float h = 11.0F + MathHelper.cos((float)i * 1.5F * 0.5F);
			float j = MathHelper.sin(f) * 5.0F;
			lv2.method_32117(method_31983(i), lv3, class_5603.method_32090(g, h, j));
			f++;
		}

		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27394;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float f = animationProgress * (float) Math.PI * -0.1F;

		for (int i = 0; i < 4; i++) {
			this.rods[i].pivotY = -2.0F + MathHelper.cos(((float)(i * 2) + animationProgress) * 0.25F);
			this.rods[i].pivotX = MathHelper.cos(f) * 9.0F;
			this.rods[i].pivotZ = MathHelper.sin(f) * 9.0F;
			f++;
		}

		f = (float) (Math.PI / 4) + animationProgress * (float) Math.PI * 0.03F;

		for (int i = 4; i < 8; i++) {
			this.rods[i].pivotY = 2.0F + MathHelper.cos(((float)(i * 2) + animationProgress) * 0.25F);
			this.rods[i].pivotX = MathHelper.cos(f) * 7.0F;
			this.rods[i].pivotZ = MathHelper.sin(f) * 7.0F;
			f++;
		}

		f = 0.47123894F + animationProgress * (float) Math.PI * -0.05F;

		for (int i = 8; i < 12; i++) {
			this.rods[i].pivotY = 11.0F + MathHelper.cos(((float)i * 1.5F + animationProgress) * 0.5F);
			this.rods[i].pivotX = MathHelper.cos(f) * 5.0F;
			this.rods[i].pivotZ = MathHelper.sin(f) * 5.0F;
			f++;
		}

		this.field_27395.yaw = headYaw * (float) (Math.PI / 180.0);
		this.field_27395.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
