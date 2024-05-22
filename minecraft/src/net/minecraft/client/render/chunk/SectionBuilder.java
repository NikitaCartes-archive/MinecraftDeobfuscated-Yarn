package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.systems.VertexSorter;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class SectionBuilder {
	private final BlockRenderManager blockRenderManager;
	private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

	public SectionBuilder(BlockRenderManager blockRenderManager, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		this.blockRenderManager = blockRenderManager;
		this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
	}

	public SectionBuilder.RenderData build(
		ChunkSectionPos sectionPos, ChunkRendererRegion renderRegion, VertexSorter vertexSorter, BlockBufferAllocatorStorage allocatorStorage
	) {
		SectionBuilder.RenderData renderData = new SectionBuilder.RenderData();
		BlockPos blockPos = sectionPos.getMinPos();
		BlockPos blockPos2 = blockPos.add(15, 15, 15);
		ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
		MatrixStack matrixStack = new MatrixStack();
		BlockModelRenderer.enableBrightnessCache();
		Map<RenderLayer, BufferBuilder> map = new Reference2ObjectArrayMap<>(RenderLayer.getBlockLayers().size());
		Random random = Random.create();

		for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
			BlockState blockState = renderRegion.getBlockState(blockPos3);
			if (blockState.isOpaqueFullCube(renderRegion, blockPos3)) {
				chunkOcclusionDataBuilder.markClosed(blockPos3);
			}

			if (blockState.hasBlockEntity()) {
				BlockEntity blockEntity = renderRegion.getBlockEntity(blockPos3);
				if (blockEntity != null) {
					this.addBlockEntity(renderData, blockEntity);
				}
			}

			FluidState fluidState = blockState.getFluidState();
			if (!fluidState.isEmpty()) {
				RenderLayer renderLayer = RenderLayers.getFluidLayer(fluidState);
				BufferBuilder bufferBuilder = this.beginBufferBuilding(map, allocatorStorage, renderLayer);
				this.blockRenderManager.renderFluid(blockPos3, renderRegion, bufferBuilder, blockState, fluidState);
			}

			if (blockState.getRenderType() == BlockRenderType.MODEL) {
				RenderLayer renderLayer = RenderLayers.getBlockLayer(blockState);
				BufferBuilder bufferBuilder = this.beginBufferBuilding(map, allocatorStorage, renderLayer);
				matrixStack.push();
				matrixStack.translate(
					(float)ChunkSectionPos.getLocalCoord(blockPos3.getX()),
					(float)ChunkSectionPos.getLocalCoord(blockPos3.getY()),
					(float)ChunkSectionPos.getLocalCoord(blockPos3.getZ())
				);
				this.blockRenderManager.renderBlock(blockState, blockPos3, renderRegion, matrixStack, bufferBuilder, true, random);
				matrixStack.pop();
			}
		}

		for (Entry<RenderLayer, BufferBuilder> entry : map.entrySet()) {
			RenderLayer renderLayer2 = (RenderLayer)entry.getKey();
			BuiltBuffer builtBuffer = ((BufferBuilder)entry.getValue()).endNullable();
			if (builtBuffer != null) {
				if (renderLayer2 == RenderLayer.getTranslucent()) {
					renderData.translucencySortingData = builtBuffer.sortQuads(allocatorStorage.get(RenderLayer.getTranslucent()), vertexSorter);
				}

				renderData.buffers.put(renderLayer2, builtBuffer);
			}
		}

		BlockModelRenderer.disableBrightnessCache();
		renderData.chunkOcclusionData = chunkOcclusionDataBuilder.build();
		return renderData;
	}

	private BufferBuilder beginBufferBuilding(Map<RenderLayer, BufferBuilder> builders, BlockBufferAllocatorStorage allocatorStorage, RenderLayer layer) {
		BufferBuilder bufferBuilder = (BufferBuilder)builders.get(layer);
		if (bufferBuilder == null) {
			BufferAllocator bufferAllocator = allocatorStorage.get(layer);
			bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			builders.put(layer, bufferBuilder);
		}

		return bufferBuilder;
	}

	private <E extends BlockEntity> void addBlockEntity(SectionBuilder.RenderData data, E blockEntity) {
		BlockEntityRenderer<E> blockEntityRenderer = this.blockEntityRenderDispatcher.get(blockEntity);
		if (blockEntityRenderer != null) {
			data.blockEntities.add(blockEntity);
			if (blockEntityRenderer.rendersOutsideBoundingBox(blockEntity)) {
				data.noCullingBlockEntities.add(blockEntity);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class RenderData {
		public final List<BlockEntity> noCullingBlockEntities = new ArrayList();
		public final List<BlockEntity> blockEntities = new ArrayList();
		public final Map<RenderLayer, BuiltBuffer> buffers = new Reference2ObjectArrayMap<>();
		public ChunkOcclusionData chunkOcclusionData = new ChunkOcclusionData();
		@Nullable
		public BuiltBuffer.SortState translucencySortingData;

		public void close() {
			this.buffers.values().forEach(BuiltBuffer::close);
		}
	}
}
