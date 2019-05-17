/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.ChatFormat;
import net.minecraft.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentWithSelectors;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

public class Components {
    public static Component style(Component component, Style style) {
        if (style.isEmpty()) {
            return component;
        }
        if (component.getStyle().isEmpty()) {
            return component.setStyle(style.clone());
        }
        return new TextComponent("").append(component).setStyle(style.clone());
    }

    public static Component resolveAndStyle(@Nullable ServerCommandSource serverCommandSource, Component component, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (i > 100) {
            return component;
        }
        Component component2 = component instanceof ComponentWithSelectors ? ((ComponentWithSelectors)((Object)component)).resolve(serverCommandSource, entity, ++i) : component.copyShallow();
        for (Component component3 : component.getSiblings()) {
            component2.append(Components.resolveAndStyle(serverCommandSource, component3, entity, i));
        }
        return Components.style(component2, component.getStyle());
    }

    public static Component profile(GameProfile gameProfile) {
        if (gameProfile.getName() != null) {
            return new TextComponent(gameProfile.getName());
        }
        if (gameProfile.getId() != null) {
            return new TextComponent(gameProfile.getId().toString());
        }
        return new TextComponent("(unknown)");
    }

    public static Component sortedJoin(Collection<String> collection) {
        return Components.sortedJoin(collection, string -> new TextComponent((String)string).applyFormat(ChatFormat.GREEN));
    }

    public static <T extends Comparable<T>> Component sortedJoin(Collection<T> collection, Function<T, Component> function) {
        if (collection.isEmpty()) {
            return new TextComponent("");
        }
        if (collection.size() == 1) {
            return function.apply(collection.iterator().next());
        }
        ArrayList<T> list = Lists.newArrayList(collection);
        list.sort(Comparable::compareTo);
        return Components.join(collection, function);
    }

    public static <T> Component join(Collection<T> collection, Function<T, Component> function) {
        if (collection.isEmpty()) {
            return new TextComponent("");
        }
        if (collection.size() == 1) {
            return function.apply(collection.iterator().next());
        }
        TextComponent component = new TextComponent("");
        boolean bl = true;
        for (T object : collection) {
            if (!bl) {
                component.append(new TextComponent(", ").applyFormat(ChatFormat.GRAY));
            }
            component.append(function.apply(object));
            bl = false;
        }
        return component;
    }

    public static Component bracketed(Component component) {
        return new TextComponent("[").append(component).append("]");
    }

    public static Component message(Message message) {
        if (message instanceof Component) {
            return (Component)message;
        }
        return new TextComponent(message.getString());
    }
}

