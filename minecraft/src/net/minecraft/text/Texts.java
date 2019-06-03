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
	public static Text method_10889(Text text, Style style) {
		if (style.isEmpty()) {
			return text;
		} else {
			return text.method_10866().isEmpty() ? text.method_10862(style.deepCopy()) : new LiteralText("").append(text).method_10862(style.deepCopy());
		}
	}

	public static Text parse(@Nullable ServerCommandSource serverCommandSource, Text text, @Nullable Entity entity, int i) throws CommandSyntaxException {
		if (i > 100) {
			return text;
		} else {
			i++;
			Text text2 = text instanceof ParsableText ? ((ParsableText)text).parse(serverCommandSource, entity, i) : text.copy();

			for (Text text3 : text.getSiblings()) {
				text2.append(parse(serverCommandSource, text3, entity, i));
			}

			return method_10889(text2, text.method_10866());
		}
	}

	public static Text toText(GameProfile gameProfile) {
		if (gameProfile.getName() != null) {
			return new LiteralText(gameProfile.getName());
		} else {
			return gameProfile.getId() != null ? new LiteralText(gameProfile.getId().toString()) : new LiteralText("(unknown)");
		}
	}

	public static Text joinOrdered(Collection<String> collection) {
		return joinOrdered(collection, string -> new LiteralText(string).formatted(Formatting.field_1060));
	}

	public static <T extends Comparable<T>> Text joinOrdered(Collection<T> collection, Function<T, Text> function) {
		if (collection.isEmpty()) {
			return new LiteralText("");
		} else if (collection.size() == 1) {
			return (Text)function.apply(collection.iterator().next());
		} else {
			List<T> list = Lists.<T>newArrayList(collection);
			list.sort(Comparable::compareTo);
			return join(collection, function);
		}
	}

	public static <T> Text join(Collection<T> collection, Function<T, Text> function) {
		if (collection.isEmpty()) {
			return new LiteralText("");
		} else if (collection.size() == 1) {
			return (Text)function.apply(collection.iterator().next());
		} else {
			Text text = new LiteralText("");
			boolean bl = true;

			for (T object : collection) {
				if (!bl) {
					text.append(new LiteralText(", ").formatted(Formatting.field_1080));
				}

				text.append((Text)function.apply(object));
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
