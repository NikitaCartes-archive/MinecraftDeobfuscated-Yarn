/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PigZombieEntityRenderer
extends BipedEntityRenderer<ZombiePigmanEntity, ZombieEntityModel<ZombiePigmanEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/zombie_pigman.png");

    public PigZombieEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ZombieEntityModel(), 0.5f);
        this.addFeature(new ArmorBipedFeatureRenderer(this, new ZombieEntityModel(0.5f, true), new ZombieEntityModel(1.0f, true)));
    }

    protected Identifier method_4093(ZombiePigmanEntity zombiePigmanEntity) {
        return SKIN;
    }
}

