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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class SkullEntityModel extends SkullBlockEntityModel {
	private final ModelPart field_27498;
	protected final ModelPart skull;

	public SkullEntityModel(ModelPart modelPart) {
		this.field_27498 = modelPart;
		this.skull = modelPart.method_32086("head");
	}

	public static class_5609 method_32048() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), class_5603.field_27701);
		return lv;
	}

	public static class_5607 method_32049() {
		class_5609 lv = method_32048();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32116("head")
			.method_32117(
				"hat", class_5606.method_32108().method_32101(32, 0).method_32098(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new class_5605(0.25F)), class_5603.field_27701
			);
		return class_5607.method_32110(lv, 64, 64);
	}

	public static class_5607 method_32050() {
		class_5609 lv = method_32048();
		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	public void method_2821(float f, float g, float h) {
		this.skull.yaw = g * (float) (Math.PI / 180.0);
		this.skull.pitch = h * (float) (Math.PI / 180.0);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.field_27498.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
