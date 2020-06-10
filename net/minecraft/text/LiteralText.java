/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import net.minecraft.text.BaseText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

public class LiteralText
extends BaseText {
    public static final Text EMPTY = new LiteralText("");
    private final String string;
    @Nullable
    private Language field_25315;
    private String field_25316;

    public LiteralText(String string) {
        this.string = string;
        this.field_25316 = string;
    }

    public String getRawString() {
        return this.string;
    }

    @Override
    public String asString() {
        if (this.string.isEmpty()) {
            return this.string;
        }
        Language language = Language.getInstance();
        if (this.field_25315 != language) {
            this.field_25316 = language.reorder(this.string, false);
            this.field_25315 = language;
        }
        return this.field_25316;
    }

    @Override
    public LiteralText copy() {
        return new LiteralText(this.string);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof LiteralText) {
            LiteralText literalText = (LiteralText)object;
            return this.string.equals(literalText.getRawString()) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "TextComponent{text='" + this.string + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
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

