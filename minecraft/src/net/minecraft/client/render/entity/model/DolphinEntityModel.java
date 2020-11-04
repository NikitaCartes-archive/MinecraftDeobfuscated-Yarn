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
public class DolphinEntityModel<T extends Entity> extends class_5597<T> {
	private final ModelPart field_27411;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart flukes;

	public DolphinEntityModel(ModelPart modelPart) {
		this.field_27411 = modelPart;
		this.body = modelPart.method_32086("body");
		this.tail = this.body.method_32086("tail");
		this.flukes = this.tail.method_32086("tail_fin");
	}

	public static class_5607 method_31992() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		float f = 18.0F;
		float g = -8.0F;
		class_5610 lv3 = lv2.method_32117(
			"body", class_5606.method_32108().method_32101(22, 0).method_32097(-4.0F, -7.0F, 0.0F, 8.0F, 7.0F, 13.0F), class_5603.method_32090(0.0F, 22.0F, -5.0F)
		);
		lv3.method_32117(
			"back_fin",
			class_5606.method_32108().method_32101(51, 0).method_32097(-0.5F, 0.0F, 8.0F, 1.0F, 4.0F, 5.0F),
			class_5603.method_32092((float) (Math.PI / 3), 0.0F, 0.0F)
		);
		lv3.method_32117(
			"left_fin",
			class_5606.method_32108().method_32101(48, 20).method_32096().method_32097(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F),
			class_5603.method_32091(2.0F, -2.0F, 4.0F, (float) (Math.PI / 3), 0.0F, (float) (Math.PI * 2.0 / 3.0))
		);
		lv3.method_32117(
			"right_fin",
			class_5606.method_32108().method_32101(48, 20).method_32097(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F),
			class_5603.method_32091(-2.0F, -2.0F, 4.0F, (float) (Math.PI / 3), 0.0F, (float) (-Math.PI * 2.0 / 3.0))
		);
		class_5610 lv4 = lv3.method_32117(
			"tail",
			class_5606.method_32108().method_32101(0, 19).method_32097(-2.0F, -2.5F, 0.0F, 4.0F, 5.0F, 11.0F),
			class_5603.method_32091(0.0F, -2.5F, 11.0F, -0.10471976F, 0.0F, 0.0F)
		);
		lv4.method_32117(
			"tail_fin", class_5606.method_32108().method_32101(19, 20).method_32097(-5.0F, -0.5F, 0.0F, 10.0F, 1.0F, 6.0F), class_5603.method_32090(0.0F, 0.0F, 9.0F)
		);
		class_5610 lv5 = lv3.method_32117(
			"head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0F, -3.0F, -3.0F, 8.0F, 7.0F, 6.0F), class_5603.method_32090(0.0F, -4.0F, -3.0F)
		);
		lv5.method_32117("nose", class_5606.method_32108().method_32101(0, 13).method_32097(-1.0F, 2.0F, -7.0F, 2.0F, 2.0F, 4.0F), class_5603.field_27701);
		return class_5607.method_32110(lv, 64, 64);
	}

	@Override
	public ModelPart method_32008() {
		return this.field_27411;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.body.pitch = headPitch * (float) (Math.PI / 180.0);
		this.body.yaw = headYaw * (float) (Math.PI / 180.0);
		if (Entity.squaredHorizontalLength(entity.getVelocity()) > 1.0E-7) {
			this.body.pitch = this.body.pitch + (-0.05F - 0.05F * MathHelper.cos(animationProgress * 0.3F));
			this.tail.pitch = -0.1F * MathHelper.cos(animationProgress * 0.3F);
			this.flukes.pitch = -0.2F * MathHelper.cos(animationProgress * 0.3F);
		}
	}
}
