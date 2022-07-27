package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface BlockEntityRendererFactory<T extends BlockEntity> {
	BlockEntityRenderer<T> create(BlockEntityRendererFactory.Context ctx);

	@Environment(EnvType.CLIENT)
	public static class Context {
		private final BlockEntityRenderDispatcher renderDispatcher;
		private final BlockRenderManager renderManager;
		private final ItemRenderer itemRenderer;
		private final EntityRenderDispatcher entityRenderDispatcher;
		private final EntityModelLoader layerRenderDispatcher;
		private final TextRenderer textRenderer;

		public Context(
			BlockEntityRenderDispatcher renderDispatcher,
			BlockRenderManager renderManager,
			ItemRenderer itemRenderer,
			EntityRenderDispatcher entityRenderDispatcher,
			EntityModelLoader layerRenderDispatcher,
			TextRenderer textRenderer
		) {
			this.renderDispatcher = renderDispatcher;
			this.renderManager = renderManager;
			this.itemRenderer = itemRenderer;
			this.entityRenderDispatcher = entityRenderDispatcher;
			this.layerRenderDispatcher = layerRenderDispatcher;
			this.textRenderer = textRenderer;
		}

		public BlockEntityRenderDispatcher getRenderDispatcher() {
			return this.renderDispatcher;
		}

		public BlockRenderManager getRenderManager() {
			return this.renderManager;
		}

		public EntityRenderDispatcher getEntityRenderDispatcher() {
			return this.entityRenderDispatcher;
		}

		public ItemRenderer getItemRenderer() {
			return this.itemRenderer;
		}

		public EntityModelLoader getLayerRenderDispatcher() {
			return this.layerRenderDispatcher;
		}

		public ModelPart getLayerModelPart(EntityModelLayer modelLayer) {
			return this.layerRenderDispatcher.getModelPart(modelLayer);
		}

		public TextRenderer getTextRenderer() {
			return this.textRenderer;
		}
	}
}
