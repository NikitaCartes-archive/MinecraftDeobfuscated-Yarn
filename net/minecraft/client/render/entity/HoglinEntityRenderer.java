/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HoglinEntityRenderer
extends MobEntityRenderer<HoglinEntity, HoglinEntityModel<HoglinEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/hoglin/hoglin.png");

    public HoglinEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new HoglinEntityModel(), 0.7f);
    }

    @Override
    public Identifier getTexture(HoglinEntity hoglinEntity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(HoglinEntity hoglinEntity) {
        return hoglinEntity.canConvert();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity entity) {
        return this.isShaking((HoglinEntity)entity);
    }
}

