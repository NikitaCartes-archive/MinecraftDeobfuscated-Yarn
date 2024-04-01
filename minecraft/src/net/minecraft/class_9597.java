package net.minecraft;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientGridCarrierView;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.Grid;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;

@Environment(EnvType.CLIENT)
public class class_9597 {
	private static final int field_51061 = 4096;
	private final BlockRenderManager field_51062;
	private final class_9597.class_9598 field_51063;

	public class_9597(BlockRenderManager blockRenderManager, class_9597.class_9598 arg) {
		this.field_51062 = blockRenderManager;
		this.field_51063 = arg;
	}

	public class_9597.class_9599 method_59308() {
		Reference2ObjectMap<RenderLayer, BufferBuilder> reference2ObjectMap = new Reference2ObjectArrayMap<>();
		MatrixStack matrixStack = new MatrixStack();
		Random random = Random.create();

		for (BlockPos blockPos : this.field_51063) {
			BlockState blockState = this.field_51063.getBlockState(blockPos);
			FluidState fluidState = blockState.getFluidState();
			if (!fluidState.isEmpty()) {
				BufferBuilder bufferBuilder = method_59310(reference2ObjectMap, RenderLayers.getFluidLayer(fluidState));
				this.field_51062.renderFluid(blockPos, this.field_51063, bufferBuilder, blockState, fluidState, blockPos.getX(), blockPos.getY(), blockPos.getZ());
			}

			if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				BufferBuilder bufferBuilder = method_59310(reference2ObjectMap, RenderLayers.getBlockLayer(blockState));
				matrixStack.push();
				matrixStack.translate((float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ());
				this.field_51062.renderBlock(blockState, blockPos, this.field_51063, matrixStack, bufferBuilder, true, random);
				matrixStack.pop();
			}
		}

		return new class_9597.class_9599(reference2ObjectMap);
	}

	private static BufferBuilder method_59310(Reference2ObjectMap<RenderLayer, BufferBuilder> reference2ObjectMap, RenderLayer renderLayer) {
		return (BufferBuilder)reference2ObjectMap.computeIfAbsent(renderLayer, renderLayerx -> {
			BufferBuilder bufferBuilder = new BufferBuilder(4096);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			return bufferBuilder;
		});
	}

	@Environment(EnvType.CLIENT)
	public static record class_9598(World level, Grid blocks, RegistryEntry<Biome> biome) implements BlockRenderView, Iterable<BlockPos> {
		public static class_9597.class_9598 method_59311(ClientGridCarrierView clientGridCarrierView) {
			return new class_9597.class_9598(clientGridCarrierView.getWorld(), clientGridCarrierView.getGrid().copy(), clientGridCarrierView.getBiome());
		}

		@Override
		public float getBrightness(Direction direction, boolean shaded) {
			return this.level.getBrightness(direction, shaded);
		}

		@Override
		public LightingProvider getLightingProvider() {
			return class_9516.field_50527;
		}

		@Override
		public int getColor(BlockPos pos, ColorResolver colorResolver) {
			return colorResolver.getColor(this.biome.value(), (double)pos.getX(), (double)pos.getZ());
		}

		@Nullable
		@Override
		public BlockEntity getBlockEntity(BlockPos pos) {
			return null;
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			return this.blocks.getBlockState(pos);
		}

		@Override
		public FluidState getFluidState(BlockPos pos) {
			return this.getBlockState(pos).getFluidState();
		}

		@Override
		public boolean isPotato() {
			return false;
		}

		@Override
		public int getHeight() {
			return this.blocks.getYSize();
		}

		@Override
		public int getBottomY() {
			return 0;
		}

		public Iterator<BlockPos> iterator() {
			return BlockPos.iterate(0, 0, 0, this.blocks.getXSize() - 1, this.blocks.getYSize() - 1, this.blocks.getZSize() - 1).iterator();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_9599 implements AutoCloseable {
		private final Reference2ObjectMap<RenderLayer, BufferBuilder> field_51064;

		public class_9599(Reference2ObjectMap<RenderLayer, BufferBuilder> reference2ObjectMap) {
			this.field_51064 = reference2ObjectMap;
		}

		public void method_59313(Reference2ObjectMap<RenderLayer, VertexBuffer> reference2ObjectMap) {
			for (RenderLayer renderLayer : RenderLayer.getBlockLayers()) {
				BufferBuilder.BuiltBuffer builtBuffer = this.method_59312(renderLayer);
				if (builtBuffer == null) {
					VertexBuffer vertexBuffer = reference2ObjectMap.remove(renderLayer);
					if (vertexBuffer != null) {
						vertexBuffer.close();
					}
				} else {
					VertexBuffer vertexBuffer = reference2ObjectMap.get(renderLayer);
					if (vertexBuffer == null) {
						vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
						reference2ObjectMap.put(renderLayer, vertexBuffer);
					}

					vertexBuffer.bind();
					vertexBuffer.upload(builtBuffer);
				}
			}
		}

		@Nullable
		public BufferBuilder.BuiltBuffer method_59312(RenderLayer renderLayer) {
			BufferBuilder bufferBuilder = this.field_51064.get(renderLayer);
			return bufferBuilder != null ? bufferBuilder.endNullable() : null;
		}

		public void close() {
			this.field_51064.values().forEach(BufferBuilder::close);
		}
	}
}
