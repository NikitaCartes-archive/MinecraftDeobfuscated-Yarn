/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SilverfishEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SilverfishEntityRenderer
extends MobEntityRenderer<SilverfishEntity, SilverfishEntityModel<SilverfishEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/silverfish.png");

    public SilverfishEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SilverfishEntityModel(context.getPart(EntityModelLayers.SILVERFISH)), 0.3f);
    }

    @Override
    protected float getLyingAngle(SilverfishEntity silverfishEntity) {
        return 180.0f;
    }

    @Override
    public Identifier getTexture(SilverfishEntity silverfishEntity) {
        return TEXTURE;
    }

    @Override
    protected /* synthetic */ float getLyingAngle(LivingEntity entity) {
        return this.getLyingAngle((SilverfishEntity)entity);
    }

    @Override
    public /* synthetic */ Identifier getTexture(Entity entity) {
        return this.getTexture((SilverfishEntity)entity);
    }
}

