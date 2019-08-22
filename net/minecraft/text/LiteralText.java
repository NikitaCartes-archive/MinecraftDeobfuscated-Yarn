/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import net.minecraft.text.BaseText;
import net.minecraft.text.Text;

public class LiteralText
extends BaseText {
    private final String string;

    public LiteralText(String string) {
        this.string = string;
    }

    public String getRawString() {
        return this.string;
    }

    @Override
    public String asString() {
        return this.string;
    }

    public LiteralText method_10992() {
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
    public /* synthetic */ Text copy() {
        return this.method_10992();
    }
}

