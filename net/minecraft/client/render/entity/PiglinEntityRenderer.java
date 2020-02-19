/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.PiglinBipedArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityRenderer
extends BipedEntityRenderer<PiglinEntity, PiglinEntityModel<PiglinEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/piglin/piglin.png");

    public PiglinEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PiglinEntityModel(0.0f, 128, 64), 0.5f);
        this.addFeature(new PiglinBipedArmorFeatureRenderer(this, new BipedEntityModel(0.5f), new BipedEntityModel(1.0f), PiglinEntityRenderer.createEarlessPiglinModel()));
    }

    private static <T extends PiglinEntity> PiglinEntityModel<T> createEarlessPiglinModel() {
        PiglinEntityModel piglinEntityModel = new PiglinEntityModel(1.0f, 64, 16);
        piglinEntityModel.leftEar.visible = false;
        piglinEntityModel.rightEar.visible = false;
        return piglinEntityModel;
    }

    @Override
    public Identifier getTexture(PiglinEntity piglinEntity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(PiglinEntity piglinEntity, MatrixStack matrixStack, float f, float g, float h) {
        if (piglinEntity.canConvert()) {
            g += (float)(Math.cos((double)piglinEntity.age * 3.25) * Math.PI * 0.5);
        }
        super.setupTransforms(piglinEntity, matrixStack, f, g, h);
    }
}

