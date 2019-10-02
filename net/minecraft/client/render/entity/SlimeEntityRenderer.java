/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityRenderer
extends MobEntityRenderer<SlimeEntity, SlimeEntityModel<SlimeEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/slime/slime.png");

    public SlimeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SlimeEntityModel(16), 0.25f);
        this.addFeature(new SlimeOverlayFeatureRenderer<SlimeEntity>(this));
    }

    public void method_4117(SlimeEntity slimeEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        this.field_4673 = 0.25f * (float)slimeEntity.getSize();
        super.method_4072(slimeEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    protected void method_4118(SlimeEntity slimeEntity, MatrixStack matrixStack, float f) {
        float g = 0.999f;
        matrixStack.scale(0.999f, 0.999f, 0.999f);
        matrixStack.translate(0.0, 0.001f, 0.0);
        float h = slimeEntity.getSize();
        float i = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch) / (h * 0.5f + 1.0f);
        float j = 1.0f / (i + 1.0f);
        matrixStack.scale(j * h, 1.0f / j * h, j * h);
    }

    public Identifier method_4116(SlimeEntity slimeEntity) {
        return SKIN;
    }
}

