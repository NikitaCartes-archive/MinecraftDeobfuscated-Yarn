/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HuskEntityRenderer
extends ZombieEntityRenderer {
    private static final Identifier SKIN = new Identifier("textures/entity/zombie/husk.png");

    public HuskEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    protected void method_3985(ZombieEntity zombieEntity, float f) {
        float g = 1.0625f;
        RenderSystem.scalef(1.0625f, 1.0625f, 1.0625f);
        super.scale(zombieEntity, f);
    }

    @Override
    protected Identifier method_4163(ZombieEntity zombieEntity) {
        return SKIN;
    }
}

