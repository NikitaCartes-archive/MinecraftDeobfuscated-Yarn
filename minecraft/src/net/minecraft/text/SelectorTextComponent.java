package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.class_2566;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorTextComponent extends AbstractTextComponent implements class_2566 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final String pattern;
	@Nullable
	private final EntitySelector field_11790;

	public SelectorTextComponent(String string) {
		this.pattern = string;
		EntitySelector entitySelector = null;

		try {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(string));
			entitySelector = entitySelectorReader.read();
		} catch (CommandSyntaxException var4) {
			LOGGER.warn("Invalid selector component: {}", string, var4.getMessage());
		}

		this.field_11790 = entitySelector;
	}

	public String getPattern() {
		return this.pattern;
	}

	@Override
	public TextComponent method_10890(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity) throws CommandSyntaxException {
		return (TextComponent)(serverCommandSource != null && this.field_11790 != null
			? EntitySelector.getNames(this.field_11790.getEntities(serverCommandSource))
			: new StringTextComponent(""));
	}

	@Override
	public String getText() {
		return this.pattern;
	}

	public SelectorTextComponent getTextComponent() {
		return new SelectorTextComponent(this.pattern);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof SelectorTextComponent)) {
			return false;
		} else {
			SelectorTextComponent selectorTextComponent = (SelectorTextComponent)object;
			return this.pattern.equals(selectorTextComponent.pattern) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.children + ", style=" + this.getStyle() + '}';
	}
}
