package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
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
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());

		for (int i = 0; i < this.positions.size(); i++) {
			BlockPos blockPos = (BlockPos)this.positions.get(i);
			Float float_ = (Float)this.sizes.get(i);
			float f = float_ / 2.0F;
			WorldRenderer.renderFilledBox(
				matrices,
				vertexConsumer,
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
	}
}
