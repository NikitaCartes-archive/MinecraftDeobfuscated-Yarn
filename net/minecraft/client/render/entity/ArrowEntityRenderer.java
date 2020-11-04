/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ArrowEntityRenderer
extends ProjectileEntityRenderer<ArrowEntity> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/projectiles/arrow.png");
    public static final Identifier TIPPED_TEXTURE = new Identifier("textures/entity/projectiles/tipped_arrow.png");

    public ArrowEntityRenderer(class_5617.class_5618 arg) {
        super(arg);
    }

    @Override
    public Identifier getTexture(ArrowEntity arrowEntity) {
        return arrowEntity.getColor() > 0 ? TIPPED_TEXTURE : TEXTURE;
    }
}

