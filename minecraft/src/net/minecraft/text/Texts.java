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
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

public class Texts {
	public static final String DEFAULT_SEPARATOR = ", ";
	public static final Text GRAY_DEFAULT_SEPARATOR_TEXT = Text.literal(", ").formatted(Formatting.GRAY);
	public static final Text DEFAULT_SEPARATOR_TEXT = Text.literal(", ");

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

	public static Optional<MutableText> parse(@Nullable ServerCommandSource source, Optional<Text> text, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		return text.isPresent() ? Optional.of(parse(source, (Text)text.get(), sender, depth)) : Optional.empty();
	}

	public static MutableText parse(@Nullable ServerCommandSource source, Text text, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		if (depth > 100) {
			return text.shallowCopy();
		} else {
			MutableText mutableText = text.getContent().parse(source, sender, depth + 1);

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
			return Text.literal(profile.getName());
		} else {
			return profile.getId() != null ? Text.literal(profile.getId().toString()) : Text.literal("(unknown)");
		}
	}

	public static Text joinOrdered(Collection<String> strings) {
		return joinOrdered(strings, string -> Text.literal(string).formatted(Formatting.GREEN));
	}

	public static <T extends Comparable<T>> Text joinOrdered(Collection<T> elements, Function<T, Text> transformer) {
		if (elements.isEmpty()) {
			return ScreenTexts.EMPTY;
		} else if (elements.size() == 1) {
			return (Text)transformer.apply((Comparable)elements.iterator().next());
		} else {
			List<T> list = Lists.<T>newArrayList(elements);
			list.sort(Comparable::compareTo);
			return join(list, transformer);
		}
	}

	public static <T> Text join(Collection<? extends T> elements, Function<T, Text> transformer) {
		return join(elements, GRAY_DEFAULT_SEPARATOR_TEXT, transformer);
	}

	public static <T> MutableText join(Collection<? extends T> elements, Optional<? extends Text> separator, Function<T, Text> transformer) {
		return join(elements, DataFixUtils.orElse(separator, GRAY_DEFAULT_SEPARATOR_TEXT), transformer);
	}

	public static Text join(Collection<? extends Text> texts, Text separator) {
		return join(texts, separator, Function.identity());
	}

	public static <T> MutableText join(Collection<? extends T> elements, Text separator, Function<T, Text> transformer) {
		if (elements.isEmpty()) {
			return Text.empty();
		} else if (elements.size() == 1) {
			return ((Text)transformer.apply(elements.iterator().next())).shallowCopy();
		} else {
			MutableText mutableText = Text.empty();
			boolean bl = true;

			for (T object : elements) {
				if (!bl) {
					mutableText.append(separator);
				}

				mutableText.append((Text)transformer.apply(object));
				bl = false;
			}

			return mutableText;
		}
	}

	public static MutableText bracketed(Text text) {
		return Text.translatable("chat.square_brackets", text);
	}

	public static Text toText(Message message) {
		return (Text)(message instanceof Text ? (Text)message : Text.literal(message.getString()));
	}

	public static boolean hasTranslation(@Nullable Text text) {
		if (text instanceof TranslatableTextContent translatableTextContent) {
			String string = translatableTextContent.getKey();
			return Language.getInstance().hasTranslation(string);
		} else {
			return true;
		}
	}

	@Deprecated(
		forRemoval = true
	)
	public static Text brokenReplaceTranslationKey(Text text, String oldKey, String updatedKey) {
		if (text instanceof TranslatableTextContent translatableTextContent && oldKey.equals(translatableTextContent.getKey())) {
			return Text.translatable(updatedKey, translatableTextContent.getArgs());
		}

		return text;
	}
}
