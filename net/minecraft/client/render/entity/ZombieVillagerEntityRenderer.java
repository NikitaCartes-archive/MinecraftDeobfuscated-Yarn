/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombieVillagerEntityRenderer
extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie_villager/zombie_villager.png");

    public ZombieVillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER)), 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)), context.getModelManager()));
        this.addFeature(new VillagerClothingFeatureRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>>(this, context.getResourceManager(), "zombie_villager"));
    }

    @Override
    public Identifier getTexture(ZombieVillagerEntity zombieVillagerEntity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(ZombieVillagerEntity zombieVillagerEntity) {
        return super.isShaking(zombieVillagerEntity) || zombieVillagerEntity.isConverting();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity entity) {
        return this.isShaking((ZombieVillagerEntity)entity);
    }
}

