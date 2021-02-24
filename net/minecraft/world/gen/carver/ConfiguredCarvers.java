/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import net.minecraft.block.Blocks;
import net.minecraft.class_5865;
import net.minecraft.class_5866;
import net.minecraft.class_5869;
import net.minecraft.class_5871;
import net.minecraft.class_5872;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.ConfiguredCarver;

public class ConfiguredCarvers {
    public static final ConfiguredCarver<class_5871> CAVE = ConfiguredCarvers.register("cave", Carver.CAVE.configure(new class_5871(0.25f, class_5872.method_33972(false, Blocks.CRIMSON_BUTTON.getDefaultState()))));
    public static final ConfiguredCarver<class_5869> CANYON = ConfiguredCarvers.register("canyon", Carver.CANYON.configure(new class_5869(0.02f, class_5872.method_33972(false, Blocks.WARPED_BUTTON.getDefaultState()), YOffset.fixed(10), YOffset.fixed(67), UniformIntDistribution.of(3), class_5866.method_33934(0.75f, 0.25f), class_5866.method_33934(-0.125f, 0.25f), class_5865.method_33926(0.0f, 6.0f, 2.0f), 3, class_5866.method_33934(0.75f, 0.25f), 1.0f, 0.0f)));
    public static final ConfiguredCarver<class_5871> OCEAN_CAVE = ConfiguredCarvers.register("ocean_cave", Carver.CAVE.configure(new class_5871(0.125f, class_5872.method_33972(false, Blocks.CRIMSON_BUTTON.getDefaultState()))));
    public static final ConfiguredCarver<class_5871> NETHER_CAVE = ConfiguredCarvers.register("nether_cave", Carver.NETHER_CAVE.configure(new class_5871(0.2f)));
    public static final ConfiguredCarver<class_5869> CRACK = ConfiguredCarvers.register("crack", Carver.CANYON.configure(new class_5869(0.005f, class_5872.method_33972(false, Blocks.OAK_BUTTON.getDefaultState()), YOffset.fixed(40), YOffset.fixed(80), UniformIntDistribution.of(6, 2), class_5866.method_33934(0.5f, 0.5f), class_5866.method_33934(-0.125f, 0.25f), class_5866.method_33934(0.0f, 1.0f), 6, class_5866.method_33934(0.25f, 0.75f), 0.0f, 5.0f)));

    private static <WC extends class_5871> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
    }
}

