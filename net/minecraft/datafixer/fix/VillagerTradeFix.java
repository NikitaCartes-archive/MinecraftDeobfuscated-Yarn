/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class VillagerTradeFix
extends ChoiceFix {
    public VillagerTradeFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "Villager trade fix", TypeReferences.ENTITY, "minecraft:villager");
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        OpticFinder<?> opticFinder = inputType.getType().findField("Offers");
        OpticFinder<?> opticFinder2 = opticFinder.type().findField("Recipes");
        Type<?> type = opticFinder2.type();
        if (!(type instanceof List.ListType)) {
            throw new IllegalStateException("Recipes are expected to be a list.");
        }
        List.ListType listType = (List.ListType)type;
        Type type2 = listType.getElement();
        OpticFinder opticFinder3 = DSL.typeFinder(type2);
        OpticFinder<?> opticFinder4 = type2.findField("buy");
        OpticFinder<?> opticFinder5 = type2.findField("buyB");
        OpticFinder<?> opticFinder6 = type2.findField("sell");
        OpticFinder<Pair<String, String>> opticFinder7 = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        Function<Typed, Typed> function = typed -> this.fixPumpkinTrade(opticFinder7, (Typed<?>)typed);
        return inputType.updateTyped(opticFinder, typed -> typed.updateTyped(opticFinder2, typed2 -> typed2.updateTyped(opticFinder3, typed -> typed.updateTyped(opticFinder4, function).updateTyped(opticFinder5, function).updateTyped(opticFinder6, function))));
    }

    private Typed<?> fixPumpkinTrade(OpticFinder<Pair<String, String>> opticFinder, Typed<?> typed) {
        return typed.update(opticFinder, pair -> pair.mapSecond(string -> Objects.equals(string, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : string));
    }
}

