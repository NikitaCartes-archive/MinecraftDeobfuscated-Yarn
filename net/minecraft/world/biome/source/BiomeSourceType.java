/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceConfig;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.level.LevelProperties;

public class BiomeSourceType<C extends BiomeSourceConfig, T extends BiomeSource> {
    public static final BiomeSourceType<CheckerboardBiomeSourceConfig, CheckerboardBiomeSource> CHECKERBOARD = BiomeSourceType.register("checkerboard", CheckerboardBiomeSource::new, CheckerboardBiomeSourceConfig::new);
    public static final BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> FIXED = BiomeSourceType.register("fixed", FixedBiomeSource::new, FixedBiomeSourceConfig::new);
    public static final BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> VANILLA_LAYERED = BiomeSourceType.register("vanilla_layered", VanillaLayeredBiomeSource::new, VanillaLayeredBiomeSourceConfig::new);
    public static final BiomeSourceType<TheEndBiomeSourceConfig, TheEndBiomeSource> THE_END = BiomeSourceType.register("the_end", TheEndBiomeSource::new, TheEndBiomeSourceConfig::new);
    private final Function<C, T> biomeSource;
    private final Function<LevelProperties, C> config;

    private static <C extends BiomeSourceConfig, T extends BiomeSource> BiomeSourceType<C, T> register(String string, Function<C, T> function, Function<LevelProperties, C> function2) {
        return Registry.register(Registry.BIOME_SOURCE_TYPE, string, new BiomeSourceType<C, T>(function, function2));
    }

    public BiomeSourceType(Function<C, T> function, Function<LevelProperties, C> function2) {
        this.biomeSource = function;
        this.config = function2;
    }

    public T applyConfig(C biomeSourceConfig) {
        return (T)((BiomeSource)this.biomeSource.apply(biomeSourceConfig));
    }

    public C getConfig(LevelProperties levelProperties) {
        return (C)((BiomeSourceConfig)this.config.apply(levelProperties));
    }
}

