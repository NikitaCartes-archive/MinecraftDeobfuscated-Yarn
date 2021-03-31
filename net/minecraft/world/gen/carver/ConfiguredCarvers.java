/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import net.minecraft.block.Blocks;
import net.minecraft.class_6108;
import net.minecraft.class_6120;
import net.minecraft.class_6124;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverDebugConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.RavineCarverConfig;

public class ConfiguredCarvers {
    public static final ConfiguredCarver<class_6108> OLD_CAVE = ConfiguredCarvers.register("old_cave", Carver.CAVE.configure(new class_6108(0.14285715f, class_6120.method_35377(YOffset.fixed(0), YOffset.fixed(127), 8), ConstantFloatProvider.create(0.5f), YOffset.aboveBottom(11), CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(-0.7f))));
    public static final ConfiguredCarver<class_6108> CAVE = ConfiguredCarvers.register("cave", Carver.CAVE.configure(new class_6108(0.33333334f, class_6124.method_35396(YOffset.aboveBottom(8), YOffset.fixed(126)), UniformFloatProvider.create(0.1f, 0.9f), YOffset.aboveBottom(9), CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()), UniformFloatProvider.create(0.3f, 1.8f), UniformFloatProvider.create(0.5f, 1.8f), UniformFloatProvider.create(-1.0f, 0.0f))));
    public static final ConfiguredCarver<RavineCarverConfig> OLD_CANYON = ConfiguredCarvers.register("old_canyon", Carver.RAVINE.configure(new RavineCarverConfig(0.02f, class_6120.method_35377(YOffset.fixed(20), YOffset.fixed(67), 8), ConstantFloatProvider.create(3.0f), YOffset.aboveBottom(11), CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()), UniformFloatProvider.create(-0.125f, 0.125f), new RavineCarverConfig.class_6107(UniformFloatProvider.create(0.75f, 1.0f), TrapezoidFloatProvider.create(0.0f, 6.0f, 2.0f), 3, UniformFloatProvider.create(0.75f, 1.0f), 1.0f, 0.0f))));
    public static final ConfiguredCarver<RavineCarverConfig> CANYON = ConfiguredCarvers.register("canyon", Carver.RAVINE.configure(new RavineCarverConfig(0.02f, class_6124.method_35396(YOffset.fixed(10), YOffset.fixed(67)), ConstantFloatProvider.create(3.0f), YOffset.aboveBottom(9), CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()), UniformFloatProvider.create(-0.125f, 0.125f), new RavineCarverConfig.class_6107(UniformFloatProvider.create(0.75f, 1.0f), TrapezoidFloatProvider.create(0.0f, 6.0f, 2.0f), 3, UniformFloatProvider.create(0.75f, 1.0f), 1.0f, 0.0f))));
    public static final ConfiguredCarver<class_6108> OLD_OCEAN_CAVE = ConfiguredCarvers.register("old_ocean_cave", Carver.CAVE.configure(new class_6108(0.06666667f, class_6120.method_35377(YOffset.fixed(0), YOffset.fixed(127), 8), ConstantFloatProvider.create(0.5f), YOffset.aboveBottom(11), CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(-0.7f))));
    public static final ConfiguredCarver<class_6108> OCEAN_CAVE = ConfiguredCarvers.register("ocean_cave", Carver.CAVE.configure(new class_6108(0.14285715f, class_6120.method_35377(YOffset.fixed(0), YOffset.fixed(127), 8), UniformFloatProvider.create(0.1f, 0.9f), YOffset.aboveBottom(9), CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), UniformFloatProvider.create(-1.0f, 0.0f))));
    public static final ConfiguredCarver<class_6108> NETHER_CAVE = ConfiguredCarvers.register("nether_cave", Carver.NETHER_CAVE.configure(new class_6108(0.2f, class_6124.method_35396(YOffset.fixed(0), YOffset.belowTop(1)), ConstantFloatProvider.create(0.5f), YOffset.aboveBottom(11), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(-0.7f))));
    public static final ConfiguredCarver<RavineCarverConfig> CRACK = ConfiguredCarvers.register("crack", Carver.RAVINE.configure(new RavineCarverConfig(0.00125f, class_6124.method_35396(YOffset.fixed(40), YOffset.fixed(80)), UniformFloatProvider.create(6.0f, 8.0f), YOffset.aboveBottom(9), CarverDebugConfig.create(false, Blocks.OAK_BUTTON.getDefaultState()), UniformFloatProvider.create(-0.125f, 0.125f), new RavineCarverConfig.class_6107(UniformFloatProvider.create(0.5f, 1.0f), UniformFloatProvider.create(0.0f, 1.0f), 6, UniformFloatProvider.create(0.25f, 1.0f), 0.0f, 5.0f))));

    private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
    }
}

