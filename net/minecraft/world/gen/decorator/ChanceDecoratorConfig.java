/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class ChanceDecoratorConfig
implements DecoratorConfig {
    public static final Codec<ChanceDecoratorConfig> field_24980 = ((MapCodec)Codec.INT.fieldOf("chance")).xmap(ChanceDecoratorConfig::new, chanceDecoratorConfig -> chanceDecoratorConfig.chance).codec();
    public final int chance;

    public ChanceDecoratorConfig(int chance) {
        this.chance = chance;
    }
}

