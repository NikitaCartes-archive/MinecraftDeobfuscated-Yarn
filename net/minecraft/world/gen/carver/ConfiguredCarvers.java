/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;

public class ConfiguredCarvers {
    public static final ConfiguredCarver<ProbabilityConfig> CAVE = ConfiguredCarvers.register("cave", Carver.CAVE.configure(new ProbabilityConfig(0.14285715f)));
    public static final ConfiguredCarver<ProbabilityConfig> CANYON = ConfiguredCarvers.register("canyon", Carver.CANYON.configure(new ProbabilityConfig(0.02f)));
    public static final ConfiguredCarver<ProbabilityConfig> OCEAN_CAVE = ConfiguredCarvers.register("ocean_cave", Carver.CAVE.configure(new ProbabilityConfig(0.06666667f)));
    public static final ConfiguredCarver<ProbabilityConfig> UNDERWATER_CANYON = ConfiguredCarvers.register("underwater_canyon", Carver.UNDERWATER_CANYON.configure(new ProbabilityConfig(0.02f)));
    public static final ConfiguredCarver<ProbabilityConfig> UNDERWATER_CAVE = ConfiguredCarvers.register("underwater_cave", Carver.UNDERWATER_CAVE.configure(new ProbabilityConfig(0.06666667f)));
    public static final ConfiguredCarver<ProbabilityConfig> NETHER_CAVE = ConfiguredCarvers.register("nether_cave", Carver.NETHER_CAVE.configure(new ProbabilityConfig(0.2f)));

    private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
    }
}

