package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BookModel extends Model {
	private final ModelPart field_27398;
	private final ModelPart leftCover;
	private final ModelPart rightCover;
	private final ModelPart leftBlock;
	private final ModelPart rightBlock;
	private final ModelPart leftPage;
	private final ModelPart rightPage;

	public BookModel(ModelPart modelPart) {
		super(RenderLayer::getEntitySolid);
		this.field_27398 = modelPart;
		this.leftCover = modelPart.method_32086("left_lid");
		this.rightCover = modelPart.method_32086("right_lid");
		this.leftBlock = modelPart.method_32086("left_pages");
		this.rightBlock = modelPart.method_32086("right_pages");
		this.leftPage = modelPart.method_32086("flip_page1");
		this.rightPage = modelPart.method_32086("flip_page2");
	}

	public static class_5607 method_31986() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"left_lid",
			class_5606.method_32108().method_32101(0, 0).method_32097(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F),
			class_5603.method_32090(0.0F, 0.0F, -1.0F)
		);
		lv2.method_32117(
			"right_lid",
			class_5606.method_32108().method_32101(16, 0).method_32097(0.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F),
			class_5603.method_32090(0.0F, 0.0F, 1.0F)
		);
		lv2.method_32117(
			"seam",
			class_5606.method_32108().method_32101(12, 0).method_32097(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F),
			class_5603.method_32092(0.0F, (float) (Math.PI / 2), 0.0F)
		);
		lv2.method_32117("left_pages", class_5606.method_32108().method_32101(0, 10).method_32097(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F), class_5603.field_27701);
		lv2.method_32117("right_pages", class_5606.method_32108().method_32101(12, 10).method_32097(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F), class_5603.field_27701);
		class_5606 lv3 = class_5606.method_32108().method_32101(24, 10).method_32097(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
		lv2.method_32117("flip_page1", lv3, class_5603.field_27701);
		lv2.method_32117("flip_page2", lv3, class_5603.field_27701);
		return class_5607.method_32110(lv, 64, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.method_24184(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	public void method_24184(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.field_27398.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	public void setPageAngles(float f, float g, float h, float i) {
		float j = (MathHelper.sin(f * 0.02F) * 0.1F + 1.25F) * i;
		this.leftCover.yaw = (float) Math.PI + j;
		this.rightCover.yaw = -j;
		this.leftBlock.yaw = j;
		this.rightBlock.yaw = -j;
		this.leftPage.yaw = j - j * 2.0F * g;
		this.rightPage.yaw = j - j * 2.0F * h;
		this.leftBlock.pivotX = MathHelper.sin(j);
		this.rightBlock.pivotX = MathHelper.sin(j);
		this.leftPage.pivotX = MathHelper.sin(j);
		this.rightPage.pivotX = MathHelper.sin(j);
	}
}
