/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class Texts {
    public static Text setStyleIfAbsent(Text text, Style style) {
        if (style.isEmpty()) {
            return text;
        }
        if (text.getStyle().isEmpty()) {
            return text.setStyle(style.deepCopy());
        }
        return new LiteralText("").append(text).setStyle(style.deepCopy());
    }

    public static Text parse(@Nullable ServerCommandSource serverCommandSource, Text text, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (i > 100) {
            return text;
        }
        Text text2 = text instanceof ParsableText ? ((ParsableText)((Object)text)).parse(serverCommandSource, entity, ++i) : text.copy();
        for (Text text3 : text.getSiblings()) {
            text2.append(Texts.parse(serverCommandSource, text3, entity, i));
        }
        return Texts.setStyleIfAbsent(text2, text.getStyle());
    }

    public static Text toText(GameProfile gameProfile) {
        if (gameProfile.getName() != null) {
            return new LiteralText(gameProfile.getName());
        }
        if (gameProfile.getId() != null) {
            return new LiteralText(gameProfile.getId().toString());
        }
        return new LiteralText("(unknown)");
    }

    public static Text joinOrdered(Collection<String> collection) {
        return Texts.joinOrdered(collection, string -> new LiteralText((String)string).formatted(Formatting.GREEN));
    }

    public static <T extends Comparable<T>> Text joinOrdered(Collection<T> collection, Function<T, Text> function) {
        if (collection.isEmpty()) {
            return new LiteralText("");
        }
        if (collection.size() == 1) {
            return function.apply(collection.iterator().next());
        }
        ArrayList<T> list = Lists.newArrayList(collection);
        list.sort(Comparable::compareTo);
        return Texts.join(collection, function);
    }

    public static <T> Text join(Collection<T> collection, Function<T, Text> function) {
        if (collection.isEmpty()) {
            return new LiteralText("");
        }
        if (collection.size() == 1) {
            return function.apply(collection.iterator().next());
        }
        LiteralText text = new LiteralText("");
        boolean bl = true;
        for (T object : collection) {
            if (!bl) {
                text.append(new LiteralText(", ").formatted(Formatting.GRAY));
            }
            text.append(function.apply(object));
            bl = false;
        }
        return text;
    }

    public static Text bracketed(Text text) {
        return new LiteralText("[").append(text).append("]");
    }

    public static Text toText(Message message) {
        if (message instanceof Text) {
            return (Text)message;
        }
        return new LiteralText(message.getString());
    }
}

