/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombieHorseEntityRenderer
extends AbstractHorseEntityRenderer<AbstractHorseEntity, HorseEntityModel<AbstractHorseEntity>> {
    private static final Map<EntityType<?>, Identifier> TEXTURES = Maps.newHashMap(ImmutableMap.of(EntityType.ZOMBIE_HORSE, new Identifier("textures/entity/horse/horse_zombie.png"), EntityType.SKELETON_HORSE, new Identifier("textures/entity/horse/horse_skeleton.png")));

    public ZombieHorseEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx, new HorseEntityModel(ctx.getPart(layer)), 1.0f);
    }

    @Override
    public Identifier getTexture(AbstractHorseEntity abstractHorseEntity) {
        return TEXTURES.get(abstractHorseEntity.getType());
    }
}

