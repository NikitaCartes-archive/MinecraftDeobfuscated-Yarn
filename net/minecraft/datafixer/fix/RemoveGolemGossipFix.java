/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class RemoveGolemGossipFix
extends ChoiceFix {
    public RemoveGolemGossipFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "Remove Golem Gossip Fix", TypeReferences.ENTITY, "minecraft:villager");
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), RemoveGolemGossipFix::updateGossipsList);
    }

    private static Dynamic<?> updateGossipsList(Dynamic<?> villagerData) {
        return villagerData.update("Gossips", dynamic22 -> villagerData.createList(dynamic22.asStream().filter(dynamic -> !dynamic.get("Type").asString("").equals("golem"))));
    }
}

