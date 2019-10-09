package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ShieldEntityModel extends Model {
	private final ModelPart field_3550;
	private final ModelPart field_3551;

	public ShieldEntityModel() {
		super(RenderLayer::getEntitySolid);
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3550 = new ModelPart(this, 0, 0);
		this.field_3550.addCuboid(-6.0F, -11.0F, -2.0F, 12.0F, 22.0F, 1.0F, 0.0F);
		this.field_3551 = new ModelPart(this, 26, 0);
		this.field_3551.addCuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F, 0.0F);
	}

	@Override
	public void renderItem(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
		this.field_3550.render(matrixStack, vertexConsumer, 0.0625F, i, j, null, f, g, h);
		this.field_3551.render(matrixStack, vertexConsumer, 0.0625F, i, j, null, f, g, h);
	}
}
