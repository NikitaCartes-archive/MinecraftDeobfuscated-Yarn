/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.class_7417;
import net.minecraft.class_7420;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class KeybindText
implements class_7417 {
    private final String key;
    @Nullable
    private Supplier<Text> translated;

    public KeybindText(String key) {
        this.key = key;
    }

    private Text getTranslated() {
        if (this.translated == null) {
            this.translated = class_7420.field_39013.apply(this.key);
        }
        return this.translated.get();
    }

    @Override
    public <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
        return this.getTranslated().visit(visitor);
    }

    @Override
    public <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        return this.getTranslated().visit(styledVisitor, style);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof KeybindText)) return false;
        KeybindText keybindText = (KeybindText)object;
        if (!this.key.equals(keybindText.key)) return false;
        return true;
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public String toString() {
        return "keybind{" + this.key + "}";
    }

    public String getKey() {
        return this.key;
    }
}

