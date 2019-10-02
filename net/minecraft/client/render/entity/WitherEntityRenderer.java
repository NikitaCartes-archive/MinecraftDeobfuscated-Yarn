/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class WitherEntityRenderer
extends MobEntityRenderer<WitherEntity, WitherEntityModel<WitherEntity>> {
    private static final Identifier INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
    private static final Identifier SKIN = new Identifier("textures/entity/wither/wither.png");

    public WitherEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WitherEntityModel(0.0f), 1.0f);
        this.addFeature(new WitherArmorFeatureRenderer(this));
    }

    public Identifier method_4153(WitherEntity witherEntity) {
        int i = witherEntity.getInvulTimer();
        if (i <= 0 || i <= 80 && i / 5 % 2 == 1) {
            return SKIN;
        }
        return INVINCIBLE_SKIN;
    }

    protected void method_4152(WitherEntity witherEntity, MatrixStack matrixStack, float f) {
        float g = 2.0f;
        int i = witherEntity.getInvulTimer();
        if (i > 0) {
            g -= ((float)i - f) / 220.0f * 0.5f;
        }
        matrixStack.scale(g, g, g);
    }
}

