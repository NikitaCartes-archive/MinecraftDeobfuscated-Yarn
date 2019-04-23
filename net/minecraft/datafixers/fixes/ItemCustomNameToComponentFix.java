/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ItemCustomNameToComponentFix
extends DataFix {
    public ItemCustomNameToComponentFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    private Dynamic<?> method_5001(Dynamic<?> dynamic) {
        Optional<Dynamic<?>> optional = dynamic.get("display").get();
        if (optional.isPresent()) {
            Dynamic dynamic2 = optional.get();
            Optional<String> optional2 = dynamic2.get("Name").asString();
            if (optional2.isPresent()) {
                dynamic2 = dynamic2.set("Name", dynamic2.createString(Component.Serializer.toJsonString(new TextComponent(optional2.get()))));
            } else {
                Optional<String> optional3 = dynamic2.get("LocName").asString();
                if (optional3.isPresent()) {
                    dynamic2 = dynamic2.set("Name", dynamic2.createString(Component.Serializer.toJsonString(new TranslatableComponent(optional3.get(), new Object[0]))));
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
        return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), this::method_5001)));
    }
}

