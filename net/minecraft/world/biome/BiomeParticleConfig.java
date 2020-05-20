/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class BiomeParticleConfig {
    public static final Codec<BiomeParticleConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ParticleTypes.field_25125.fieldOf("options")).forGetter(biomeParticleConfig -> biomeParticleConfig.field_24676), ((MapCodec)Codec.FLOAT.fieldOf("probability")).forGetter(biomeParticleConfig -> Float.valueOf(biomeParticleConfig.chance))).apply((Applicative<BiomeParticleConfig, ?>)instance, BiomeParticleConfig::new));
    private final ParticleEffect field_24676;
    private final float chance;

    public BiomeParticleConfig(ParticleEffect particleEffect, float f) {
        this.field_24676 = particleEffect;
        this.chance = f;
    }

    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParticleType() {
        return this.field_24676;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldAddParticle(Random random) {
        return random.nextFloat() <= this.chance;
    }
}

