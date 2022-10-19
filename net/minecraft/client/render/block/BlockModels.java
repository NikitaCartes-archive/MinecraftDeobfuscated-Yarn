/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(value=EnvType.CLIENT)
public class BlockModels {
    private Map<BlockState, BakedModel> models = Map.of();
    private final BakedModelManager modelManager;

    public BlockModels(BakedModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public Sprite getModelParticleSprite(BlockState state) {
        return this.getModel(state).getParticleSprite();
    }

    public BakedModel getModel(BlockState state) {
        BakedModel bakedModel = this.models.get(state);
        if (bakedModel == null) {
            bakedModel = this.modelManager.getMissingModel();
        }
        return bakedModel;
    }

    public BakedModelManager getModelManager() {
        return this.modelManager;
    }

    public void setModels(Map<BlockState, BakedModel> models) {
        this.models = models;
    }

    public static ModelIdentifier getModelId(BlockState state) {
        return BlockModels.getModelId(Registry.BLOCK.getId(state.getBlock()), state);
    }

    public static ModelIdentifier getModelId(Identifier id, BlockState state) {
        return new ModelIdentifier(id, BlockModels.propertyMapToString(state.getEntries()));
    }

    public static String propertyMapToString(Map<Property<?>, Comparable<?>> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(',');
            }
            Property<?> property = entry.getKey();
            stringBuilder.append(property.getName());
            stringBuilder.append('=');
            stringBuilder.append(BlockModels.propertyValueToString(property, entry.getValue()));
        }
        return stringBuilder.toString();
    }

    private static <T extends Comparable<T>> String propertyValueToString(Property<T> property, Comparable<?> value) {
        return property.name(value);
    }
}

