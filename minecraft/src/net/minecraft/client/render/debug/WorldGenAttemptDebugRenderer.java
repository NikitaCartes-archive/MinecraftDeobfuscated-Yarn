package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class WorldGenAttemptDebugRenderer implements DebugRenderer.Renderer {
	private final List<BlockPos> positions = Lists.<BlockPos>newArrayList();
	private final List<Float> sizes = Lists.<Float>newArrayList();
	private final List<Float> alphas = Lists.<Float>newArrayList();
	private final List<Float> reds = Lists.<Float>newArrayList();
	private final List<Float> greens = Lists.<Float>newArrayList();
	private final List<Float> blues = Lists.<Float>newArrayList();

	public void addBox(BlockPos pos, float size, float red, float green, float blue, float alpha) {
		this.positions.add(pos);
		this.sizes.add(size);
		this.alphas.add(alpha);
		this.reds.add(red);
		this.greens.add(green);
		this.blues.add(blue);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

		for (int i = 0; i < this.positions.size(); i++) {
			BlockPos blockPos = (BlockPos)this.positions.get(i);
			Float float_ = (Float)this.sizes.get(i);
			float f = float_ / 2.0F;
			WorldRenderer.drawBox(
				bufferBuilder,
				(double)((float)blockPos.getX() + 0.5F - f) - cameraX,
				(double)((float)blockPos.getY() + 0.5F - f) - cameraY,
				(double)((float)blockPos.getZ() + 0.5F - f) - cameraZ,
				(double)((float)blockPos.getX() + 0.5F + f) - cameraX,
				(double)((float)blockPos.getY() + 0.5F + f) - cameraY,
				(double)((float)blockPos.getZ() + 0.5F + f) - cameraZ,
				(Float)this.reds.get(i),
				(Float)this.greens.get(i),
				(Float)this.blues.get(i),
				(Float)this.alphas.get(i)
			);
		}

		tessellator.draw();
		RenderSystem.enableTexture();
	}
}
