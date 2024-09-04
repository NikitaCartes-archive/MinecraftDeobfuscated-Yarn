package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface EntityRendererFactory<T extends Entity> {
	EntityRenderer<T, ?> create(EntityRendererFactory.Context ctx);

	@Environment(EnvType.CLIENT)
	public static class Context {
		private final EntityRenderDispatcher renderDispatcher;
		private final ItemRenderer itemRenderer;
		private final MapRenderer mapRenderer;
		private final BlockRenderManager blockRenderManager;
		private final ResourceManager resourceManager;
		private final EntityModelLoader modelLoader;
		private final EquipmentModelLoader equipmentModelLoader;
		private final TextRenderer textRenderer;
		private final EquipmentRenderer equipmentRenderer;

		public Context(
			EntityRenderDispatcher renderDispatcher,
			ItemRenderer itemRenderer,
			MapRenderer mapRenderer,
			BlockRenderManager blockRenderManager,
			ResourceManager resourceManager,
			EntityModelLoader modelLoader,
			EquipmentModelLoader equipmentModelLoader,
			TextRenderer textRenderer
		) {
			this.renderDispatcher = renderDispatcher;
			this.itemRenderer = itemRenderer;
			this.mapRenderer = mapRenderer;
			this.blockRenderManager = blockRenderManager;
			this.resourceManager = resourceManager;
			this.modelLoader = modelLoader;
			this.equipmentModelLoader = equipmentModelLoader;
			this.textRenderer = textRenderer;
			this.equipmentRenderer = new EquipmentRenderer(equipmentModelLoader, this.getModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE));
		}

		public EntityRenderDispatcher getRenderDispatcher() {
			return this.renderDispatcher;
		}

		public ItemRenderer getItemRenderer() {
			return this.itemRenderer;
		}

		public MapRenderer getMapRenderer() {
			return this.mapRenderer;
		}

		public BlockRenderManager getBlockRenderManager() {
			return this.blockRenderManager;
		}

		public ResourceManager getResourceManager() {
			return this.resourceManager;
		}

		public EntityModelLoader getModelLoader() {
			return this.modelLoader;
		}

		public EquipmentModelLoader getEquipmentModelLoader() {
			return this.equipmentModelLoader;
		}

		public EquipmentRenderer getEquipmentRenderer() {
			return this.equipmentRenderer;
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
