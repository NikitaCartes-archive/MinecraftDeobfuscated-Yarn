package net.minecraft.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

public class Texts {
	public static Text setStyleIfAbsent(Text text, Style style) {
		if (style.isEmpty()) {
			return text;
		} else {
			return text.getStyle().isEmpty() ? text.setStyle(style.deepCopy()) : new LiteralText("").append(text).setStyle(style.deepCopy());
		}
	}

	public static Text parse(@Nullable ServerCommandSource source, Text text, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		if (depth > 100) {
			return text;
		} else {
			depth++;
			Text text2 = text instanceof ParsableText ? ((ParsableText)text).parse(source, sender, depth) : text.copy();

			for (Text text3 : text.getSiblings()) {
				text2.append(parse(source, text3, sender, depth));
			}

			return setStyleIfAbsent(text2, text.getStyle());
		}
	}

	public static Text toText(GameProfile profile) {
		if (profile.getName() != null) {
			return new LiteralText(profile.getName());
		} else {
			return profile.getId() != null ? new LiteralText(profile.getId().toString()) : new LiteralText("(unknown)");
		}
	}

	public static Text joinOrdered(Collection<String> strings) {
		return joinOrdered(strings, string -> new LiteralText(string).formatted(Formatting.GREEN));
	}

	public static <T extends Comparable<T>> Text joinOrdered(Collection<T> elements, Function<T, Text> transformer) {
		if (elements.isEmpty()) {
			return new LiteralText("");
		} else if (elements.size() == 1) {
			return (Text)transformer.apply(elements.iterator().next());
		} else {
			List<T> list = Lists.<T>newArrayList(elements);
			list.sort(Comparable::compareTo);
			return join(list, transformer);
		}
	}

	public static <T> Text join(Collection<T> elements, Function<T, Text> transformer) {
		if (elements.isEmpty()) {
			return new LiteralText("");
		} else if (elements.size() == 1) {
			return (Text)transformer.apply(elements.iterator().next());
		} else {
			Text text = new LiteralText("");
			boolean bl = true;

			for (T object : elements) {
				if (!bl) {
					text.append(new LiteralText(", ").formatted(Formatting.GRAY));
				}

				text.append((Text)transformer.apply(object));
				bl = false;
			}

			return text;
		}
	}

	public static Text bracketed(Text text) {
		return new LiteralText("[").append(text).append("]");
	}

	public static Text toText(Message message) {
		return (Text)(message instanceof Text ? (Text)message : new LiteralText(message.getString()));
	}
}
