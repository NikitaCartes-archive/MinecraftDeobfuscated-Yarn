/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.TropicalFishSomethingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TropicalFishEntityRenderer
extends MobEntityRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
    private final TropicalFishEntityModelA<TropicalFishEntity> field_4800 = new TropicalFishEntityModelA();
    private final TropicalFishEntityModelB<TropicalFishEntity> field_4799 = new TropicalFishEntityModelB();

    public TropicalFishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new TropicalFishEntityModelA(), 0.15f);
        this.addFeature(new TropicalFishSomethingFeatureRenderer(this));
    }

    @Nullable
    protected Identifier method_4141(TropicalFishEntity tropicalFishEntity) {
        return tropicalFishEntity.getShapeId();
    }

    public void method_4140(TropicalFishEntity tropicalFishEntity, double d, double e, double f, float g, float h) {
        this.model = tropicalFishEntity.getShape() == 0 ? this.field_4800 : this.field_4799;
        float[] fs = tropicalFishEntity.getBaseColorComponents();
        GlStateManager.color3f(fs[0], fs[1], fs[2]);
        super.method_4072(tropicalFishEntity, d, e, f, g, h);
    }

    protected void method_4142(TropicalFishEntity tropicalFishEntity, float f, float g, float h) {
        super.setupTransforms(tropicalFishEntity, f, g, h);
        float i = 4.3f * MathHelper.sin(0.6f * f);
        GlStateManager.rotatef(i, 0.0f, 1.0f, 0.0f);
        if (!tropicalFishEntity.isInsideWater()) {
            GlStateManager.translatef(0.2f, 0.1f, 0.0f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
    }
}

