package net.minecraft;

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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class class_9810 {
	private final BlockRenderManager field_52164;
	private final BlockEntityRenderDispatcher field_52165;

	public class_9810(BlockRenderManager blockRenderManager, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		this.field_52164 = blockRenderManager;
		this.field_52165 = blockEntityRenderDispatcher;
	}

	public class_9810.class_9811 method_60904(
		ChunkSectionPos chunkSectionPos, ChunkRendererRegion chunkRendererRegion, VertexSorter vertexSorter, BlockBufferBuilderStorage blockBufferBuilderStorage
	) {
		class_9810.class_9811 lv = new class_9810.class_9811();
		BlockPos blockPos = chunkSectionPos.getMinPos();
		BlockPos blockPos2 = blockPos.add(15, 15, 15);
		ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
		MatrixStack matrixStack = new MatrixStack();
		BlockModelRenderer.enableBrightnessCache();
		Map<RenderLayer, BufferBuilder> map = new Reference2ObjectArrayMap<>(RenderLayer.getBlockLayers().size());
		Random random = Random.create();

		for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
			BlockState blockState = chunkRendererRegion.getBlockState(blockPos3);
			if (blockState.isOpaqueFullCube(chunkRendererRegion, blockPos3)) {
				chunkOcclusionDataBuilder.markClosed(blockPos3);
			}

			if (blockState.hasBlockEntity()) {
				BlockEntity blockEntity = chunkRendererRegion.getBlockEntity(blockPos3);
				if (blockEntity != null) {
					this.method_60902(lv, blockEntity);
				}
			}

			FluidState fluidState = blockState.getFluidState();
			if (!fluidState.isEmpty()) {
				RenderLayer renderLayer = RenderLayers.getFluidLayer(fluidState);
				BufferBuilder bufferBuilder = this.method_60903(map, blockBufferBuilderStorage, renderLayer);
				this.field_52164.renderFluid(blockPos3, chunkRendererRegion, bufferBuilder, blockState, fluidState);
			}

			if (blockState.getRenderType() == BlockRenderType.MODEL) {
				RenderLayer renderLayer = RenderLayers.getBlockLayer(blockState);
				BufferBuilder bufferBuilder = this.method_60903(map, blockBufferBuilderStorage, renderLayer);
				matrixStack.push();
				matrixStack.translate(
					(float)ChunkSectionPos.getLocalCoord(blockPos3.getX()),
					(float)ChunkSectionPos.getLocalCoord(blockPos3.getY()),
					(float)ChunkSectionPos.getLocalCoord(blockPos3.getZ())
				);
				this.field_52164.renderBlock(blockState, blockPos3, chunkRendererRegion, matrixStack, bufferBuilder, true, random);
				matrixStack.pop();
			}
		}

		for (Entry<RenderLayer, BufferBuilder> entry : map.entrySet()) {
			RenderLayer renderLayer2 = (RenderLayer)entry.getKey();
			class_9801 lv2 = ((BufferBuilder)entry.getValue()).method_60794();
			if (lv2 != null) {
				if (renderLayer2 == RenderLayer.getTranslucent()) {
					lv.field_52170 = lv2.method_60819(blockBufferBuilderStorage.get(RenderLayer.getTranslucent()), vertexSorter);
				}

				lv.field_52168.put(renderLayer2, lv2);
			}
		}

		BlockModelRenderer.disableBrightnessCache();
		lv.field_52169 = chunkOcclusionDataBuilder.build();
		return lv;
	}

	private BufferBuilder method_60903(Map<RenderLayer, BufferBuilder> map, BlockBufferBuilderStorage blockBufferBuilderStorage, RenderLayer renderLayer) {
		BufferBuilder bufferBuilder = (BufferBuilder)map.get(renderLayer);
		if (bufferBuilder == null) {
			class_9799 lv = blockBufferBuilderStorage.get(renderLayer);
			bufferBuilder = new BufferBuilder(lv, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			map.put(renderLayer, bufferBuilder);
		}

		return bufferBuilder;
	}

	private <E extends BlockEntity> void method_60902(class_9810.class_9811 arg, E blockEntity) {
		BlockEntityRenderer<E> blockEntityRenderer = this.field_52165.get(blockEntity);
		if (blockEntityRenderer != null) {
			arg.field_52167.add(blockEntity);
			if (blockEntityRenderer.rendersOutsideBoundingBox(blockEntity)) {
				arg.field_52166.add(blockEntity);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class class_9811 {
		public final List<BlockEntity> field_52166 = new ArrayList();
		public final List<BlockEntity> field_52167 = new ArrayList();
		public final Map<RenderLayer, class_9801> field_52168 = new Reference2ObjectArrayMap<>();
		public ChunkOcclusionData field_52169 = new ChunkOcclusionData();
		@Nullable
		public class_9801.class_9802 field_52170;

		public void method_60905() {
			this.field_52168.values().forEach(class_9801::close);
		}
	}
}
