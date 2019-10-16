/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GhastEntityRenderer
extends MobEntityRenderer<GhastEntity, GhastEntityModel<GhastEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/ghast/ghast.png");
    private static final Identifier ANGRY_SKIN = new Identifier("textures/entity/ghast/ghast_shooting.png");

    public GhastEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new GhastEntityModel(), 1.5f);
    }

    public Identifier method_3972(GhastEntity ghastEntity) {
        if (ghastEntity.isShooting()) {
            return ANGRY_SKIN;
        }
        return SKIN;
    }

    protected void method_3973(GhastEntity ghastEntity, MatrixStack matrixStack, float f) {
        float g = 1.0f;
        float h = 4.5f;
        float i = 4.5f;
        matrixStack.scale(4.5f, 4.5f, 4.5f);
    }
}

