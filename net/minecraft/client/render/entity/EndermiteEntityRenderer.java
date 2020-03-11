/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EndermiteEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EndermiteEntityRenderer
extends MobEntityRenderer<EndermiteEntity, EndermiteEntityModel<EndermiteEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/endermite.png");

    public EndermiteEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EndermiteEntityModel(), 0.3f);
    }

    @Override
    protected float getLyingAngle(EndermiteEntity endermiteEntity) {
        return 180.0f;
    }

    @Override
    public Identifier getTexture(EndermiteEntity endermiteEntity) {
        return TEXTURE;
    }

    @Override
    protected /* synthetic */ float getLyingAngle(LivingEntity entity) {
        return this.getLyingAngle((EndermiteEntity)entity);
    }

    @Override
    public /* synthetic */ Identifier getTexture(Entity entity) {
        return this.getTexture((EndermiteEntity)entity);
    }
}

