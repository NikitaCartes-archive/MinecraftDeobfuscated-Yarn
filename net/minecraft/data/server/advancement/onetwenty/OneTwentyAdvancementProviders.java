/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.advancement.onetwenty;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.advancement.AdvancementProvider;
import net.minecraft.data.server.advancement.onetwenty.OneTwentyAdventureTabAdvancementGenerator;
import net.minecraft.data.server.advancement.onetwenty.OneTwentyHusbandryTabAdvancementGenerator;
import net.minecraft.registry.RegistryWrapper;

public class OneTwentyAdvancementProviders {
    public static AdvancementProvider createOneTwentyProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        return new AdvancementProvider(output, registryLookupFuture, List.of(new OneTwentyAdventureTabAdvancementGenerator(), new OneTwentyHusbandryTabAdvancementGenerator()));
    }
}

