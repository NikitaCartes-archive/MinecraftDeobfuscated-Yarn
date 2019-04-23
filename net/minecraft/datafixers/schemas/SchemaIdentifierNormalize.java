/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.Identifier;

public class SchemaIdentifierNormalize
extends Schema {
    public SchemaIdentifierNormalize(int i, Schema schema) {
        super(i, schema);
    }

    public static String normalize(String string) {
        Identifier identifier = Identifier.create(string);
        if (identifier != null) {
            return identifier.toString();
        }
        return string;
    }

    @Override
    public Type<?> getChoiceType(DSL.TypeReference typeReference, String string) {
        return super.getChoiceType(typeReference, SchemaIdentifierNormalize.normalize(string));
    }
}

