/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public abstract class EntityRenameFix
extends DataFix {
    private final String name;

    public EntityRenameFix(String name, Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType<?> taggedChoiceType = this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
        TaggedChoice.TaggedChoiceType<?> taggedChoiceType2 = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
        Type<Pair<String, String>> type = DSL.named(TypeReferences.ENTITY_NAME.typeName(), IdentifierNormalizingSchema.method_28295());
        if (!Objects.equals(this.getOutputSchema().getType(TypeReferences.ENTITY_NAME), type)) {
            throw new IllegalStateException("Entity name type is not what was expected.");
        }
        return TypeRewriteRule.seq(this.fixTypeEverywhere(this.name, taggedChoiceType, taggedChoiceType2, dynamicOps -> pair -> pair.mapFirst(string -> {
            String string2 = this.rename((String)string);
            Type<?> type = taggedChoiceType.types().get(string);
            Type<?> type2 = taggedChoiceType2.types().get(string2);
            if (!type2.equals(type, true, true)) {
                throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", type2, type));
            }
            return string2;
        })), this.fixTypeEverywhere(this.name + " for entity name", type, dynamicOps -> pair -> pair.mapSecond(this::rename)));
    }

    protected abstract String rename(String var1);
}

