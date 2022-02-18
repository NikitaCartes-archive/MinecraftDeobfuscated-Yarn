/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class TicksInWrongChunkFix
extends DataFix {
    public TicksInWrongChunkFix(Schema schema) {
        super(schema, false);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
        OpticFinder<?> opticFinder = type.findField("block_ticks");
        return this.fixTypeEverywhereTyped("Handle ticks saved in the wrong chunk", type, typed -> {
            Optional optional = typed.getOptionalTyped(opticFinder);
            Optional optional2 = optional.isPresent() ? optional.get().write().result() : Optional.empty();
            return typed.update(DSL.remainderFinder(), dynamic -> {
                int i = dynamic.get("xPos").asInt(0);
                int j = dynamic.get("zPos").asInt(0);
                Optional optional2 = dynamic.get("fluid_ticks").get().result();
                dynamic = TicksInWrongChunkFix.method_40439(dynamic, i, j, optional2, "neighbor_block_ticks");
                dynamic = TicksInWrongChunkFix.method_40439(dynamic, i, j, optional2, "neighbor_fluid_ticks");
                return dynamic;
            });
        });
    }

    private static Dynamic<?> method_40439(Dynamic<?> dynamic2, int i, int j, Optional<? extends Dynamic<?>> optional, String string) {
        List<Dynamic> list;
        if (optional.isPresent() && !(list = optional.get().asStream().filter(dynamic -> {
            int k = dynamic.get("x").asInt(0);
            int l = dynamic.get("z").asInt(0);
            int m = Math.abs(i - (k >> 4));
            int n = Math.abs(j - (l >> 4));
            return (m != 0 || n != 0) && m <= 1 && n <= 1;
        }).toList()).isEmpty()) {
            dynamic2 = dynamic2.set("UpgradeData", dynamic2.get("UpgradeData").orElseEmptyMap().set(string, dynamic2.createList(list.stream())));
        }
        return dynamic2;
    }
}

