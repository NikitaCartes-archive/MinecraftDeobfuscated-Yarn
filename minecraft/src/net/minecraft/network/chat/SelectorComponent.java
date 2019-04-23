package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorComponent extends BaseComponent implements ComponentWithSelectors {
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
		} catch (CommandSyntaxException var4) {
			LOGGER.warn("Invalid selector component: {}", string, var4.getMessage());
		}

		this.field_11790 = entitySelector;
	}

	public String getPattern() {
		return this.pattern;
	}

	@Override
	public Component resolve(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity) throws CommandSyntaxException {
		return (Component)(serverCommandSource != null && this.field_11790 != null
			? EntitySelector.getNames(this.field_11790.getEntities(serverCommandSource))
			: new TextComponent(""));
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
		} else if (!(object instanceof SelectorComponent)) {
			return false;
		} else {
			SelectorComponent selectorComponent = (SelectorComponent)object;
			return this.pattern.equals(selectorComponent.pattern) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}
}
