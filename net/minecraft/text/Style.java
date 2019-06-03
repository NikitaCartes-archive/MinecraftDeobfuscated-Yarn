/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class Style {
    private Style parent;
    private Formatting color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private String insertion;
    private static final Style ROOT = new Style(){

        @Override
        @Nullable
        public Formatting getColor() {
            return null;
        }

        @Override
        public boolean isBold() {
            return false;
        }

        @Override
        public boolean isItalic() {
            return false;
        }

        @Override
        public boolean isStrikethrough() {
            return false;
        }

        @Override
        public boolean isUnderlined() {
            return false;
        }

        @Override
        public boolean isObfuscated() {
            return false;
        }

        @Override
        @Nullable
        public ClickEvent getClickEvent() {
            return null;
        }

        @Override
        @Nullable
        public HoverEvent getHoverEvent() {
            return null;
        }

        @Override
        @Nullable
        public String getInsertion() {
            return null;
        }

        @Override
        public Style setColor(Formatting formatting) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Style setBold(Boolean boolean_) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Style setItalic(Boolean boolean_) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Style setStrikethrough(Boolean boolean_) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Style setUnderline(Boolean boolean_) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Style setObfuscated(Boolean boolean_) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Style setClickEvent(ClickEvent clickEvent) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Style setHoverEvent(HoverEvent hoverEvent) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Style setParent(Style style) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "Style.ROOT";
        }

        @Override
        public Style deepCopy() {
            return this;
        }

        @Override
        public Style copy() {
            return this;
        }

        @Override
        public String asString() {
            return "";
        }
    };

    @Nullable
    public Formatting getColor() {
        return this.color == null ? this.getParent().getColor() : this.color;
    }

    public boolean isBold() {
        return this.bold == null ? this.getParent().isBold() : this.bold.booleanValue();
    }

    public boolean isItalic() {
        return this.italic == null ? this.getParent().isItalic() : this.italic.booleanValue();
    }

    public boolean isStrikethrough() {
        return this.strikethrough == null ? this.getParent().isStrikethrough() : this.strikethrough.booleanValue();
    }

    public boolean isUnderlined() {
        return this.underline == null ? this.getParent().isUnderlined() : this.underline.booleanValue();
    }

    public boolean isObfuscated() {
        return this.obfuscated == null ? this.getParent().isObfuscated() : this.obfuscated.booleanValue();
    }

    public boolean isEmpty() {
        return this.bold == null && this.italic == null && this.strikethrough == null && this.underline == null && this.obfuscated == null && this.color == null && this.clickEvent == null && this.hoverEvent == null && this.insertion == null;
    }

    @Nullable
    public ClickEvent getClickEvent() {
        return this.clickEvent == null ? this.getParent().getClickEvent() : this.clickEvent;
    }

    @Nullable
    public HoverEvent getHoverEvent() {
        return this.hoverEvent == null ? this.getParent().getHoverEvent() : this.hoverEvent;
    }

    @Nullable
    public String getInsertion() {
        return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
    }

    public Style setColor(Formatting formatting) {
        this.color = formatting;
        return this;
    }

    public Style setBold(Boolean boolean_) {
        this.bold = boolean_;
        return this;
    }

    public Style setItalic(Boolean boolean_) {
        this.italic = boolean_;
        return this;
    }

    public Style setStrikethrough(Boolean boolean_) {
        this.strikethrough = boolean_;
        return this;
    }

    public Style setUnderline(Boolean boolean_) {
        this.underline = boolean_;
        return this;
    }

    public Style setObfuscated(Boolean boolean_) {
        this.obfuscated = boolean_;
        return this;
    }

    public Style setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    public Style setHoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }

    public Style setInsertion(String string) {
        this.insertion = string;
        return this;
    }

    public Style setParent(Style style) {
        this.parent = style;
        return this;
    }

    public String asString() {
        if (this.isEmpty()) {
            if (this.parent != null) {
                return this.parent.asString();
            }
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (this.getColor() != null) {
            stringBuilder.append((Object)this.getColor());
        }
        if (this.isBold()) {
            stringBuilder.append((Object)Formatting.BOLD);
        }
        if (this.isItalic()) {
            stringBuilder.append((Object)Formatting.ITALIC);
        }
        if (this.isUnderlined()) {
            stringBuilder.append((Object)Formatting.UNDERLINE);
        }
        if (this.isObfuscated()) {
            stringBuilder.append((Object)Formatting.OBFUSCATED);
        }
        if (this.isStrikethrough()) {
            stringBuilder.append((Object)Formatting.STRIKETHROUGH);
        }
        return stringBuilder.toString();
    }

    private Style getParent() {
        return this.parent == null ? ROOT : this.parent;
    }

    public String toString() {
        return "Style{hasParent=" + (this.parent != null) + ", color=" + (Object)((Object)this.color) + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underline + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + '}';
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Style) {
            Style style = (Style)object;
            return this.isBold() == style.isBold() && this.getColor() == style.getColor() && this.isItalic() == style.isItalic() && this.isObfuscated() == style.isObfuscated() && this.isStrikethrough() == style.isStrikethrough() && this.isUnderlined() == style.isUnderlined() && (this.getClickEvent() != null ? this.getClickEvent().equals(style.getClickEvent()) : style.getClickEvent() == null) && (this.getHoverEvent() != null ? this.getHoverEvent().equals(style.getHoverEvent()) : style.getHoverEvent() == null) && (this.getInsertion() != null ? this.getInsertion().equals(style.getInsertion()) : style.getInsertion() == null);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.color, this.bold, this.italic, this.underline, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion});
    }

    public Style deepCopy() {
        Style style = new Style();
        style.bold = this.bold;
        style.italic = this.italic;
        style.strikethrough = this.strikethrough;
        style.underline = this.underline;
        style.obfuscated = this.obfuscated;
        style.color = this.color;
        style.clickEvent = this.clickEvent;
        style.hoverEvent = this.hoverEvent;
        style.parent = this.parent;
        style.insertion = this.insertion;
        return style;
    }

    public Style copy() {
        Style style = new Style();
        style.setBold(this.isBold());
        style.setItalic(this.isItalic());
        style.setStrikethrough(this.isStrikethrough());
        style.setUnderline(this.isUnderlined());
        style.setObfuscated(this.isObfuscated());
        style.setColor(this.getColor());
        style.setClickEvent(this.getClickEvent());
        style.setHoverEvent(this.getHoverEvent());
        style.setInsertion(this.getInsertion());
        return style;
    }

    public static class Serializer
    implements JsonDeserializer<Style>,
    JsonSerializer<Style> {
        @Nullable
        public Style method_10991(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                JsonPrimitive jsonPrimitive;
                JsonObject jsonObject2;
                Style style = new Style();
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject == null) {
                    return null;
                }
                if (jsonObject.has("bold")) {
                    style.bold = jsonObject.get("bold").getAsBoolean();
                }
                if (jsonObject.has("italic")) {
                    style.italic = jsonObject.get("italic").getAsBoolean();
                }
                if (jsonObject.has("underlined")) {
                    style.underline = jsonObject.get("underlined").getAsBoolean();
                }
                if (jsonObject.has("strikethrough")) {
                    style.strikethrough = jsonObject.get("strikethrough").getAsBoolean();
                }
                if (jsonObject.has("obfuscated")) {
                    style.obfuscated = jsonObject.get("obfuscated").getAsBoolean();
                }
                if (jsonObject.has("color")) {
                    style.color = (Formatting)((Object)jsonDeserializationContext.deserialize(jsonObject.get("color"), (Type)((Object)Formatting.class)));
                }
                if (jsonObject.has("insertion")) {
                    style.insertion = jsonObject.get("insertion").getAsString();
                }
                if (jsonObject.has("clickEvent") && (jsonObject2 = jsonObject.getAsJsonObject("clickEvent")) != null) {
                    String string;
                    jsonPrimitive = jsonObject2.getAsJsonPrimitive("action");
                    ClickEvent.Action action = jsonPrimitive == null ? null : ClickEvent.Action.byName(jsonPrimitive.getAsString());
                    JsonPrimitive jsonPrimitive2 = jsonObject2.getAsJsonPrimitive("value");
                    String string2 = string = jsonPrimitive2 == null ? null : jsonPrimitive2.getAsString();
                    if (action != null && string != null && action.isUserDefinable()) {
                        style.clickEvent = new ClickEvent(action, string);
                    }
                }
                if (jsonObject.has("hoverEvent") && (jsonObject2 = jsonObject.getAsJsonObject("hoverEvent")) != null) {
                    jsonPrimitive = jsonObject2.getAsJsonPrimitive("action");
                    HoverEvent.Action action2 = jsonPrimitive == null ? null : HoverEvent.Action.byName(jsonPrimitive.getAsString());
                    Text text = (Text)jsonDeserializationContext.deserialize(jsonObject2.get("value"), (Type)((Object)Text.class));
                    if (action2 != null && text != null && action2.isUserDefinable()) {
                        style.hoverEvent = new HoverEvent(action2, text);
                    }
                }
                return style;
            }
            return null;
        }

        @Nullable
        public JsonElement method_10990(Style style, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject2;
            if (style.isEmpty()) {
                return null;
            }
            JsonObject jsonObject = new JsonObject();
            if (style.bold != null) {
                jsonObject.addProperty("bold", style.bold);
            }
            if (style.italic != null) {
                jsonObject.addProperty("italic", style.italic);
            }
            if (style.underline != null) {
                jsonObject.addProperty("underlined", style.underline);
            }
            if (style.strikethrough != null) {
                jsonObject.addProperty("strikethrough", style.strikethrough);
            }
            if (style.obfuscated != null) {
                jsonObject.addProperty("obfuscated", style.obfuscated);
            }
            if (style.color != null) {
                jsonObject.add("color", jsonSerializationContext.serialize((Object)style.color));
            }
            if (style.insertion != null) {
                jsonObject.add("insertion", jsonSerializationContext.serialize(style.insertion));
            }
            if (style.clickEvent != null) {
                jsonObject2 = new JsonObject();
                jsonObject2.addProperty("action", style.clickEvent.getAction().getName());
                jsonObject2.addProperty("value", style.clickEvent.getValue());
                jsonObject.add("clickEvent", jsonObject2);
            }
            if (style.hoverEvent != null) {
                jsonObject2 = new JsonObject();
                jsonObject2.addProperty("action", style.hoverEvent.getAction().getName());
                jsonObject2.add("value", jsonSerializationContext.serialize(style.hoverEvent.getValue()));
                jsonObject.add("hoverEvent", jsonObject2);
            }
            return jsonObject;
        }

        @Override
        @Nullable
        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.method_10990((Style)object, type, jsonSerializationContext);
        }

        @Override
        @Nullable
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.method_10991(jsonElement, type, jsonDeserializationContext);
        }
    }
}

