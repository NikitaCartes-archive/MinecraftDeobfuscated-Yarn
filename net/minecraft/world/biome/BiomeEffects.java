/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import java.util.Optional;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.BiomeParticleConfig;

public class BiomeEffects {
    private final int fogColor;
    private final int waterColor;
    private final int waterFogColor;
    private final Optional<BiomeParticleConfig> particleConfig;

    private BiomeEffects(int fogColor, int waterColor, int waterFogColor, Optional<BiomeParticleConfig> particleConfig) {
        this.fogColor = fogColor;
        this.waterColor = waterColor;
        this.waterFogColor = waterFogColor;
        this.particleConfig = particleConfig;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFogColor() {
        return this.fogColor;
    }

    @Environment(value=EnvType.CLIENT)
    public int getWaterColor() {
        return this.waterColor;
    }

    @Environment(value=EnvType.CLIENT)
    public int getWaterFogColor() {
        return this.waterFogColor;
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeParticleConfig> getParticleConfig() {
        return this.particleConfig;
    }

    public static class Builder {
        private OptionalInt fogColor = OptionalInt.empty();
        private OptionalInt waterColor = OptionalInt.empty();
        private OptionalInt waterFogColor = OptionalInt.empty();
        private Optional<BiomeParticleConfig> particleConfig = Optional.empty();

        public Builder fogColor(int fogColor) {
            this.fogColor = OptionalInt.of(fogColor);
            return this;
        }

        public Builder waterColor(int waterColor) {
            this.waterColor = OptionalInt.of(waterColor);
            return this;
        }

        public Builder waterFogColor(int waterFogColor) {
            this.waterFogColor = OptionalInt.of(waterFogColor);
            return this;
        }

        public Builder particleConfig(BiomeParticleConfig particleConfig) {
            this.particleConfig = Optional.of(particleConfig);
            return this;
        }

        public BiomeEffects build() {
            return new BiomeEffects(this.fogColor.orElseThrow(() -> new IllegalStateException("Missing 'fog' color.")), this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")), this.waterFogColor.orElseThrow(() -> new IllegalStateException("Missing 'water fog' color.")), this.particleConfig);
        }
    }
}

