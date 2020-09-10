package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ShieldEntityModel extends Model {
	private final ModelPart plate;
	private final ModelPart handle;

	public ShieldEntityModel() {
		super(RenderLayer::getEntitySolid);
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.plate = new ModelPart(this, 0, 0);
		this.plate.addCuboid(-6.0F, -11.0F, -2.0F, 12.0F, 22.0F, 1.0F, 0.0F);
		this.handle = new ModelPart(this, 26, 0);
		this.handle.addCuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F, 0.0F);
	}

	public ModelPart getPlate() {
		return this.plate;
	}

	public ModelPart getHandle() {
		return this.handle;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.plate.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		this.handle.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
