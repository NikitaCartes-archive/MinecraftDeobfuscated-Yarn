/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.stream.Stream;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ItemLoreToComponentFix
extends DataFix {
    public ItemLoreToComponentFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<?> opticFinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("Item Lore componentize", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("display", dynamic2 -> dynamic2.update("Lore", dynamic -> DataFixUtils.orElse(dynamic.asStreamOpt().map(ItemLoreToComponentFix::fixLoreTags).map(dynamic::createList), dynamic))))));
    }

    private static <T> Stream<Dynamic<T>> fixLoreTags(Stream<Dynamic<T>> stream) {
        return stream.map(dynamic -> DataFixUtils.orElse(dynamic.asString().map(ItemLoreToComponentFix::componentize).map(dynamic::createString), dynamic));
    }

    private static String componentize(String string) {
        return Text.Serializer.toJson(new LiteralText(string));
    }
}

