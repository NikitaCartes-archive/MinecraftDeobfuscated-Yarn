/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverDebugConfig;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.RavineCarverConfig;
import net.minecraft.world.gen.heightprovider.BiasedToBottomHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredCarvers {
    public static final ConfiguredCarver<CaveCarverConfig> CAVE = ConfiguredCarvers.register("cave", Carver.CAVE.configure(new CaveCarverConfig(0.14285715f, BiasedToBottomHeightProvider.create(YOffset.fixed(0), YOffset.fixed(127), 8), ConstantFloatProvider.create(0.5f), YOffset.aboveBottom(11), CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(-0.7f))));
    public static final ConfiguredCarver<CaveCarverConfig> PROTOTYPE_CAVE = ConfiguredCarvers.register("prototype_cave", Carver.CAVE.configure(new CaveCarverConfig(0.33333334f, UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(126)), UniformFloatProvider.create(0.1f, 0.9f), YOffset.aboveBottom(9), CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()), UniformFloatProvider.create(0.3f, 1.8f), UniformFloatProvider.create(0.5f, 1.8f), UniformFloatProvider.create(-1.0f, 0.0f))));
    public static final ConfiguredCarver<RavineCarverConfig> CANYON = ConfiguredCarvers.register("canyon", Carver.RAVINE.configure(new RavineCarverConfig(0.02f, BiasedToBottomHeightProvider.create(YOffset.fixed(20), YOffset.fixed(67), 8), ConstantFloatProvider.create(3.0f), YOffset.aboveBottom(11), CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()), UniformFloatProvider.create(-0.125f, 0.125f), new RavineCarverConfig.Shape(UniformFloatProvider.create(0.75f, 1.0f), TrapezoidFloatProvider.create(0.0f, 6.0f, 2.0f), 3, UniformFloatProvider.create(0.75f, 1.0f), 1.0f, 0.0f))));
    public static final ConfiguredCarver<RavineCarverConfig> PROTOTYPE_CANYON = ConfiguredCarvers.register("prototype_canyon", Carver.RAVINE.configure(new RavineCarverConfig(0.02f, UniformHeightProvider.create(YOffset.fixed(10), YOffset.fixed(67)), ConstantFloatProvider.create(3.0f), YOffset.aboveBottom(9), CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()), UniformFloatProvider.create(-0.125f, 0.125f), new RavineCarverConfig.Shape(UniformFloatProvider.create(0.75f, 1.0f), TrapezoidFloatProvider.create(0.0f, 6.0f, 2.0f), 3, UniformFloatProvider.create(0.75f, 1.0f), 1.0f, 0.0f))));
    public static final ConfiguredCarver<CaveCarverConfig> OCEAN_CAVE = ConfiguredCarvers.register("ocean_cave", Carver.CAVE.configure(new CaveCarverConfig(0.06666667f, BiasedToBottomHeightProvider.create(YOffset.fixed(0), YOffset.fixed(127), 8), ConstantFloatProvider.create(0.5f), YOffset.aboveBottom(11), CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(-0.7f))));
    public static final ConfiguredCarver<CaveCarverConfig> PROTOTYPE_OCEAN_CAVE = ConfiguredCarvers.register("prototype_ocean_cave", Carver.CAVE.configure(new CaveCarverConfig(0.14285715f, BiasedToBottomHeightProvider.create(YOffset.fixed(0), YOffset.fixed(127), 8), UniformFloatProvider.create(0.1f, 0.9f), YOffset.aboveBottom(9), CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), UniformFloatProvider.create(-1.0f, 0.0f))));
    public static final ConfiguredCarver<RavineCarverConfig> UNDERWATER_CANYON = ConfiguredCarvers.register("underwater_canyon", Carver.UNDERWATER_CANYON.configure(new RavineCarverConfig(0.02f, BiasedToBottomHeightProvider.create(YOffset.fixed(20), YOffset.fixed(67), 8), ConstantFloatProvider.create(3.0f), YOffset.aboveBottom(11), CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()), UniformFloatProvider.create(-0.125f, 0.125f), new RavineCarverConfig.Shape(UniformFloatProvider.create(0.75f, 1.0f), TrapezoidFloatProvider.create(0.0f, 6.0f, 2.0f), 3, UniformFloatProvider.create(0.75f, 1.0f), 1.0f, 0.0f))));
    public static final ConfiguredCarver<CaveCarverConfig> UNDERWATER_CAVE = ConfiguredCarvers.register("underwater_cave", Carver.UNDERWATER_CAVE.configure(new CaveCarverConfig(0.06666667f, BiasedToBottomHeightProvider.create(YOffset.fixed(0), YOffset.fixed(127), 8), ConstantFloatProvider.create(0.5f), YOffset.aboveBottom(11), CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(-0.7f))));
    public static final ConfiguredCarver<CaveCarverConfig> NETHER_CAVE = ConfiguredCarvers.register("nether_cave", Carver.NETHER_CAVE.configure(new CaveCarverConfig(0.2f, UniformHeightProvider.create(YOffset.fixed(0), YOffset.belowTop(1)), ConstantFloatProvider.create(0.5f), YOffset.aboveBottom(11), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(-0.7f))));
    public static final ConfiguredCarver<RavineCarverConfig> PROTOTYPE_CRACK = ConfiguredCarvers.register("prototype_crack", Carver.RAVINE.configure(new RavineCarverConfig(0.00125f, UniformHeightProvider.create(YOffset.fixed(40), YOffset.fixed(80)), UniformFloatProvider.create(6.0f, 8.0f), YOffset.aboveBottom(9), CarverDebugConfig.create(false, Blocks.OAK_BUTTON.getDefaultState()), UniformFloatProvider.create(-0.125f, 0.125f), new RavineCarverConfig.Shape(UniformFloatProvider.create(0.5f, 1.0f), UniformFloatProvider.create(0.0f, 1.0f), 6, UniformFloatProvider.create(0.25f, 1.0f), 0.0f, 5.0f))));

    private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
    }
}

