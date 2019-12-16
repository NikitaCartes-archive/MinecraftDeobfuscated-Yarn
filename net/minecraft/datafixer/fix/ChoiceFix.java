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

public abstract class ChoiceFix
extends DataFix {
    private final String name;
    private final String choiceName;
    private final DSL.TypeReference type;

    public ChoiceFix(Schema outputSchema, boolean changesType, String name, DSL.TypeReference type, String choiceName) {
        super(outputSchema, changesType);
        this.name = name;
        this.type = type;
        this.choiceName = choiceName;
    }

    @Override
    public TypeRewriteRule makeRule() {
        OpticFinder<?> opticFinder = DSL.namedChoice(this.choiceName, this.getInputSchema().getChoiceType(this.type, this.choiceName));
        return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type), (Typed<?> typed) -> typed.updateTyped(opticFinder, this.getOutputSchema().getChoiceType(this.type, this.choiceName), this::transform));
    }

    protected abstract Typed<?> transform(Typed<?> var1);
}

