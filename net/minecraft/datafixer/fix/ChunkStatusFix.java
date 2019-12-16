/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;

public class ChunkStatusFix
extends DataFix {
    public ChunkStatusFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
        Type<?> type2 = type.findFieldType("Level");
        OpticFinder<?> opticFinder = DSL.fieldFinder("Level", type2);
        return this.fixTypeEverywhereTyped("ChunkStatusFix", type, this.getOutputSchema().getType(TypeReferences.CHUNK), (Typed<?> typed2) -> typed2.updateTyped(opticFinder, typed -> {
            Dynamic dynamic = typed.get(DSL.remainderFinder());
            String string = dynamic.get("Status").asString("empty");
            if (Objects.equals(string, "postprocessed")) {
                dynamic = dynamic.set("Status", dynamic.createString("fullchunk"));
            }
            return typed.set(DSL.remainderFinder(), dynamic);
        }));
    }
}

