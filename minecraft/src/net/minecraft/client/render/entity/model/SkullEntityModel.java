package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class SkullEntityModel extends Model {
	protected final ModelPart skull;

	public SkullEntityModel() {
		this(0, 35, 64, 64);
	}

	public SkullEntityModel(int i, int j, int k, int l) {
		super(RenderLayer::getEntityTranslucent);
		this.textureWidth = k;
		this.textureHeight = l;
		this.skull = new ModelPart(this, i, j);
		this.skull.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F);
		this.skull.setPivot(0.0F, 0.0F, 0.0F);
	}

	public void render(float f, float g, float h) {
		this.skull.yaw = g * (float) (Math.PI / 180.0);
		this.skull.pitch = h * (float) (Math.PI / 180.0);
	}

	@Override
	public void renderItem(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
		this.skull.render(matrixStack, vertexConsumer, 0.0625F, i, j, null, f, g, h);
	}
}
