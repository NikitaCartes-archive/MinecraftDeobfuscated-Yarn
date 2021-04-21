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
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class SwimStatsRenameFix
extends DataFix {
    private final String field_33560;
    private final Map<String, String> field_33561;

    public SwimStatsRenameFix(Schema outputSchema, String string, Map<String, String> map) {
        super(outputSchema, false);
        this.field_33560 = string;
        this.field_33561 = map;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(TypeReferences.STATS);
        Type<?> type2 = this.getInputSchema().getType(TypeReferences.STATS);
        OpticFinder<?> opticFinder = type2.findField("stats");
        OpticFinder<?> opticFinder2 = opticFinder.type().findField("minecraft:custom");
        OpticFinder<String> opticFinder3 = IdentifierNormalizingSchema.getIdentifierType().finder();
        return this.fixTypeEverywhereTyped(this.field_33560, type2, type, (Typed<?> typed) -> typed.updateTyped(opticFinder, typed2 -> typed2.updateTyped(opticFinder2, typed -> typed.update(opticFinder3, string -> {
            for (Map.Entry<String, String> entry : this.field_33561.entrySet()) {
                if (!string.equals(entry.getKey())) continue;
                return entry.getValue();
            }
            return string;
        }))));
    }
}

