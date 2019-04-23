/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;

public class ParticleType<T extends ParticleEffect> {
    private final boolean shouldAlwaysSpawn;
    private final ParticleEffect.Factory<T> parametersFactory;

    protected ParticleType(boolean bl, ParticleEffect.Factory<T> factory) {
        this.shouldAlwaysSpawn = bl;
        this.parametersFactory = factory;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldAlwaysSpawn() {
        return this.shouldAlwaysSpawn;
    }

    public ParticleEffect.Factory<T> getParametersFactory() {
        return this.parametersFactory;
    }
}

