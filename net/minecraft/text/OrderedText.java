/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;

/**
 * An object that can supply character code points
 * to a visitor, with a style context.
 */
@FunctionalInterface
public interface OrderedText {
    /**
     * An empty text that does not call the visitors.
     */
    public static final OrderedText EMPTY = characterVisitor -> true;

    @Environment(value=EnvType.CLIENT)
    public boolean accept(CharacterVisitor var1);

    @Environment(value=EnvType.CLIENT)
    public static OrderedText styled(int codePoint, Style style) {
        return visitor -> visitor.accept(0, style, codePoint);
    }

    @Environment(value=EnvType.CLIENT)
    public static OrderedText styledString(String string, Style style) {
        if (string.isEmpty()) {
            return EMPTY;
        }
        return visitor -> TextVisitFactory.visitForwards(string, style, visitor);
    }

    @Environment(value=EnvType.CLIENT)
    public static OrderedText styledStringMapped(String string, Style style, Int2IntFunction codePointMapper) {
        if (string.isEmpty()) {
            return EMPTY;
        }
        return visitor -> TextVisitFactory.visitBackwards(string, style, OrderedText.map(visitor, codePointMapper));
    }

    @Environment(value=EnvType.CLIENT)
    public static CharacterVisitor map(CharacterVisitor visitor, Int2IntFunction codePointMapper) {
        return (charIndex, style, charPoint) -> visitor.accept(charIndex, style, (Integer)codePointMapper.apply(charPoint));
    }

    @Environment(value=EnvType.CLIENT)
    public static OrderedText concat(OrderedText first, OrderedText second) {
        return OrderedText.innerConcat(first, second);
    }

    @Environment(value=EnvType.CLIENT)
    public static OrderedText concat(List<OrderedText> texts) {
        int i = texts.size();
        switch (i) {
            case 0: {
                return EMPTY;
            }
            case 1: {
                return texts.get(0);
            }
            case 2: {
                return OrderedText.innerConcat(texts.get(0), texts.get(1));
            }
        }
        return OrderedText.innerConcat(ImmutableList.copyOf(texts));
    }

    @Environment(value=EnvType.CLIENT)
    public static OrderedText innerConcat(OrderedText text1, OrderedText text2) {
        return visitor -> text1.accept(visitor) && text2.accept(visitor);
    }

    @Environment(value=EnvType.CLIENT)
    public static OrderedText innerConcat(List<OrderedText> texts) {
        return visitor -> {
            for (OrderedText orderedText : texts) {
                if (orderedText.accept(visitor)) continue;
                return false;
            }
            return true;
        };
    }
}

