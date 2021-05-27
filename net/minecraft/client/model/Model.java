/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Represents a dynamic model which has its own render layers and custom rendering.
 */
@Environment(value=EnvType.CLIENT)
public abstract class Model {
    protected final Function<Identifier, RenderLayer> layerFactory;

    public Model(Function<Identifier, RenderLayer> layerFactory) {
        this.layerFactory = layerFactory;
    }

    /**
     * {@return the render layer for the corresponding texture}
     * 
     * @param texture the texture used for the render layer
     */
    public final RenderLayer getLayer(Identifier texture) {
        return this.layerFactory.apply(texture);
    }

    /**
     * Renders the model.
     * 
     * @param light the lightmap coordinates used for this model rendering
     */
    public abstract void render(MatrixStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8);
}

