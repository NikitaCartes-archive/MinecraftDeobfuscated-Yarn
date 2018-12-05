package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public abstract class class_752 {
	private double field_3954;
	private double field_3953;
	private double field_3957;
	protected List<ChunkRenderer> field_3955 = Lists.<ChunkRenderer>newArrayListWithCapacity(17424);
	protected boolean field_3956;

	public void method_3158(double d, double e, double f) {
		this.field_3956 = true;
		this.field_3955.clear();
		this.field_3954 = d;
		this.field_3953 = e;
		this.field_3957 = f;
	}

	public void method_3157(ChunkRenderer chunkRenderer) {
		BlockPos blockPos = chunkRenderer.method_3670();
		GlStateManager.translatef(
			(float)((double)blockPos.getX() - this.field_3954), (float)((double)blockPos.getY() - this.field_3953), (float)((double)blockPos.getZ() - this.field_3957)
		);
	}

	public void method_3159(ChunkRenderer chunkRenderer, BlockRenderLayer blockRenderLayer) {
		this.field_3955.add(chunkRenderer);
	}

	public abstract void method_3160(BlockRenderLayer blockRenderLayer);
}
