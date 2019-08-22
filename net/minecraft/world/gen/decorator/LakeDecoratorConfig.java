/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class LakeDecoratorConfig
implements DecoratorConfig {
    public final int chance;

    public LakeDecoratorConfig(int i) {
        this.chance = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("chance"), dynamicOps.createInt(this.chance))));
    }

    public static LakeDecoratorConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("chance").asInt(0);
        return new LakeDecoratorConfig(i);
    }
}

