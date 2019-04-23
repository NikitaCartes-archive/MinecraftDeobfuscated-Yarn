/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public interface DecoratorConfig {
    public static final NopeDecoratorConfig DEFAULT = new NopeDecoratorConfig();

    public <T> Dynamic<T> serialize(DynamicOps<T> var1);
}

