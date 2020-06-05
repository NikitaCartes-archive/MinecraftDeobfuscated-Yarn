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
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ItemShulkerBoxColorFix
extends DataFix {
    public static final String[] COLORED_SHULKER_BOX_IDS = new String[]{"minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box", "minecraft:silver_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"};

    public ItemShulkerBoxColorFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
        OpticFinder<?> opticFinder2 = type.findField("tag");
        OpticFinder<?> opticFinder3 = opticFinder2.type().findField("BlockEntityTag");
        return this.fixTypeEverywhereTyped("ItemShulkerBoxColorFix", type, typed -> {
            Typed typed2;
            Optional optional3;
            Optional optional2;
            Optional optional = typed.getOptional(opticFinder);
            if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:shulker_box") && (optional2 = typed.getOptionalTyped(opticFinder2)).isPresent() && (optional3 = (typed2 = optional2.get()).getOptionalTyped(opticFinder3)).isPresent()) {
                Typed<Dynamic<?>> typed3 = optional3.get();
                Dynamic<?> dynamic = typed3.get(DSL.remainderFinder());
                int i = dynamic.get("Color").asInt(0);
                dynamic.remove("Color");
                return typed.set(opticFinder2, typed2.set(opticFinder3, typed3.set(DSL.remainderFinder(), dynamic))).set(opticFinder, Pair.of(TypeReferences.ITEM_NAME.typeName(), COLORED_SHULKER_BOX_IDS[i % 16]));
            }
            return typed;
        });
    }
}

