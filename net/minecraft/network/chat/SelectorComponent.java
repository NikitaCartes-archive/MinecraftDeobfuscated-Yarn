/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentWithSelectors;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SelectorComponent
extends BaseComponent
implements ComponentWithSelectors {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String pattern;
    @Nullable
    private final EntitySelector field_11790;

    public SelectorComponent(String string) {
        this.pattern = string;
        EntitySelector entitySelector = null;
        try {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(string));
            entitySelector = entitySelectorReader.read();
        } catch (CommandSyntaxException commandSyntaxException) {
            LOGGER.warn("Invalid selector component: {}", (Object)string, (Object)commandSyntaxException.getMessage());
        }
        this.field_11790 = entitySelector;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Override
    public Component resolve(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity) throws CommandSyntaxException {
        if (serverCommandSource == null || this.field_11790 == null) {
            return new TextComponent("");
        }
        return EntitySelector.getNames(this.field_11790.getEntities(serverCommandSource));
    }

    @Override
    public String getText() {
        return this.pattern;
    }

    public SelectorComponent method_10931() {
        return new SelectorComponent(this.pattern);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof SelectorComponent) {
            SelectorComponent selectorComponent = (SelectorComponent)object;
            return this.pattern.equals(selectorComponent.pattern) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    @Override
    public /* synthetic */ Component copyShallow() {
        return this.method_10931();
    }
}

