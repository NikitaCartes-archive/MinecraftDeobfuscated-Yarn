package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class SkullOverlayEntityModel extends SkullEntityModel {
	private final ModelPart skullOverlay = new ModelPart(this, 32, 0);

	public SkullOverlayEntityModel() {
		super(0, 0, 64, 64);
		this.skullOverlay.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.25F);
		this.skullOverlay.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(float f, float g, float h) {
		super.render(f, g, h);
		this.skullOverlay.yaw = this.skull.yaw;
		this.skullOverlay.pitch = this.skull.pitch;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		super.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.skullOverlay.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
