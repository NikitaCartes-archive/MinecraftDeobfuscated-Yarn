/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.AbstractUuidFix;

public class ItemStackUuidFix
extends AbstractUuidFix {
    public ItemStackUuidFix(Schema outputSchema) {
        super(outputSchema, TypeReferences.ITEM_STACK);
    }

    @Override
    public TypeRewriteRule makeRule() {
        OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        return this.fixTypeEverywhereTyped("ItemStackUUIDFix", this.getInputSchema().getType(this.typeReference), typed2 -> {
            if (typed2.getOptional(opticFinder).map(pair -> "minecraft:player_head".equals(pair.getSecond())).orElse(false).booleanValue()) {
                OpticFinder<?> opticFinder2 = typed2.getType().findField("tag");
                return typed2.updateTyped(opticFinder2, typed -> typed.update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("SkullOwner", dynamic -> ItemStackUuidFix.updateStringUuid(dynamic, "Id", "Id").orElse((Dynamic<?>)dynamic))));
            }
            return typed2;
        });
    }
}

