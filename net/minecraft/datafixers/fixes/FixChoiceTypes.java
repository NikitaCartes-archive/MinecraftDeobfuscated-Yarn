/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice;

public class FixChoiceTypes
extends DataFix {
    private final String name;
    private final DSL.TypeReference types;

    public FixChoiceTypes(Schema schema, String string, DSL.TypeReference typeReference) {
        super(schema, true);
        this.name = string;
        this.types = typeReference;
    }

    @Override
    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType<?> taggedChoiceType = this.getInputSchema().findChoiceType(this.types);
        TaggedChoice.TaggedChoiceType<?> taggedChoiceType2 = this.getOutputSchema().findChoiceType(this.types);
        return this.method_15476(this.name, taggedChoiceType, taggedChoiceType2);
    }

    protected final <K> TypeRewriteRule method_15476(String string, TaggedChoice.TaggedChoiceType<K> taggedChoiceType, TaggedChoice.TaggedChoiceType<?> taggedChoiceType2) {
        if (taggedChoiceType.getKeyType() != taggedChoiceType2.getKeyType()) {
            throw new IllegalStateException("Could not inject: key type is not the same");
        }
        TaggedChoice.TaggedChoiceType<?> taggedChoiceType3 = taggedChoiceType2;
        return this.fixTypeEverywhere(string, taggedChoiceType, taggedChoiceType3, dynamicOps -> pair -> {
            if (!taggedChoiceType3.hasType(pair.getFirst())) {
                throw new IllegalArgumentException(String.format("Unknown type %s in %s ", pair.getFirst(), this.types));
            }
            return pair;
        });
    }
}

