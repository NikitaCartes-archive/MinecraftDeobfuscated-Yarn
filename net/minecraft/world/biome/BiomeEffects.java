/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeParticleConfig;

public class BiomeEffects {
    public static final Codec<BiomeEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("fog_color")).forGetter(biomeEffects -> biomeEffects.fogColor), ((MapCodec)Codec.INT.fieldOf("water_color")).forGetter(biomeEffects -> biomeEffects.waterColor), ((MapCodec)Codec.INT.fieldOf("water_fog_color")).forGetter(biomeEffects -> biomeEffects.waterFogColor), ((MapCodec)Codec.INT.fieldOf("sky_color")).forGetter(biomeEffects -> biomeEffects.skyColor), Codec.INT.optionalFieldOf("foliage_color").forGetter(biomeEffects -> biomeEffects.foliageColor), Codec.INT.optionalFieldOf("grass_color").forGetter(biomeEffects -> biomeEffects.grassColor), GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", GrassColorModifier.NONE).forGetter(biomeEffects -> biomeEffects.grassColorModifier), BiomeParticleConfig.CODEC.optionalFieldOf("particle").forGetter(biomeEffects -> biomeEffects.particleConfig), SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(biomeEffects -> biomeEffects.loopSound), BiomeMoodSound.CODEC.optionalFieldOf("mood_sound").forGetter(biomeEffects -> biomeEffects.moodSound), BiomeAdditionsSound.CODEC.optionalFieldOf("additions_sound").forGetter(biomeEffects -> biomeEffects.additionsSound), MusicSound.CODEC.optionalFieldOf("music").forGetter(biomeEffects -> biomeEffects.music)).apply((Applicative<BiomeEffects, ?>)instance, BiomeEffects::new));
    private final int fogColor;
    private final int waterColor;
    private final int waterFogColor;
    private final int skyColor;
    private final Optional<Integer> foliageColor;
    private final Optional<Integer> grassColor;
    private final GrassColorModifier grassColorModifier;
    private final Optional<BiomeParticleConfig> particleConfig;
    private final Optional<SoundEvent> loopSound;
    private final Optional<BiomeMoodSound> moodSound;
    private final Optional<BiomeAdditionsSound> additionsSound;
    private final Optional<MusicSound> music;

    private BiomeEffects(int fogColor, int waterColor, int waterFogColor, int skyColor, Optional<Integer> foliageColor, Optional<Integer> grassColor, GrassColorModifier grassColorModifier, Optional<BiomeParticleConfig> particleConfig, Optional<SoundEvent> loopSound, Optional<BiomeMoodSound> moodSound, Optional<BiomeAdditionsSound> additionsSound, Optional<MusicSound> music) {
        this.fogColor = fogColor;
        this.waterColor = waterColor;
        this.waterFogColor = waterFogColor;
        this.skyColor = skyColor;
        this.foliageColor = foliageColor;
        this.grassColor = grassColor;
        this.grassColorModifier = grassColorModifier;
        this.particleConfig = particleConfig;
        this.loopSound = loopSound;
        this.moodSound = moodSound;
        this.additionsSound = additionsSound;
        this.music = music;
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
    public int getSkyColor() {
        return this.skyColor;
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<Integer> getFoliageColor() {
        return this.foliageColor;
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<Integer> getGrassColor() {
        return this.grassColor;
    }

    @Environment(value=EnvType.CLIENT)
    public GrassColorModifier getGrassColorModifier() {
        return this.grassColorModifier;
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeParticleConfig> getParticleConfig() {
        return this.particleConfig;
    }

    /**
     * Returns the loop sound.
     * 
     * <p>A loop sound is played continuously as an ambient sound whenever the
     * player is in the biome with this effect.
     */
    @Environment(value=EnvType.CLIENT)
    public Optional<SoundEvent> getLoopSound() {
        return this.loopSound;
    }

    /**
     * Returns the mood sound.
     * 
     * <p>A mood sound is played once every 6000 to 17999 ticks as an ambient
     * sound whenever the player is in the biome with this effect and near a
     * position that has 0 sky light and less than 7 combined light.
     * 
     * <p>Overworld biomes have the regular cave sound as their mood sound,
     * while three nether biomes in 20w10a have their dedicated mood sounds.
     */
    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeMoodSound> getMoodSound() {
        return this.moodSound;
    }

    /**
     * Returns the additions sound.
     * 
     * <p>An additions sound is played at 1.1% chance every tick as an ambient
     * sound whenever the player is in the biome with this effect.
     */
    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeAdditionsSound> getAdditionsSound() {
        return this.additionsSound;
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<MusicSound> getMusic() {
        return this.music;
    }

    public static enum GrassColorModifier implements StringIdentifiable
    {
        NONE("none"){

            @Override
            @Environment(value=EnvType.CLIENT)
            public int getModifiedGrassColor(double x, double z, int color) {
                return color;
            }
        }
        ,
        DARK_FOREST("dark_forest"){

            @Override
            @Environment(value=EnvType.CLIENT)
            public int getModifiedGrassColor(double x, double z, int color) {
                return (color & 0xFEFEFE) + 2634762 >> 1;
            }
        }
        ,
        SWAMP("swamp"){

            @Override
            @Environment(value=EnvType.CLIENT)
            public int getModifiedGrassColor(double x, double z, int color) {
                double d = Biome.FOLIAGE_NOISE.sample(x * 0.0225, z * 0.0225, false);
                if (d < -0.1) {
                    return 5011004;
                }
                return 6975545;
            }
        };

        private final String name;
        public static final Codec<GrassColorModifier> CODEC;
        private static final Map<String, GrassColorModifier> BY_NAME;

        @Environment(value=EnvType.CLIENT)
        public abstract int getModifiedGrassColor(double var1, double var3, int var5);

        private GrassColorModifier(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static GrassColorModifier byName(String name) {
            return BY_NAME.get(name);
        }

        static {
            CODEC = StringIdentifiable.createCodec(GrassColorModifier::values, GrassColorModifier::byName);
            BY_NAME = Arrays.stream(GrassColorModifier.values()).collect(Collectors.toMap(GrassColorModifier::getName, grassColorModifier -> grassColorModifier));
        }
    }

    public static class Builder {
        private OptionalInt fogColor = OptionalInt.empty();
        private OptionalInt waterColor = OptionalInt.empty();
        private OptionalInt waterFogColor = OptionalInt.empty();
        private OptionalInt skyColor = OptionalInt.empty();
        private Optional<Integer> foliageColor = Optional.empty();
        private Optional<Integer> grassColor = Optional.empty();
        private GrassColorModifier grassColorModifier = GrassColorModifier.NONE;
        private Optional<BiomeParticleConfig> particleConfig = Optional.empty();
        private Optional<SoundEvent> loopSound = Optional.empty();
        private Optional<BiomeMoodSound> moodSound = Optional.empty();
        private Optional<BiomeAdditionsSound> additionsSound = Optional.empty();
        private Optional<MusicSound> musicSound = Optional.empty();

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

        public Builder skyColor(int skyColor) {
            this.skyColor = OptionalInt.of(skyColor);
            return this;
        }

        public Builder foliageColor(int foliageColor) {
            this.foliageColor = Optional.of(foliageColor);
            return this;
        }

        public Builder grassColor(int grassColor) {
            this.grassColor = Optional.of(grassColor);
            return this;
        }

        public Builder grassColorModifier(GrassColorModifier grassColorModifier) {
            this.grassColorModifier = grassColorModifier;
            return this;
        }

        public Builder particleConfig(BiomeParticleConfig particleConfig) {
            this.particleConfig = Optional.of(particleConfig);
            return this;
        }

        public Builder loopSound(SoundEvent sound) {
            this.loopSound = Optional.of(sound);
            return this;
        }

        public Builder moodSound(BiomeMoodSound moodSound) {
            this.moodSound = Optional.of(moodSound);
            return this;
        }

        public Builder additionsSound(BiomeAdditionsSound additionsSound) {
            this.additionsSound = Optional.of(additionsSound);
            return this;
        }

        public Builder music(MusicSound music) {
            this.musicSound = Optional.of(music);
            return this;
        }

        public BiomeEffects build() {
            return new BiomeEffects(this.fogColor.orElseThrow(() -> new IllegalStateException("Missing 'fog' color.")), this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")), this.waterFogColor.orElseThrow(() -> new IllegalStateException("Missing 'water fog' color.")), this.skyColor.orElseThrow(() -> new IllegalStateException("Missing 'sky' color.")), this.foliageColor, this.grassColor, this.grassColorModifier, this.particleConfig, this.loopSound, this.moodSound, this.additionsSound, this.musicSound);
        }
    }
}

