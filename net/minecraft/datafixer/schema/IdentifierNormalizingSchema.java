/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Const;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.util.Identifier;

public class IdentifierNormalizingSchema
extends Schema {
    public static final PrimitiveCodec<String> field_24652 = new PrimitiveCodec<String>(){

        @Override
        public <T> DataResult<String> read(DynamicOps<T> dynamicOps, T object) {
            return dynamicOps.getStringValue(object).map(IdentifierNormalizingSchema::normalize);
        }

        @Override
        public <T> T write(DynamicOps<T> dynamicOps, String string) {
            return dynamicOps.createString(string);
        }

        public String toString() {
            return "NamespacedString";
        }

        @Override
        public /* synthetic */ Object write(DynamicOps dynamicOps, Object object) {
            return this.write(dynamicOps, (String)object);
        }
    };
    private static final Type<String> field_24653 = new Const.PrimitiveType<String>(field_24652);

    public IdentifierNormalizingSchema(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    public static String normalize(String id) {
        Identifier identifier = Identifier.tryParse(id);
        if (identifier != null) {
            return identifier.toString();
        }
        return id;
    }

    public static Type<String> method_28295() {
        return field_24653;
    }

    @Override
    public Type<?> getChoiceType(DSL.TypeReference typeReference, String string) {
        return super.getChoiceType(typeReference, IdentifierNormalizingSchema.normalize(string));
    }
}

