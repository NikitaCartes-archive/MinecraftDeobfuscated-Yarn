/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class StatsRenameFix
extends DataFix {
    private final String name;
    private final Map<String, String> replacements;

    public StatsRenameFix(Schema outputSchema, String name, Map<String, String> replacements) {
        super(outputSchema, false);
        this.name = name;
        this.replacements = replacements;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return TypeRewriteRule.seq(this.renameStats(), this.renameObjectives());
    }

    private TypeRewriteRule renameObjectives() {
        Type<?> type = this.getOutputSchema().getType(TypeReferences.OBJECTIVE);
        Type<?> type2 = this.getInputSchema().getType(TypeReferences.OBJECTIVE);
        OpticFinder<?> opticFinder = type2.findField("CriteriaType");
        TaggedChoice.TaggedChoiceType<?> taggedChoiceType = opticFinder.type().findChoiceType("type", -1).orElseThrow(() -> new IllegalStateException("Can't find choice type for criteria"));
        Type<?> type3 = taggedChoiceType.types().get("minecraft:custom");
        if (type3 == null) {
            throw new IllegalStateException("Failed to find custom criterion type variant");
        }
        OpticFinder<?> opticFinder2 = DSL.namedChoice("minecraft:custom", type3);
        OpticFinder<String> opticFinder3 = DSL.fieldFinder("id", IdentifierNormalizingSchema.getIdentifierType());
        return this.fixTypeEverywhereTyped(this.name, type2, type, (Typed<?> typed) -> typed.updateTyped(opticFinder, typed2 -> typed2.updateTyped(opticFinder2, typed -> typed.update(opticFinder3, old -> this.replacements.getOrDefault(old, (String)old)))));
    }

    private TypeRewriteRule renameStats() {
        Type<?> type = this.getOutputSchema().getType(TypeReferences.STATS);
        Type<?> type2 = this.getInputSchema().getType(TypeReferences.STATS);
        OpticFinder<?> opticFinder = type2.findField("stats");
        OpticFinder<?> opticFinder2 = opticFinder.type().findField("minecraft:custom");
        OpticFinder<String> opticFinder3 = IdentifierNormalizingSchema.getIdentifierType().finder();
        return this.fixTypeEverywhereTyped(this.name, type2, type, (Typed<?> typed) -> typed.updateTyped(opticFinder, typed2 -> typed2.updateTyped(opticFinder2, typed -> typed.update(opticFinder3, old -> this.replacements.getOrDefault(old, (String)old)))));
    }
}

