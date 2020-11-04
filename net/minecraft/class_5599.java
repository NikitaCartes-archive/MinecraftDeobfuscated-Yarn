/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5600;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;

@Environment(value=EnvType.CLIENT)
public class class_5599
implements SynchronousResourceReloadListener {
    private Map<EntityModelLayer, ModelPart> field_27542 = ImmutableMap.of();

    public ModelPart method_32072(EntityModelLayer entityModelLayer) {
        ModelPart modelPart = this.field_27542.get(entityModelLayer);
        if (modelPart == null) {
            throw new IllegalArgumentException("No model for layer " + entityModelLayer);
        }
        return modelPart;
    }

    @Override
    public void apply(ResourceManager manager) {
        this.field_27542 = ImmutableMap.copyOf(class_5600.method_32073());
    }
}

