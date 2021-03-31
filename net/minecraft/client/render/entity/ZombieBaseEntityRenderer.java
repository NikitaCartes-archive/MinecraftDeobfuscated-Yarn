/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, M extends ZombieEntityModel<T>>
extends BipedEntityRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");

    protected ZombieBaseEntityRenderer(EntityRendererFactory.Context ctx, M bodyModel, M legsArmorModel, M bodyArmorModel) {
        super(ctx, bodyModel, 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, legsArmorModel, bodyArmorModel));
    }

    @Override
    public Identifier getTexture(ZombieEntity zombieEntity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(T zombieEntity) {
        return super.isShaking(zombieEntity) || ((ZombieEntity)zombieEntity).isConvertingInWater();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity entity) {
        return this.isShaking((T)((ZombieEntity)entity));
    }
}

