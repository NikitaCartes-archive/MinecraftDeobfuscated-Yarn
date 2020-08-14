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

	public SelectorText(String pattern) {
		this.pattern = pattern;
		EntitySelector entitySelector = null;

		try {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(pattern));
			entitySelector = entitySelectorReader.read();
		} catch (CommandSyntaxException var4) {
			LOGGER.warn("Invalid selector component: {}", pattern, var4.getMessage());
		}

		this.selector = entitySelector;
	}

	public String getPattern() {
		return this.pattern;
	}

	@Override
	public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		return (MutableText)(source != null && this.selector != null ? EntitySelector.getNames(this.selector.getEntities(source)) : new LiteralText(""));
	}

	@Override
	public String asString() {
		return this.pattern;
	}

	public SelectorText copy() {
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
