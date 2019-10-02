package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class DragonHeadEntityModel extends SkullEntityModel {
	private final ModelPart head;
	private final ModelPart jaw;

	public DragonHeadEntityModel(float f) {
		this.textureWidth = 256;
		this.textureHeight = 256;
		float g = -16.0F;
		this.head = new ModelPart(this);
		this.head.addCuboid("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, f, 176, 44);
		this.head.addCuboid("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, f, 112, 30);
		this.head.mirror = true;
		this.head.addCuboid("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.head.addCuboid("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.head.mirror = false;
		this.head.addCuboid("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.head.addCuboid("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.jaw = new ModelPart(this);
		this.jaw.setPivot(0.0F, 4.0F, -8.0F);
		this.jaw.addCuboid("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, f, 176, 65);
		this.head.addChild(this.jaw);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, float f, float g, float h, float i, int j) {
		this.jaw.pitch = (float)(Math.sin((double)(f * (float) Math.PI * 0.2F)) + 1.0) * 0.2F;
		this.head.yaw = g * (float) (Math.PI / 180.0);
		this.head.pitch = h * (float) (Math.PI / 180.0);
		matrixStack.push();
		matrixStack.translate(0.0, -0.374375F, 0.0);
		matrixStack.scale(0.75F, 0.75F, 0.75F);
		this.head.render(matrixStack, vertexConsumer, i, j, null);
		matrixStack.pop();
	}
}
