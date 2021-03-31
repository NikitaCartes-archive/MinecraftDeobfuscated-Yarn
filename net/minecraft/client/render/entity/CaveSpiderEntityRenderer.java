/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CaveSpiderEntityRenderer
extends SpiderEntityRenderer<CaveSpiderEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/spider/cave_spider.png");
    private static final float field_32918 = 0.7f;

    public CaveSpiderEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.CAVE_SPIDER);
        this.shadowRadius *= 0.7f;
    }

    @Override
    protected void scale(CaveSpiderEntity caveSpiderEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(0.7f, 0.7f, 0.7f);
    }

    @Override
    public Identifier getTexture(CaveSpiderEntity caveSpiderEntity) {
        return TEXTURE;
    }
}

