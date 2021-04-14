/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SelectorText
extends BaseText
implements ParsableText {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String pattern;
    @Nullable
    private final EntitySelector selector;
    protected final Optional<Text> field_33540;

    public SelectorText(String pattern, Optional<Text> optional) {
        this.pattern = pattern;
        this.field_33540 = optional;
        EntitySelector entitySelector = null;
        try {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(pattern));
            entitySelector = entitySelectorReader.read();
        } catch (CommandSyntaxException commandSyntaxException) {
            LOGGER.warn("Invalid selector component: {}: {}", (Object)pattern, (Object)commandSyntaxException.getMessage());
        }
        this.selector = entitySelector;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
    }

    public Optional<Text> method_36339() {
        return this.field_33540;
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source == null || this.selector == null) {
            return new LiteralText("");
        }
        Optional<MutableText> optional = Texts.method_36330(source, this.field_33540, sender, depth);
        return Texts.method_36331(this.selector.getEntities(source), optional, Entity::getDisplayName);
    }

    @Override
    public String asString() {
        return this.pattern;
    }

    @Override
    public SelectorText copy() {
        return new SelectorText(this.pattern, this.field_33540);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof SelectorText) {
            SelectorText selectorText = (SelectorText)object;
            return this.pattern.equals(selectorText.pattern) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    @Override
    public /* synthetic */ BaseText copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ MutableText copy() {
        return this.copy();
    }
}

