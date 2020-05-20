/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.AbstractUuidFix;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ItemStackUuidFix
extends AbstractUuidFix {
    public ItemStackUuidFix(Schema outputSchema) {
        super(outputSchema, TypeReferences.ITEM_STACK);
    }

    @Override
    public TypeRewriteRule makeRule() {
        OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.method_28295()));
        return this.fixTypeEverywhereTyped("ItemStackUUIDFix", this.getInputSchema().getType(this.typeReference), typed -> {
            OpticFinder<?> opticFinder2 = typed.getType().findField("tag");
            return typed.updateTyped(opticFinder2, typed2 -> typed2.update(DSL.remainderFinder(), dynamic -> {
                dynamic = this.method_26297((Dynamic<?>)dynamic);
                if (typed.getOptional(opticFinder).map(pair -> "minecraft:player_head".equals(pair.getSecond())).orElse(false).booleanValue()) {
                    dynamic = this.method_26298((Dynamic<?>)dynamic);
                }
                return dynamic;
            }));
        });
    }

    private Dynamic<?> method_26297(Dynamic<?> dynamic) {
        return dynamic.update("AttributeModifiers", dynamic22 -> dynamic.createList(dynamic22.asStream().map(dynamic -> ItemStackUuidFix.updateRegularMostLeast(dynamic, "UUID", "UUID").orElse((Dynamic<?>)dynamic))));
    }

    private Dynamic<?> method_26298(Dynamic<?> dynamic2) {
        return dynamic2.update("SkullOwner", dynamic -> ItemStackUuidFix.updateStringUuid(dynamic, "Id", "Id").orElse((Dynamic<?>)dynamic));
    }
}

