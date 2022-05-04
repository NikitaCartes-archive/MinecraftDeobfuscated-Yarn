package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;

public class SelectorTextContent implements TextContent {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final String pattern;
	@Nullable
	private final EntitySelector selector;
	protected final Optional<Text> separator;

	public SelectorTextContent(String pattern, Optional<Text> separator) {
		this.pattern = pattern;
		this.separator = separator;
		this.selector = readSelector(pattern);
	}

	@Nullable
	private static EntitySelector readSelector(String pattern) {
		EntitySelector entitySelector = null;

		try {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(pattern));
			entitySelector = entitySelectorReader.read();
		} catch (CommandSyntaxException var3) {
			LOGGER.warn("Invalid selector component: {}: {}", pattern, var3.getMessage());
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
	public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		if (source != null && this.selector != null) {
			Optional<? extends Text> optional = Texts.parse(source, this.separator, sender, depth);
			return Texts.join(this.selector.getEntities(source), optional, Entity::getDisplayName);
		} else {
			return Text.empty();
		}
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
		return visitor.accept(style, this.pattern);
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
		return visitor.accept(this.pattern);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof SelectorTextContent selectorTextContent
				&& this.pattern.equals(selectorTextContent.pattern)
				&& this.separator.equals(selectorTextContent.separator)) {
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
