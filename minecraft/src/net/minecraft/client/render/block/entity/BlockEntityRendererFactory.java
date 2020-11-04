package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5599;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.EntityModelLayer;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface BlockEntityRendererFactory<T extends BlockEntity> {
	BlockEntityRenderer<T> create(BlockEntityRendererFactory.Context ctx);

	@Environment(EnvType.CLIENT)
	public static class Context {
		private final BlockEntityRenderDispatcher renderDispatcher;
		private final BlockRenderManager renderManager;
		private final class_5599 layerRenderDispatcher;
		private final TextRenderer textRenderer;

		public Context(BlockEntityRenderDispatcher renderDispatcher, BlockRenderManager renderManager, class_5599 layerRenderDispatcher, TextRenderer textRenderer) {
			this.renderDispatcher = renderDispatcher;
			this.renderManager = renderManager;
			this.layerRenderDispatcher = layerRenderDispatcher;
			this.textRenderer = textRenderer;
		}

		public BlockEntityRenderDispatcher getRenderDispatcher() {
			return this.renderDispatcher;
		}

		public BlockRenderManager getRenderManager() {
			return this.renderManager;
		}

		public class_5599 getLayerRenderDispatcher() {
			return this.layerRenderDispatcher;
		}

		public ModelPart getLayerModelPart(EntityModelLayer modelLayer) {
			return this.layerRenderDispatcher.method_32072(modelLayer);
		}

		public TextRenderer getTextRenderer() {
			return this.textRenderer;
		}
	}
}
