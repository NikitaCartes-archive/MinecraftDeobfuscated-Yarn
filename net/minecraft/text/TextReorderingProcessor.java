/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;

public class TextReorderingProcessor {
    private final String string;
    private final List<Style> styles;
    private final Int2IntFunction reverser;

    private TextReorderingProcessor(String string, List<Style> styles, Int2IntFunction reverser) {
        this.string = string;
        this.styles = ImmutableList.copyOf(styles);
        this.reverser = reverser;
    }

    public String getString() {
        return this.string;
    }

    public List<OrderedText> process(int start, int length, boolean reverse) {
        if (length == 0) {
            return ImmutableList.of();
        }
        ArrayList<OrderedText> list = Lists.newArrayList();
        Style style = this.styles.get(start);
        int i = start;
        for (int j = 1; j < length; ++j) {
            int k = start + j;
            Style style2 = this.styles.get(k);
            if (style2.equals(style)) continue;
            String string = this.string.substring(i, k);
            list.add(reverse ? OrderedText.styledBackwardsVisitedString(string, style, this.reverser) : OrderedText.styledForwardsVisitedString(string, style));
            style = style2;
            i = k;
        }
        if (i < start + length) {
            String string2 = this.string.substring(i, start + length);
            list.add(reverse ? OrderedText.styledBackwardsVisitedString(string2, style, this.reverser) : OrderedText.styledForwardsVisitedString(string2, style));
        }
        return reverse ? Lists.reverse(list) : list;
    }

    public static TextReorderingProcessor create(StringVisitable visitable) {
        return TextReorderingProcessor.create(visitable, codePoint -> codePoint, string -> string);
    }

    public static TextReorderingProcessor create(StringVisitable visitable, Int2IntFunction reverser, UnaryOperator<String> shaper) {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Style> list = Lists.newArrayList();
        visitable.visit((style2, text) -> {
            TextVisitFactory.visitFormatted(text, style2, (charIndex, style, codePoint) -> {
                stringBuilder.appendCodePoint(codePoint);
                int i = Character.charCount(codePoint);
                for (int j = 0; j < i; ++j) {
                    list.add(style);
                }
                return true;
            });
            return Optional.empty();
        }, Style.EMPTY);
        return new TextReorderingProcessor((String)shaper.apply(stringBuilder.toString()), list, reverser);
    }
}

