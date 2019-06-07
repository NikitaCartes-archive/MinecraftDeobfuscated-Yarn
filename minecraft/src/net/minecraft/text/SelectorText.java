package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorText extends BaseText implements ParsableText {
	private static final Logger LOGGER = LogManager.getLogger();
	private final String pattern;
	@Nullable
	private final EntitySelector selector;

	public SelectorText(String string) {
		this.pattern = string;
		EntitySelector entitySelector = null;

		try {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(string));
			entitySelector = entitySelectorReader.read();
		} catch (CommandSyntaxException var4) {
			LOGGER.warn("Invalid selector component: {}", string, var4.getMessage());
		}

		this.selector = entitySelector;
	}

	public String getPattern() {
		return this.pattern;
	}

	@Override
	public Text parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
		return (Text)(serverCommandSource != null && this.selector != null
			? EntitySelector.getNames(this.selector.getEntities(serverCommandSource))
			: new LiteralText(""));
	}

	@Override
	public String asString() {
		return this.pattern;
	}

	public SelectorText method_10931() {
		return new SelectorText(this.pattern);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof SelectorText)) {
			return false;
		} else {
			SelectorText selectorText = (SelectorText)object;
			return this.pattern.equals(selectorText.pattern) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}
}
