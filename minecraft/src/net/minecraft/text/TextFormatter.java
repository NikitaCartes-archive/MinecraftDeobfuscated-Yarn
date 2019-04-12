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

public class TextFormatter {
	public static TextComponent addStyle(TextComponent textComponent, Style style) {
		if (style.isEmpty()) {
			return textComponent;
		} else {
			return textComponent.getStyle().isEmpty()
				? textComponent.setStyle(style.clone())
				: new StringTextComponent("").append(textComponent).setStyle(style.clone());
		}
	}

	public static TextComponent method_10881(@Nullable ServerCommandSource serverCommandSource, TextComponent textComponent, @Nullable Entity entity) throws CommandSyntaxException {
		TextComponent textComponent2 = textComponent instanceof TextComponentWithSelectors
			? ((TextComponentWithSelectors)textComponent).resolveSelectors(serverCommandSource, entity)
			: textComponent.copyShallow();

		for (TextComponent textComponent3 : textComponent.getSiblings()) {
			textComponent2.append(method_10881(serverCommandSource, textComponent3, entity));
		}

		return addStyle(textComponent2, textComponent.getStyle());
	}

	public static TextComponent profile(GameProfile gameProfile) {
		if (gameProfile.getName() != null) {
			return new StringTextComponent(gameProfile.getName());
		} else {
			return gameProfile.getId() != null ? new StringTextComponent(gameProfile.getId().toString()) : new StringTextComponent("(unknown)");
		}
	}

	public static TextComponent sortedJoin(Collection<String> collection) {
		return sortedJoin(collection, string -> new StringTextComponent(string).applyFormat(TextFormat.field_1060));
	}

	public static <T extends Comparable<T>> TextComponent sortedJoin(Collection<T> collection, Function<T, TextComponent> function) {
		if (collection.isEmpty()) {
			return new StringTextComponent("");
		} else if (collection.size() == 1) {
			return (TextComponent)function.apply(collection.iterator().next());
		} else {
			List<T> list = Lists.<T>newArrayList(collection);
			list.sort(Comparable::compareTo);
			return join(collection, function);
		}
	}

	public static <T> TextComponent join(Collection<T> collection, Function<T, TextComponent> function) {
		if (collection.isEmpty()) {
			return new StringTextComponent("");
		} else if (collection.size() == 1) {
			return (TextComponent)function.apply(collection.iterator().next());
		} else {
			TextComponent textComponent = new StringTextComponent("");
			boolean bl = true;

			for (T object : collection) {
				if (!bl) {
					textComponent.append(new StringTextComponent(", ").applyFormat(TextFormat.field_1080));
				}

				textComponent.append((TextComponent)function.apply(object));
				bl = false;
			}

			return textComponent;
		}
	}

	public static TextComponent bracketed(TextComponent textComponent) {
		return new StringTextComponent("[").append(textComponent).append("]");
	}

	public static TextComponent message(Message message) {
		return (TextComponent)(message instanceof TextComponent ? (TextComponent)message : new StringTextComponent(message.getString()));
	}
}
