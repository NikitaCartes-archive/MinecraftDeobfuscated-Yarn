package net.minecraft.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

public class Texts {
	public static final String field_33536 = ", ";
	public static final Text field_33537 = new LiteralText(", ").formatted(Formatting.GRAY);
	public static final Text field_33538 = new LiteralText(", ");

	public static MutableText setStyleIfAbsent(MutableText text, Style style) {
		if (style.isEmpty()) {
			return text;
		} else {
			Style style2 = text.getStyle();
			if (style2.isEmpty()) {
				return text.setStyle(style);
			} else {
				return style2.equals(style) ? text : text.setStyle(style2.withParent(style));
			}
		}
	}

	public static Optional<MutableText> method_36330(@Nullable ServerCommandSource serverCommandSource, Optional<Text> optional, @Nullable Entity entity, int i) throws CommandSyntaxException {
		return optional.isPresent() ? Optional.of(parse(serverCommandSource, (Text)optional.get(), entity, i)) : Optional.empty();
	}

	public static MutableText parse(@Nullable ServerCommandSource source, Text text, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		if (depth > 100) {
			return text.shallowCopy();
		} else {
			MutableText mutableText = text instanceof ParsableText ? ((ParsableText)text).parse(source, sender, depth + 1) : text.copy();

			for (Text text2 : text.getSiblings()) {
				mutableText.append(parse(source, text2, sender, depth + 1));
			}

			return mutableText.fillStyle(parseStyle(source, text.getStyle(), sender, depth));
		}
	}

	private static Style parseStyle(@Nullable ServerCommandSource source, Style style, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		HoverEvent hoverEvent = style.getHoverEvent();
		if (hoverEvent != null) {
			Text text = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
			if (text != null) {
				HoverEvent hoverEvent2 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, parse(source, text, sender, depth + 1));
				return style.withHoverEvent(hoverEvent2);
			}
		}

		return style;
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
			return LiteralText.EMPTY;
		} else if (elements.size() == 1) {
			return (Text)transformer.apply(elements.iterator().next());
		} else {
			List<T> list = Lists.<T>newArrayList(elements);
			list.sort(Comparable::compareTo);
			return join(list, transformer);
		}
	}

	public static <T> Text join(Collection<T> elements, Function<T, Text> transformer) {
		return method_36332(elements, field_33537, transformer);
	}

	public static <T> MutableText method_36331(Collection<T> collection, Optional<? extends Text> optional, Function<T, Text> function) {
		return method_36332(collection, DataFixUtils.orElse(optional, field_33537), function);
	}

	public static <T> MutableText method_36332(Collection<T> collection, Text text, Function<T, Text> function) {
		if (collection.isEmpty()) {
			return new LiteralText("");
		} else if (collection.size() == 1) {
			return ((Text)function.apply(collection.iterator().next())).shallowCopy();
		} else {
			MutableText mutableText = new LiteralText("");
			boolean bl = true;

			for (T object : collection) {
				if (!bl) {
					mutableText.append(text);
				}

				mutableText.append((Text)function.apply(object));
				bl = false;
			}

			return mutableText;
		}
	}

	public static MutableText bracketed(Text text) {
		return new TranslatableText("chat.square_brackets", text);
	}

	public static Text toText(Message message) {
		return (Text)(message instanceof Text ? (Text)message : new LiteralText(message.getString()));
	}
}
