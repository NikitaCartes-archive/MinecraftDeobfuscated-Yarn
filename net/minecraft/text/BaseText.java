/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5481;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

public abstract class BaseText
implements MutableText {
    protected final List<Text> siblings = Lists.newArrayList();
    private class_5481 field_26623 = class_5481.field_26385;
    @Nullable
    @Environment(value=EnvType.CLIENT)
    private Language field_26624;
    private Style style = Style.EMPTY;

    @Override
    public MutableText append(Text text) {
        this.siblings.add(text);
        return this;
    }

    @Override
    public String asString() {
        return "";
    }

    @Override
    public List<Text> getSiblings() {
        return this.siblings;
    }

    @Override
    public MutableText setStyle(Style style) {
        this.style = style;
        return this;
    }

    @Override
    public Style getStyle() {
        return this.style;
    }

    @Override
    public abstract BaseText copy();

    @Override
    public final MutableText shallowCopy() {
        BaseText baseText = this.copy();
        baseText.siblings.addAll(this.siblings);
        baseText.setStyle(this.style);
        return baseText;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public class_5481 method_30937() {
        Language language = Language.getInstance();
        if (this.field_26624 != language) {
            this.field_26623 = language.method_30934(this);
            this.field_26624 = language;
        }
        return this.field_26623;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BaseText) {
            BaseText baseText = (BaseText)obj;
            return this.siblings.equals(baseText.siblings) && Objects.equals(this.getStyle(), baseText.getStyle());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.getStyle(), this.siblings);
    }

    public String toString() {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }

    @Override
    public /* synthetic */ MutableText copy() {
        return this.copy();
    }
}

