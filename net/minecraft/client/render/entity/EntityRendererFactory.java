/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface EntityRendererFactory<T extends Entity> {
    public EntityRenderer<T> create(Context var1);

    @Environment(value=EnvType.CLIENT)
    public static class Context {
        private final EntityRenderDispatcher renderDispatcher;
        private final ItemRenderer itemRenderer;
        private final ResourceManager resourceManager;
        private final EntityModelLoader modelLoader;
        private final TextRenderer textRenderer;

        public Context(EntityRenderDispatcher renderDispatcher, ItemRenderer itemRenderer, ResourceManager resourceManager, EntityModelLoader modelLoader, TextRenderer textRenderer) {
            this.renderDispatcher = renderDispatcher;
            this.itemRenderer = itemRenderer;
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

        public ResourceManager getResourceManager() {
            return this.resourceManager;
        }

        public EntityModelLoader getModelLoader() {
            return this.modelLoader;
        }

        public ModelPart getPart(EntityModelLayer layer) {
            return this.modelLoader.getModelPart(layer);
        }

        public TextRenderer getTextRenderer() {
            return this.textRenderer;
        }
    }
}

