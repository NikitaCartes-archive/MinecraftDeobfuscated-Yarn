/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.lang.reflect.Type;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.ChoiceFix;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix
extends ChoiceFix {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)Text.class), new JsonDeserializer<Text>(){

        public Text method_15583(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                return new LiteralText(jsonElement.getAsString());
            }
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                Text text = null;
                for (JsonElement jsonElement2 : jsonArray) {
                    Text text2 = this.method_15583(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                    if (text == null) {
                        text = text2;
                        continue;
                    }
                    text.append(text2);
                }
                return text;
            }
            throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.method_15583(jsonElement, type, jsonDeserializationContext);
        }
    }).create();

    public BlockEntitySignTextStrictJsonFix(Schema schema, boolean bl) {
        super(schema, bl, "BlockEntitySignTextStrictJsonFix", TypeReferences.BLOCK_ENTITY, "Sign");
    }

    private Dynamic<?> fix(Dynamic<?> dynamic, String string) {
        String string2 = dynamic.get(string).asString("");
        Text text = null;
        if ("null".equals(string2) || StringUtils.isEmpty(string2)) {
            text = new LiteralText("");
        } else if (string2.charAt(0) == '\"' && string2.charAt(string2.length() - 1) == '\"' || string2.charAt(0) == '{' && string2.charAt(string2.length() - 1) == '}') {
            try {
                text = JsonHelper.deserialize(GSON, string2, Text.class, true);
                if (text == null) {
                    text = new LiteralText("");
                }
            } catch (JsonParseException jsonParseException) {
                // empty catch block
            }
            if (text == null) {
                try {
                    text = Text.Serializer.fromJson(string2);
                } catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
            }
            if (text == null) {
                try {
                    text = Text.Serializer.fromLenientJson(string2);
                } catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
            }
            if (text == null) {
                text = new LiteralText(string2);
            }
        } else {
            text = new LiteralText(string2);
        }
        return dynamic.set(string, dynamic.createString(Text.Serializer.toJson(text)));
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> {
            dynamic = this.fix((Dynamic<?>)dynamic, "Text1");
            dynamic = this.fix((Dynamic<?>)dynamic, "Text2");
            dynamic = this.fix((Dynamic<?>)dynamic, "Text3");
            dynamic = this.fix((Dynamic<?>)dynamic, "Text4");
            return dynamic;
        });
    }
}

