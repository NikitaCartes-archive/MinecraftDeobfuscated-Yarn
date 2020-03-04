/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseBaseEntity;

@Environment(value=EnvType.CLIENT)
public abstract class HorseBaseEntityRenderer<T extends HorseBaseEntity, M extends HorseEntityModel<T>>
extends MobEntityRenderer<T, M> {
    private final float scale;

    public HorseBaseEntityRenderer(EntityRenderDispatcher dispatcher, M model, float scale) {
        super(dispatcher, model, 0.75f);
        this.scale = scale;
    }

    @Override
    protected void scale(T horseBaseEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(this.scale, this.scale, this.scale);
        super.scale(horseBaseEntity, matrixStack, f);
    }
}

