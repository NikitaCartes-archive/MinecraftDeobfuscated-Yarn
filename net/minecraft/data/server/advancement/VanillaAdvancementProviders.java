/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.advancement;

import java.util.List;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.advancement.AdvancementProvider;
import net.minecraft.data.server.advancement.AdventureTabAdvancementGenerator;
import net.minecraft.data.server.advancement.EndTabAdvancementGenerator;
import net.minecraft.data.server.advancement.HusbandryTabAdvancementGenerator;
import net.minecraft.data.server.advancement.NetherTabAdvancementGenerator;
import net.minecraft.data.server.advancement.StoryTabAdvancementGenerator;

public class VanillaAdvancementProviders {
    public static AdvancementProvider createVanillaProvider(DataOutput output) {
        return new AdvancementProvider(output, List.of(new EndTabAdvancementGenerator(), new HusbandryTabAdvancementGenerator(), new AdventureTabAdvancementGenerator(), new NetherTabAdvancementGenerator(), new StoryTabAdvancementGenerator()));
    }
}

