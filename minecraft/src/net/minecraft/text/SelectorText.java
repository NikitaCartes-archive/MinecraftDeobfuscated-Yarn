package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
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
	protected final Optional<Text> field_33540;

	public SelectorText(String pattern, Optional<Text> optional) {
		this.pattern = pattern;
		this.field_33540 = optional;
		EntitySelector entitySelector = null;

		try {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(pattern));
			entitySelector = entitySelectorReader.read();
		} catch (CommandSyntaxException var5) {
			LOGGER.warn("Invalid selector component: {}: {}", pattern, var5.getMessage());
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
		if (source != null && this.selector != null) {
			Optional<? extends Text> optional = Texts.method_36330(source, this.field_33540, sender, depth);
			return Texts.method_36331(this.selector.getEntities(source), optional, Entity::getDisplayName);
		} else {
			return new LiteralText("");
		}
	}

	@Override
	public String asString() {
		return this.pattern;
	}

	public SelectorText copy() {
		return new SelectorText(this.pattern, this.field_33540);
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
