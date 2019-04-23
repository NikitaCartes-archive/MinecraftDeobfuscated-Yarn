/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.BlockEntitySignTextStrictJsonFix;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix
extends DataFix {
    public ItemWrittenBookPagesStrictJsonFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public Dynamic<?> method_5085(Dynamic<?> dynamic) {
        return dynamic.update("pages", dynamic2 -> DataFixUtils.orElse(dynamic2.asStreamOpt().map(stream -> stream.map(dynamic -> {
            if (!dynamic.asString().isPresent()) {
                return dynamic;
            }
            String string = dynamic.asString("");
            Component component = null;
            if ("null".equals(string) || StringUtils.isEmpty(string)) {
                component = new TextComponent("");
            } else if (string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
                try {
                    component = JsonHelper.deserialize(BlockEntitySignTextStrictJsonFix.GSON, string, Component.class, true);
                    if (component == null) {
                        component = new TextComponent("");
                    }
                } catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
                if (component == null) {
                    try {
                        component = Component.Serializer.fromJsonString(string);
                    } catch (JsonParseException jsonParseException) {
                        // empty catch block
                    }
                }
                if (component == null) {
                    try {
                        component = Component.Serializer.fromLenientJsonString(string);
                    } catch (JsonParseException jsonParseException) {
                        // empty catch block
                    }
                }
                if (component == null) {
                    component = new TextComponent(string);
                }
            } else {
                component = new TextComponent(string);
            }
            return dynamic.createString(Component.Serializer.toJsonString(component));
        })).map(dynamic::createList), dynamic.emptyList()));
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<?> opticFinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), this::method_5085)));
    }
}

