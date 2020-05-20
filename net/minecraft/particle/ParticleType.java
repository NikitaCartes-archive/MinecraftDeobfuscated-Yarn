/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;

public abstract class ParticleType<T extends ParticleEffect> {
    private final boolean shouldAlwaysSpawn;
    private final ParticleEffect.Factory<T> parametersFactory;

    protected ParticleType(boolean shouldAlwaysShow, ParticleEffect.Factory<T> parametersFactory) {
        this.shouldAlwaysSpawn = shouldAlwaysShow;
        this.parametersFactory = parametersFactory;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldAlwaysSpawn() {
        return this.shouldAlwaysSpawn;
    }

    public ParticleEffect.Factory<T> getParametersFactory() {
        return this.parametersFactory;
    }

    public abstract Codec<T> method_29138();
}

