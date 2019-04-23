package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormat;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public class Components {
	public static Component style(Component component, Style style) {
		if (style.isEmpty()) {
			return component;
		} else {
			return component.getStyle().isEmpty() ? component.setStyle(style.clone()) : new TextComponent("").append(component).setStyle(style.clone());
		}
	}

	public static Component resolveAndStyle(@Nullable ServerCommandSource serverCommandSource, Component component, @Nullable Entity entity) throws CommandSyntaxException {
		Component component2 = component instanceof ComponentWithSelectors
			? ((ComponentWithSelectors)component).resolve(serverCommandSource, entity)
			: component.copyShallow();

		for (Component component3 : component.getSiblings()) {
			component2.append(resolveAndStyle(serverCommandSource, component3, entity));
		}

		return style(component2, component.getStyle());
	}

	public static Component profile(GameProfile gameProfile) {
		if (gameProfile.getName() != null) {
			return new TextComponent(gameProfile.getName());
		} else {
			return gameProfile.getId() != null ? new TextComponent(gameProfile.getId().toString()) : new TextComponent("(unknown)");
		}
	}

	public static Component sortedJoin(Collection<String> collection) {
		return sortedJoin(collection, string -> new TextComponent(string).applyFormat(ChatFormat.field_1060));
	}

	public static <T extends Comparable<T>> Component sortedJoin(Collection<T> collection, Function<T, Component> function) {
		if (collection.isEmpty()) {
			return new TextComponent("");
		} else if (collection.size() == 1) {
			return (Component)function.apply(collection.iterator().next());
		} else {
			List<T> list = Lists.<T>newArrayList(collection);
			list.sort(Comparable::compareTo);
			return join(collection, function);
		}
	}

	public static <T> Component join(Collection<T> collection, Function<T, Component> function) {
		if (collection.isEmpty()) {
			return new TextComponent("");
		} else if (collection.size() == 1) {
			return (Component)function.apply(collection.iterator().next());
		} else {
			Component component = new TextComponent("");
			boolean bl = true;

			for (T object : collection) {
				if (!bl) {
					component.append(new TextComponent(", ").applyFormat(ChatFormat.field_1080));
				}

				component.append((Component)function.apply(object));
				bl = false;
			}

			return component;
		}
	}

	public static Component bracketed(Component component) {
		return new TextComponent("[").append(component).append("]");
	}

	public static Component message(Message message) {
		return (Component)(message instanceof Component ? (Component)message : new TextComponent(message.getString()));
	}
}
