/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import net.minecraft.datafixer.TypeReferences;

public class UntaggedSpawnerFix
extends DataFix {
    public UntaggedSpawnerFix(Schema schema) {
        super(schema, true);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.UNTAGGED_SPAWNER);
        Type<?> type2 = this.getOutputSchema().getType(TypeReferences.UNTAGGED_SPAWNER);
        OpticFinder<?> opticFinder = type.findField("SpawnData");
        Type<?> type3 = type2.findField("SpawnData").type();
        OpticFinder<?> opticFinder2 = type.findField("SpawnPotentials");
        Type<?> type4 = type2.findField("SpawnPotentials").type();
        return this.fixTypeEverywhereTyped("Fix mob spawner data structure", type, type2, (Typed<?> typed2) -> typed2.updateTyped(opticFinder, type3, typed -> this.method_37974(type3, (Typed<?>)typed)).updateTyped(opticFinder2, type4, typed -> this.fixSpawner(type4, (Typed<?>)typed)));
    }

    private <T> Typed<T> method_37974(Type<T> type, Typed<?> typed) {
        DynamicOps<?> dynamicOps = typed.getOps();
        return new Typed(type, dynamicOps, Pair.of(typed.getValue(), new Dynamic(dynamicOps)));
    }

    private <T> Typed<T> fixSpawner(Type<T> type, Typed<?> typed) {
        DynamicOps<?> dynamicOps = typed.getOps();
        List list = (List)typed.getValue();
        List<Pair> list2 = list.stream().map(object -> {
            Pair pair = (Pair)object;
            int i = ((Dynamic)pair.getSecond()).get("Weight").asNumber().result().orElse(1).intValue();
            Dynamic dynamic = new Dynamic(dynamicOps);
            dynamic = dynamic.set("weight", dynamic.createInt(i));
            Dynamic dynamic2 = ((Dynamic)pair.getSecond()).remove("Weight").remove("Entity");
            return Pair.of(Pair.of(pair.getFirst(), dynamic2), dynamic);
        }).toList();
        return new Typed<List<Pair>>(type, dynamicOps, list2);
    }
}

