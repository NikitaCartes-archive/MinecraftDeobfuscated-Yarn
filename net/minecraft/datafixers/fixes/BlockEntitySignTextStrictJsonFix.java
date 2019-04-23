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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix
extends ChoiceFix {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)Component.class), new JsonDeserializer<Component>(){

        public Component method_15583(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                return new TextComponent(jsonElement.getAsString());
            }
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                Component component = null;
                for (JsonElement jsonElement2 : jsonArray) {
                    Component component2 = this.method_15583(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                    if (component == null) {
                        component = component2;
                        continue;
                    }
                    component.append(component2);
                }
                return component;
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

    private Dynamic<?> method_15582(Dynamic<?> dynamic, String string) {
        String string2 = dynamic.get(string).asString("");
        Component component = null;
        if ("null".equals(string2) || StringUtils.isEmpty(string2)) {
            component = new TextComponent("");
        } else if (string2.charAt(0) == '\"' && string2.charAt(string2.length() - 1) == '\"' || string2.charAt(0) == '{' && string2.charAt(string2.length() - 1) == '}') {
            try {
                component = JsonHelper.deserialize(GSON, string2, Component.class, true);
                if (component == null) {
                    component = new TextComponent("");
                }
            } catch (JsonParseException jsonParseException) {
                // empty catch block
            }
            if (component == null) {
                try {
                    component = Component.Serializer.fromJsonString(string2);
                } catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
            }
            if (component == null) {
                try {
                    component = Component.Serializer.fromLenientJsonString(string2);
                } catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
            }
            if (component == null) {
                component = new TextComponent(string2);
            }
        } else {
            component = new TextComponent(string2);
        }
        return dynamic.set(string, dynamic.createString(Component.Serializer.toJsonString(component)));
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> {
            dynamic = this.method_15582((Dynamic<?>)dynamic, "Text1");
            dynamic = this.method_15582((Dynamic<?>)dynamic, "Text2");
            dynamic = this.method_15582((Dynamic<?>)dynamic, "Text3");
            dynamic = this.method_15582((Dynamic<?>)dynamic, "Text4");
            return dynamic;
        });
    }
}

