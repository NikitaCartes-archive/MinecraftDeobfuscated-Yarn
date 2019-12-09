/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class HeightmapRangeDecoratorConfig
implements DecoratorConfig {
    public final int min;
    public final int max;

    public HeightmapRangeDecoratorConfig(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString("min"), ops.createInt(this.min), ops.createString("max"), ops.createInt(this.max))));
    }

    public static HeightmapRangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("min").asInt(0);
        int j = dynamic.get("max").asInt(0);
        return new HeightmapRangeDecoratorConfig(i, j);
    }
}

