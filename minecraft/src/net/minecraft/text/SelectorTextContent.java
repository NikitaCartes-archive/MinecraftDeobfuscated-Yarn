package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public record SelectorTextContent(ParsedSelector selector, Optional<Text> separator) implements TextContent {
	public static final MapCodec<SelectorTextContent> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					ParsedSelector.CODEC.fieldOf("selector").forGetter(SelectorTextContent::selector),
					TextCodecs.CODEC.optionalFieldOf("separator").forGetter(SelectorTextContent::separator)
				)
				.apply(instance, SelectorTextContent::new)
	);
	public static final TextContent.Type<SelectorTextContent> TYPE = new TextContent.Type<>(CODEC, "selector");

	@Override
	public TextContent.Type<?> getType() {
		return TYPE;
	}

	@Override
	public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		if (source == null) {
			return Text.empty();
		} else {
			Optional<? extends Text> optional = Texts.parse(source, this.separator, sender, depth);
			return Texts.join(this.selector.comp_3068().getEntities(source), optional, Entity::getDisplayName);
		}
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
		return visitor.accept(style, this.selector.comp_3067());
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
		return visitor.accept(this.selector.comp_3067());
	}

	public String toString() {
		return "pattern{" + this.selector + "}";
	}
}
