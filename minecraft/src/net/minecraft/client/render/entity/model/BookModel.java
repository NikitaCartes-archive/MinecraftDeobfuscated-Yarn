package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
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
		this.leftCover = modelPart.getChild("left_lid");
		this.rightCover = modelPart.getChild("right_lid");
		this.leftBlock = modelPart.getChild("left_pages");
		this.rightBlock = modelPart.getChild("right_pages");
		this.leftPage = modelPart.getChild("flip_page1");
		this.rightPage = modelPart.getChild("flip_page2");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			"left_lid", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F), ModelTransform.pivot(0.0F, 0.0F, -1.0F)
		);
		modelPartData.addChild(
			"right_lid", ModelPartBuilder.create().uv(16, 0).cuboid(0.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F), ModelTransform.pivot(0.0F, 0.0F, 1.0F)
		);
		modelPartData.addChild(
			"seam", ModelPartBuilder.create().uv(12, 0).cuboid(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F), ModelTransform.rotation(0.0F, (float) (Math.PI / 2), 0.0F)
		);
		modelPartData.addChild("left_pages", ModelPartBuilder.create().uv(0, 10).cuboid(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F), ModelTransform.NONE);
		modelPartData.addChild("right_pages", ModelPartBuilder.create().uv(12, 10).cuboid(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F), ModelTransform.NONE);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(24, 10).cuboid(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
		modelPartData.addChild("flip_page1", modelPartBuilder, ModelTransform.NONE);
		modelPartData.addChild("flip_page2", modelPartBuilder, ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
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
