/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountDecoratorConfig
implements DecoratorConfig {
    public final int count;

    public CountDecoratorConfig(int i) {
        this.count = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count))));
    }

    public static CountDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("count").asInt(0);
        return new CountDecoratorConfig(i);
    }
}

