package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public abstract class ChunkRendererList {
	private double cameraX;
	private double cameraY;
	private double cameraZ;
	protected final List<ChunkRenderer> chunkRenderers = Lists.<ChunkRenderer>newArrayListWithCapacity(17424);
	protected boolean isCameraPositionSet;

	public void setCameraPosition(double x, double y, double z) {
		this.isCameraPositionSet = true;
		this.chunkRenderers.clear();
		this.cameraX = x;
		this.cameraY = y;
		this.cameraZ = z;
	}

	public void translateToOrigin(ChunkRenderer renderer) {
		BlockPos blockPos = renderer.getOrigin();
		GlStateManager.translatef(
			(float)((double)blockPos.getX() - this.cameraX), (float)((double)blockPos.getY() - this.cameraY), (float)((double)blockPos.getZ() - this.cameraZ)
		);
	}

	public void add(ChunkRenderer chunkRenderer, RenderLayer layer) {
		this.chunkRenderers.add(chunkRenderer);
	}

	public abstract void render(RenderLayer layer);
}
