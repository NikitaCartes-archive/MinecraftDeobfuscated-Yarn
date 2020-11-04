/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SpectralArrowEntityRenderer
extends ProjectileEntityRenderer<SpectralArrowEntity> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/projectiles/spectral_arrow.png");

    public SpectralArrowEntityRenderer(class_5617.class_5618 arg) {
        super(arg);
    }

    @Override
    public Identifier getTexture(SpectralArrowEntity spectralArrowEntity) {
        return TEXTURE;
    }
}

