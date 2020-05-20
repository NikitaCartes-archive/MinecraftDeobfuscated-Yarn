/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.AbstractUuidFix;
import net.minecraft.datafixer.fix.ChoiceFix;

public class VillagerGossipFix
extends ChoiceFix {
    public VillagerGossipFix(Schema outputSchema, String choiceType) {
        super(outputSchema, false, "Gossip for for " + choiceType, TypeReferences.ENTITY, choiceType);
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("Gossips", dynamic -> DataFixUtils.orElse(dynamic.asStreamOpt().result().map(stream -> stream.map(dynamic -> AbstractUuidFix.updateRegularMostLeast(dynamic, "Target", "Target").orElse((Dynamic<?>)dynamic))).map(dynamic::createList), dynamic)));
    }
}

