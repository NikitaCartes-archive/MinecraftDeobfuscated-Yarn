/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.carver.CarverConfig;

public class DefaultCarverConfig
implements CarverConfig {
    public static final Codec<DefaultCarverConfig> CODEC = Codec.unit(() -> INSTANCE);
    public static final DefaultCarverConfig INSTANCE = new DefaultCarverConfig();
}

