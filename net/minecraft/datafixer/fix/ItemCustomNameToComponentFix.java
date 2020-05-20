/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ItemCustomNameToComponentFix
extends DataFix {
    public ItemCustomNameToComponentFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    private Dynamic<?> fixCustomName(Dynamic<?> dynamic) {
        Optional<Dynamic<?>> optional = dynamic.get("display").result();
        if (optional.isPresent()) {
            Dynamic dynamic2 = optional.get();
            Optional<String> optional2 = dynamic2.get("Name").asString().result();
            if (optional2.isPresent()) {
                dynamic2 = dynamic2.set("Name", dynamic2.createString(Text.Serializer.toJson(new LiteralText(optional2.get()))));
            } else {
                Optional<String> optional3 = dynamic2.get("LocName").asString().result();
                if (optional3.isPresent()) {
                    dynamic2 = dynamic2.set("Name", dynamic2.createString(Text.Serializer.toJson(new TranslatableText(optional3.get()))));
                    dynamic2 = dynamic2.remove("LocName");
                }
            }
            return dynamic.set("display", dynamic2);
        }
        return dynamic;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<?> opticFinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), this::fixCustomName)));
    }
}

