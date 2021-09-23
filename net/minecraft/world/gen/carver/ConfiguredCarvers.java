/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
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
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredCarvers {
    public static final ConfiguredCarver<CaveCarverConfig> CAVE = ConfiguredCarvers.register("cave", Carver.CAVE.configure(new CaveCarverConfig(0.15f, (HeightProvider)UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(180)), (FloatProvider)UniformFloatProvider.create(0.1f, 0.9f), YOffset.aboveBottom(8), CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()), (FloatProvider)UniformFloatProvider.create(0.7f, 1.4f), (FloatProvider)UniformFloatProvider.create(0.8f, 1.3f), (FloatProvider)UniformFloatProvider.create(-1.0f, -0.4f))));
    public static final ConfiguredCarver<CaveCarverConfig> CAVE_EXTRA_UNDERGROUND = ConfiguredCarvers.register("cave_extra_underground", Carver.CAVE.configure(new CaveCarverConfig(0.07f, (HeightProvider)UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(47)), (FloatProvider)UniformFloatProvider.create(0.1f, 0.9f), YOffset.aboveBottom(8), CarverDebugConfig.create(false, Blocks.OAK_BUTTON.getDefaultState()), (FloatProvider)UniformFloatProvider.create(0.7f, 1.4f), (FloatProvider)UniformFloatProvider.create(0.8f, 1.3f), (FloatProvider)UniformFloatProvider.create(-1.0f, -0.4f))));
    public static final ConfiguredCarver<RavineCarverConfig> CANYON = ConfiguredCarvers.register("canyon", Carver.RAVINE.configure(new RavineCarverConfig(0.01f, UniformHeightProvider.create(YOffset.fixed(10), YOffset.fixed(67)), ConstantFloatProvider.create(3.0f), YOffset.aboveBottom(8), CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()), UniformFloatProvider.create(-0.125f, 0.125f), new RavineCarverConfig.Shape(UniformFloatProvider.create(0.75f, 1.0f), TrapezoidFloatProvider.create(0.0f, 6.0f, 2.0f), 3, UniformFloatProvider.create(0.75f, 1.0f), 1.0f, 0.0f))));
    public static final ConfiguredCarver<CaveCarverConfig> NETHER_CAVE = ConfiguredCarvers.register("nether_cave", Carver.NETHER_CAVE.configure(new CaveCarverConfig(0.2f, (HeightProvider)UniformHeightProvider.create(YOffset.fixed(0), YOffset.belowTop(1)), (FloatProvider)ConstantFloatProvider.create(0.5f), YOffset.aboveBottom(10), false, (FloatProvider)ConstantFloatProvider.create(1.0f), (FloatProvider)ConstantFloatProvider.create(1.0f), (FloatProvider)ConstantFloatProvider.create(-0.7f))));

    private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
    }
}

