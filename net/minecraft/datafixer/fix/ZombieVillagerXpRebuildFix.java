/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;
import net.minecraft.datafixer.fix.VillagerXpRebuildFix;

public class ZombieVillagerXpRebuildFix
extends ChoiceFix {
    public ZombieVillagerXpRebuildFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "Zombie Villager XP rebuild", TypeReferences.ENTITY, "minecraft:zombie_villager");
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> {
            Optional<Number> optional = dynamic.get("Xp").asNumber();
            if (!optional.isPresent()) {
                int i = dynamic.get("VillagerData").get("level").asNumber().orElse(1).intValue();
                return dynamic.set("Xp", dynamic.createInt(VillagerXpRebuildFix.levelToXp(i)));
            }
            return dynamic;
        });
    }
}

