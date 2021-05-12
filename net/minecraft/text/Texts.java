/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class Texts {
    public static final String DEFAULT_SEPARATOR = ", ";
    public static final Text GRAY_DEFAULT_SEPARATOR_TEXT = new LiteralText(", ").formatted(Formatting.GRAY);
    public static final Text DEFAULT_SEPARATOR_TEXT = new LiteralText(", ");

    public static MutableText setStyleIfAbsent(MutableText text, Style style) {
        if (style.isEmpty()) {
            return text;
        }
        Style style2 = text.getStyle();
        if (style2.isEmpty()) {
            return text.setStyle(style);
        }
        if (style2.equals(style)) {
            return text;
        }
        return text.setStyle(style2.withParent(style));
    }

    public static Optional<MutableText> parse(@Nullable ServerCommandSource source, Optional<Text> text, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        return text.isPresent() ? Optional.of(Texts.parse(source, text.get(), sender, depth)) : Optional.empty();
    }

    public static MutableText parse(@Nullable ServerCommandSource source, Text text, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (depth > 100) {
            return text.shallowCopy();
        }
        MutableText mutableText = text instanceof ParsableText ? ((ParsableText)((Object)text)).parse(source, sender, depth + 1) : text.copy();
        for (Text text2 : text.getSiblings()) {
            mutableText.append(Texts.parse(source, text2, sender, depth + 1));
        }
        return mutableText.fillStyle(Texts.parseStyle(source, text.getStyle(), sender, depth));
    }

    private static Style parseStyle(@Nullable ServerCommandSource source, Style style, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        Text text;
        HoverEvent hoverEvent = style.getHoverEvent();
        if (hoverEvent != null && (text = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT)) != null) {
            HoverEvent hoverEvent2 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Texts.parse(source, text, sender, depth + 1));
            return style.withHoverEvent(hoverEvent2);
        }
        return style;
    }

    public static Text toText(GameProfile profile) {
        if (profile.getName() != null) {
            return new LiteralText(profile.getName());
        }
        if (profile.getId() != null) {
            return new LiteralText(profile.getId().toString());
        }
        return new LiteralText("(unknown)");
    }

    public static Text joinOrdered(Collection<String> strings) {
        return Texts.joinOrdered(strings, string -> new LiteralText((String)string).formatted(Formatting.GREEN));
    }

    public static <T extends Comparable<T>> Text joinOrdered(Collection<T> elements, Function<T, Text> transformer) {
        if (elements.isEmpty()) {
            return LiteralText.EMPTY;
        }
        if (elements.size() == 1) {
            return transformer.apply((Comparable)elements.iterator().next());
        }
        ArrayList<T> list = Lists.newArrayList(elements);
        list.sort(Comparable::compareTo);
        return Texts.join(list, transformer);
    }

    public static <T> Text join(Collection<T> elements, Function<T, Text> transformer) {
        return Texts.join(elements, GRAY_DEFAULT_SEPARATOR_TEXT, transformer);
    }

    public static <T> MutableText join(Collection<T> elements, Optional<? extends Text> separator, Function<T, Text> transformer) {
        return Texts.join(elements, DataFixUtils.orElse(separator, GRAY_DEFAULT_SEPARATOR_TEXT), transformer);
    }

    public static <T> MutableText join(Collection<T> elements, Text separator, Function<T, Text> transformer) {
        if (elements.isEmpty()) {
            return new LiteralText("");
        }
        if (elements.size() == 1) {
            return transformer.apply(elements.iterator().next()).shallowCopy();
        }
        LiteralText mutableText = new LiteralText("");
        boolean bl = true;
        for (T object : elements) {
            if (!bl) {
                mutableText.append(separator);
            }
            mutableText.append(transformer.apply(object));
            bl = false;
        }
        return mutableText;
    }

    public static MutableText bracketed(Text text) {
        return new TranslatableText("chat.square_brackets", text);
    }

    public static Text toText(Message message) {
        if (message instanceof Text) {
            return (Text)message;
        }
        return new LiteralText(message.getString());
    }
}

