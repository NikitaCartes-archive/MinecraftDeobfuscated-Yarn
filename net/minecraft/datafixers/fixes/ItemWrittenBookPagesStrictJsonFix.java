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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix
extends DataFix {
    public ItemWrittenBookPagesStrictJsonFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public Dynamic<?> fixBookPages(Dynamic<?> tag) {
        return tag.update("pages", dynamic2 -> DataFixUtils.orElse(dynamic2.asStreamOpt().map(stream -> stream.map(dynamic -> {
            if (!dynamic.asString().isPresent()) {
                return dynamic;
            }
            String string = dynamic.asString("");
            Text text = null;
            if ("null".equals(string) || StringUtils.isEmpty(string)) {
                text = new LiteralText("");
            } else if (string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
                try {
                    text = JsonHelper.deserialize(BlockEntitySignTextStrictJsonFix.GSON, string, Text.class, true);
                    if (text == null) {
                        text = new LiteralText("");
                    }
                } catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
                if (text == null) {
                    try {
                        text = Text.Serializer.fromJson(string);
                    } catch (JsonParseException jsonParseException) {
                        // empty catch block
                    }
                }
                if (text == null) {
                    try {
                        text = Text.Serializer.fromLenientJson(string);
                    } catch (JsonParseException jsonParseException) {
                        // empty catch block
                    }
                }
                if (text == null) {
                    text = new LiteralText(string);
                }
            } else {
                text = new LiteralText(string);
            }
            return dynamic.createString(Text.Serializer.toJson(text));
        })).map(tag::createList), tag.emptyList()));
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<?> opticFinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), this::fixBookPages)));
    }
}

