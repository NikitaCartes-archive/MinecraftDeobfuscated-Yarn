/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface ParticleFactory<T extends ParticleEffect> {
    @Nullable
    public Particle createParticle(T var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13);
}

