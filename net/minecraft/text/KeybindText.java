/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class KeybindText
extends BaseText {
    private static Function<String, Supplier<Text>> translator = key -> () -> new LiteralText((String)key);
    private final String key;
    private Supplier<Text> translated;

    public KeybindText(String key) {
        this.key = key;
    }

    @Environment(value=EnvType.CLIENT)
    public static void setTranslator(Function<String, Supplier<Text>> translator) {
        KeybindText.translator = translator;
    }

    private Text getTranslated() {
        if (this.translated == null) {
            this.translated = translator.apply(this.key);
        }
        return this.translated.get();
    }

    @Override
    public <T> Optional<T> visitSelf(Text.Visitor<T> visitor) {
        return this.getTranslated().visit(visitor);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public <T> Optional<T> visitSelf(Text.StyledVisitor<T> visitor, Style style) {
        return this.getTranslated().visit(visitor, style);
    }

    @Override
    public KeybindText copy() {
        return new KeybindText(this.key);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof KeybindText) {
            KeybindText keybindText = (KeybindText)object;
            return this.key.equals(keybindText.key) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "KeybindComponent{keybind='" + this.key + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public /* synthetic */ BaseText copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ MutableText copy() {
        return this.copy();
    }
}

