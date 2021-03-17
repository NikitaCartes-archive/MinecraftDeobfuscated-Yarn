/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverDebugConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.RavineCarverConfig;

public class ConfiguredCarvers {
    public static final ConfiguredCarver<CarverConfig> CAVE = ConfiguredCarvers.register("cave", Carver.CAVE.configure(new CarverConfig(0.33333334f, CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()))));
    public static final ConfiguredCarver<RavineCarverConfig> CANYON = ConfiguredCarvers.register("canyon", Carver.RAVINE.configure(new RavineCarverConfig(0.02f, CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()), YOffset.fixed(10), YOffset.fixed(67), UniformIntDistribution.of(3), UniformFloatProvider.create(0.75f, 0.25f), UniformFloatProvider.create(-0.125f, 0.25f), TrapezoidFloatProvider.create(0.0f, 6.0f, 2.0f), 3, UniformFloatProvider.create(0.75f, 0.25f), 1.0f, 0.0f)));
    public static final ConfiguredCarver<CarverConfig> OCEAN_CAVE = ConfiguredCarvers.register("ocean_cave", Carver.CAVE.configure(new CarverConfig(0.14285715f, CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()))));
    public static final ConfiguredCarver<CarverConfig> NETHER_CAVE = ConfiguredCarvers.register("nether_cave", Carver.NETHER_CAVE.configure(new CarverConfig(0.2f)));
    public static final ConfiguredCarver<RavineCarverConfig> CRACK = ConfiguredCarvers.register("crack", Carver.RAVINE.configure(new RavineCarverConfig(0.00125f, CarverDebugConfig.create(false, Blocks.OAK_BUTTON.getDefaultState()), YOffset.fixed(40), YOffset.fixed(80), UniformIntDistribution.of(6, 2), UniformFloatProvider.create(0.5f, 0.5f), UniformFloatProvider.create(-0.125f, 0.25f), UniformFloatProvider.create(0.0f, 1.0f), 6, UniformFloatProvider.create(0.25f, 0.75f), 0.0f, 5.0f)));

    private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
    }
}

