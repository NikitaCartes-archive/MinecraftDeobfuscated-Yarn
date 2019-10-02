/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class StrayOverlayFeatureRenderer<T extends MobEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    private static final Identifier SKIN = new Identifier("textures/entity/skeleton/stray_overlay.png");
    private final StrayEntityModel<T> model = new StrayEntityModel(0.25f, true);

    public StrayOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_23204(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T mobEntity, float f, float g, float h, float j, float k, float l, float m) {
        FeatureRenderer.method_23195(this.getModel(), this.model, SKIN, matrixStack, layeredVertexConsumerStorage, i, mobEntity, f, g, j, k, l, m, h);
    }
}

