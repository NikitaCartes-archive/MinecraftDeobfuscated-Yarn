package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.class_7417;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;

public class SelectorText implements class_7417 {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final String pattern;
	@Nullable
	private final EntitySelector selector;
	protected final Optional<Text> separator;

	public SelectorText(String string, Optional<Text> separator) {
		this.pattern = string;
		this.separator = separator;
		this.selector = method_43486(string);
	}

	@Nullable
	private static EntitySelector method_43486(String string) {
		EntitySelector entitySelector = null;

		try {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(string));
			entitySelector = entitySelectorReader.read();
		} catch (CommandSyntaxException var3) {
			LOGGER.warn("Invalid selector component: {}: {}", string, var3.getMessage());
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
		if (serverCommandSource != null && this.selector != null) {
			Optional<? extends Text> optional = Texts.parse(serverCommandSource, this.separator, entity, i);
			return Texts.join(this.selector.getEntities(serverCommandSource), optional, Entity::getDisplayName);
		} else {
			return Text.method_43473();
		}
	}

	@Override
	public <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
		return styledVisitor.accept(style, this.pattern);
	}

	@Override
	public <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
		return visitor.accept(this.pattern);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof SelectorText selectorText && this.pattern.equals(selectorText.pattern) && this.separator.equals(selectorText.separator)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		int i = this.pattern.hashCode();
		return 31 * i + this.separator.hashCode();
	}

	public String toString() {
		return "pattern{" + this.pattern + "}";
	}
}
