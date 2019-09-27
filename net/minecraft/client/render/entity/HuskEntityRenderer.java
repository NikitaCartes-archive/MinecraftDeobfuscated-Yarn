/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
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

    protected void method_3985(ZombieEntity zombieEntity, class_4587 arg, float f) {
        float g = 1.0625f;
        arg.method_22905(1.0625f, 1.0625f, 1.0625f);
        super.scale(zombieEntity, arg, f);
    }

    @Override
    public Identifier method_4163(ZombieEntity zombieEntity) {
        return SKIN;
    }
}

