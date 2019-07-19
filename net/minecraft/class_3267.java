/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class class_3267
implements DecoratorConfig {
    public final int field_14192;

    public class_3267(int i) {
        this.field_14192 = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("chance"), dynamicOps.createInt(this.field_14192))));
    }

    public static class_3267 method_14415(Dynamic<?> dynamic) {
        int i = dynamic.get("chance").asInt(0);
        return new class_3267(i);
    }
}

