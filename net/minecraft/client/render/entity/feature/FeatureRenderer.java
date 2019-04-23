/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class FeatureRenderer<T extends Entity, M extends EntityModel<T>> {
    private final FeatureRendererContext<T, M> context;

    public FeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        this.context = featureRendererContext;
    }

    public M getModel() {
        return this.context.getModel();
    }

    public void bindTexture(Identifier identifier) {
        this.context.bindTexture(identifier);
    }

    public void applyLightmapCoordinates(T entity) {
        this.context.applyLightmapCoordinates(entity);
    }

    public abstract void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);

    public abstract boolean hasHurtOverlay();
}

