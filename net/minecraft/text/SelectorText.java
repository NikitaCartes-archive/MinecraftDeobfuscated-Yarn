/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import net.minecraft.class_7417;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SelectorText
implements class_7417 {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String pattern;
    @Nullable
    private final EntitySelector selector;
    protected final Optional<Text> separator;

    public SelectorText(String string, Optional<Text> separator) {
        this.pattern = string;
        this.separator = separator;
        this.selector = SelectorText.method_43486(string);
    }

    @Nullable
    private static EntitySelector method_43486(String string) {
        EntitySelector entitySelector = null;
        try {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(string));
            entitySelector = entitySelectorReader.read();
        } catch (CommandSyntaxException commandSyntaxException) {
            LOGGER.warn("Invalid selector component: {}: {}", (Object)string, (Object)commandSyntaxException.getMessage());
        }
        return entitySelector;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
    }

    public Optional<Text> getSeparator() {
        return this.separator;
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (serverCommandSource == null || this.selector == null) {
            return Text.method_43473();
        }
        Optional<MutableText> optional = Texts.parse(serverCommandSource, this.separator, entity, i);
        return Texts.join(this.selector.getEntities(serverCommandSource), optional, Entity::getDisplayName);
    }

    @Override
    public <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        return styledVisitor.accept(style, this.pattern);
    }

    @Override
    public <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(this.pattern);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SelectorText)) return false;
        SelectorText selectorText = (SelectorText)object;
        if (!this.pattern.equals(selectorText.pattern)) return false;
        if (!this.separator.equals(selectorText.separator)) return false;
        return true;
    }

    public int hashCode() {
        int i = this.pattern.hashCode();
        i = 31 * i + this.separator.hashCode();
        return i;
    }

    public String toString() {
        return "pattern{" + this.pattern + "}";
    }
}

