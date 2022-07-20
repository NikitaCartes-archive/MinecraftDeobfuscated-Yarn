/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class class_7641
extends DataFix {
    private final Set<String> field_39900;

    public class_7641(Schema schema, boolean bl, Set<String> set) {
        super(schema, bl);
        this.field_39900 = set;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
        OpticFinder<?> opticFinder2 = type.findField("tag");
        OpticFinder<?> opticFinder3 = opticFinder2.type().findField("BlockEntityTag");
        return this.fixTypeEverywhereTyped("ItemRemoveBlockEntityTagFix", type, typed -> {
            Typed<Dynamic<?>> typed2;
            Optional optional3;
            Optional optional2;
            Optional optional = typed.getOptional(opticFinder);
            if (optional.isPresent() && this.field_39900.contains(((Pair)optional.get()).getSecond()) && (optional2 = typed.getOptionalTyped(opticFinder2)).isPresent() && (optional3 = (typed2 = optional2.get()).getOptionalTyped(opticFinder3)).isPresent()) {
                Optional<Dynamic<?>> optional4 = typed2.write().result();
                Dynamic<?> dynamic = optional4.isPresent() ? optional4.get() : typed2.get(DSL.remainderFinder());
                Dynamic<?> dynamic2 = dynamic.remove("BlockEntityTag");
                Optional optional5 = opticFinder2.type().readTyped(dynamic2).result();
                if (optional5.isEmpty()) {
                    return typed;
                }
                return typed.set(opticFinder2, optional5.get().getFirst());
            }
            return typed;
        });
    }
}

