/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountDecoratorConfig> field_24985 = ((MapCodec)Codec.INT.fieldOf("count")).xmap(CountDecoratorConfig::new, countDecoratorConfig -> countDecoratorConfig.count).codec();
    public final int count;

    public CountDecoratorConfig(int count) {
        this.count = count;
    }
}

