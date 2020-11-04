package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class DragonHeadEntityModel extends SkullBlockEntityModel {
	private final ModelPart head;
	private final ModelPart jaw;

	public DragonHeadEntityModel(ModelPart modelPart) {
		this.head = modelPart.method_32086("head");
		this.jaw = this.head.method_32086("jaw");
	}

	public static class_5607 method_32071() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		float f = -16.0F;
		class_5610 lv3 = lv2.method_32117(
			"head",
			class_5606.method_32108()
				.method_32104("upper_lip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 176, 44)
				.method_32104("upper_head", -8.0F, -8.0F, -10.0F, 16, 16, 16, 112, 30)
				.method_32106(true)
				.method_32104("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
				.method_32104("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0)
				.method_32106(false)
				.method_32104("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
				.method_32104("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0),
			class_5603.field_27701
		);
		lv3.method_32117(
			"jaw",
			class_5606.method_32108().method_32101(176, 65).method_32102("jaw", -6.0F, 0.0F, -16.0F, 12.0F, 4.0F, 16.0F),
			class_5603.method_32090(0.0F, 4.0F, -8.0F)
		);
		return class_5607.method_32110(lv, 256, 256);
	}

	@Override
	public void method_2821(float f, float g, float h) {
		this.jaw.pitch = (float)(Math.sin((double)(f * (float) Math.PI * 0.2F)) + 1.0) * 0.2F;
		this.head.yaw = g * (float) (Math.PI / 180.0);
		this.head.pitch = h * (float) (Math.PI / 180.0);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		matrices.push();
		matrices.translate(0.0, -0.374375F, 0.0);
		matrices.scale(0.75F, 0.75F, 0.75F);
		this.head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		matrices.pop();
	}
}
