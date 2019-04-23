/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class NopeDecoratorConfig
implements DecoratorConfig {
    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.emptyMap());
    }

    public static NopeDecoratorConfig deserialize(Dynamic<?> dynamic) {
        return new NopeDecoratorConfig();
    }
}

