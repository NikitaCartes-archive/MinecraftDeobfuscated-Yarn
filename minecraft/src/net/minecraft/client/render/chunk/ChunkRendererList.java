package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public abstract class ChunkRendererList {
	private double cameraX;
	private double cameraY;
	private double cameraZ;
	protected final List<ChunkRenderer> chunkRenderers = Lists.<ChunkRenderer>newArrayListWithCapacity(17424);
	protected boolean isCameraPositionSet;

	public void setCameraPosition(double d, double e, double f) {
		this.isCameraPositionSet = true;
		this.chunkRenderers.clear();
		this.cameraX = d;
		this.cameraY = e;
		this.cameraZ = f;
	}

	public void method_3157(ChunkRenderer chunkRenderer) {
		BlockPos blockPos = chunkRenderer.method_3670();
		GlStateManager.translatef(
			(float)((double)blockPos.getX() - this.cameraX), (float)((double)blockPos.getY() - this.cameraY), (float)((double)blockPos.getZ() - this.cameraZ)
		);
	}

	public void method_3159(ChunkRenderer chunkRenderer, BlockRenderLayer blockRenderLayer) {
		this.chunkRenderers.add(chunkRenderer);
	}

	public abstract void render(BlockRenderLayer blockRenderLayer);
}
