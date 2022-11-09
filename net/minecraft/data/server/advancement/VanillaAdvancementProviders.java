/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.advancement;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.advancement.AdvancementProvider;
import net.minecraft.data.server.advancement.AdventureTabAdvancementGenerator;
import net.minecraft.data.server.advancement.EndTabAdvancementGenerator;
import net.minecraft.data.server.advancement.HusbandryTabAdvancementGenerator;
import net.minecraft.data.server.advancement.NetherTabAdvancementGenerator;
import net.minecraft.data.server.advancement.StoryTabAdvancementGenerator;
import net.minecraft.registry.RegistryWrapper;

public class VanillaAdvancementProviders {
    public static AdvancementProvider createVanillaProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        return new AdvancementProvider(output, registryLookupFuture, List.of(new EndTabAdvancementGenerator(), new HusbandryTabAdvancementGenerator(), new AdventureTabAdvancementGenerator(), new NetherTabAdvancementGenerator(), new StoryTabAdvancementGenerator()));
    }
}

