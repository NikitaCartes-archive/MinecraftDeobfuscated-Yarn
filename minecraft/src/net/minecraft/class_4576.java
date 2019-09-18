package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public abstract class class_4576<T extends BlockEntity> extends BlockEntityRenderer<T> {
	@Override
	public final void method_22747(
		T blockEntity, double d, double e, double f, float g, int i, BufferBuilder bufferBuilder, BlockRenderLayer blockRenderLayer, BlockPos blockPos
	) {
		this.method_22737(
			blockEntity,
			(double)blockPos.getX() - BlockEntityRenderDispatcher.renderOffsetX,
			(double)blockPos.getY() - BlockEntityRenderDispatcher.renderOffsetY,
			(double)blockPos.getZ() - BlockEntityRenderDispatcher.renderOffsetZ,
			g,
			i,
			blockRenderLayer,
			bufferBuilder
		);
	}

	private void method_22737(T blockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder) {
		World world = blockEntity.getWorld();
		int k;
		int l;
		if (world != null) {
			int j = world.getLightmapIndex(blockEntity.getPos());
			k = j >> 16;
			l = j & 65535;
		} else {
			k = 240;
			l = 240;
		}

		bufferBuilder.setOffset(d, e, f);
		this.method_22738(blockEntity, d, e, f, g, i, blockRenderLayer, bufferBuilder, k, l);
		bufferBuilder.setOffset(0.0, 0.0, 0.0);
	}

	@Override
	public final void render(T blockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
		this.method_22737(blockEntity, d, e, f, g, i, blockRenderLayer, bufferBuilder);
		bufferBuilder.setOffset(0.0, 0.0, 0.0);
		blockRenderLayer.method_22723();
		tessellator.draw();
		blockRenderLayer.method_22724();
	}

	protected abstract void method_22738(
		T blockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, int j, int k
	);

	protected Sprite method_22739(Identifier identifier) {
		return MinecraftClient.getInstance().getSpriteAtlas().getSprite(identifier);
	}
}
