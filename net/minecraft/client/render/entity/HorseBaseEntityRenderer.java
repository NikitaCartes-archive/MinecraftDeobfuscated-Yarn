/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseBaseEntity;

@Environment(value=EnvType.CLIENT)
public abstract class HorseBaseEntityRenderer<T extends HorseBaseEntity, M extends HorseEntityModel<T>>
extends MobEntityRenderer<T, M> {
    private final float scale;

    public HorseBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M horseEntityModel, float f) {
        super(entityRenderDispatcher, horseEntityModel, 0.75f);
        this.scale = f;
    }

    protected void method_3874(T horseBaseEntity, float f) {
        RenderSystem.scalef(this.scale, this.scale, this.scale);
        super.scale(horseBaseEntity, f);
    }
}

