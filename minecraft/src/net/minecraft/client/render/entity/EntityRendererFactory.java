package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface EntityRendererFactory<T extends Entity> {
	EntityRenderer<T> create(EntityRendererFactory.Context ctx);

	@Environment(EnvType.CLIENT)
	public static class Context {
		private final EntityRenderDispatcher renderDispatcher;
		private final ItemRenderer itemRenderer;
		private final BlockRenderManager blockRenderManager;
		private final HeldItemRenderer heldItemRenderer;
		private final ResourceManager resourceManager;
		private final EntityModelLoader modelLoader;
		private final TextRenderer textRenderer;

		public Context(
			EntityRenderDispatcher renderDispatcher,
			ItemRenderer itemRenderer,
			BlockRenderManager blockRenderManager,
			HeldItemRenderer heldItemRenderer,
			ResourceManager resourceManager,
			EntityModelLoader modelLoader,
			TextRenderer textRenderer
		) {
			this.renderDispatcher = renderDispatcher;
			this.itemRenderer = itemRenderer;
			this.blockRenderManager = blockRenderManager;
			this.heldItemRenderer = heldItemRenderer;
			this.resourceManager = resourceManager;
			this.modelLoader = modelLoader;
			this.textRenderer = textRenderer;
		}

		public EntityRenderDispatcher getRenderDispatcher() {
			return this.renderDispatcher;
		}

		public ItemRenderer getItemRenderer() {
			return this.itemRenderer;
		}

		public BlockRenderManager getBlockRenderManager() {
			return this.blockRenderManager;
		}

		public HeldItemRenderer getHeldItemRenderer() {
			return this.heldItemRenderer;
		}

		public ResourceManager getResourceManager() {
			return this.resourceManager;
		}

		public EntityModelLoader getModelLoader() {
			return this.modelLoader;
		}

		public BakedModelManager getModelManager() {
			return this.blockRenderManager.getModels().getModelManager();
		}

		public ModelPart getPart(EntityModelLayer layer) {
			return this.modelLoader.getModelPart(layer);
		}

		public TextRenderer getTextRenderer() {
			return this.textRenderer;
		}
	}
}
