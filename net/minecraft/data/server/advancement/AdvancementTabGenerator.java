/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.advancement;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.registry.RegistryWrapper;

public interface AdvancementTabGenerator {
    public void accept(RegistryWrapper.WrapperLookup var1, Consumer<Advancement> var2);
}

