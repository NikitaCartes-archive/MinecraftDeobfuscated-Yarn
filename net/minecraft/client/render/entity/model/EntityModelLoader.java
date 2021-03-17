/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;

@Environment(value=EnvType.CLIENT)
public class EntityModelLoader
implements SynchronousResourceReloader {
    private Map<EntityModelLayer, TexturedModelData> modelParts = ImmutableMap.of();

    public ModelPart getModelPart(EntityModelLayer layer) {
        TexturedModelData texturedModelData = this.modelParts.get(layer);
        if (texturedModelData == null) {
            throw new IllegalArgumentException("No model for layer " + layer);
        }
        return texturedModelData.createModel();
    }

    @Override
    public void reload(ResourceManager manager) {
        this.modelParts = ImmutableMap.copyOf(EntityModels.getModels());
    }
}

