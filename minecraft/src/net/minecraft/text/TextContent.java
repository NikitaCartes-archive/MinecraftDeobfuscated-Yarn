package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;

/**
 * Represents type-specific content of text. It is stored in each tree node
 * in a text tree structure. Its implementations are immutable.
 */
public interface TextContent {
	/**
	 * Visits this content. Returns a value if the visitor terminates amid
	 * the visit, or {@code Optional.empty()} if it proceeds.
	 * 
	 * @return {@code Optional.empty()} if the visit finished, or a terminating
	 * result from the {@code visitor}
	 * @see Text#visit(StringVisitable.StyledVisitor, Style)
	 */
	default <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
		return Optional.empty();
	}

	/**
	 * Visits this content. Returns a value if the visitor terminates amid
	 * the visit, or {@code Optional.empty()} if it proceeds.
	 * 
	 * @return {@code Optional.empty()} if the visit finished, or a terminating
	 * result from the {@code visitor}
	 * @see Text#visit(StringVisitable.Visitor)
	 */
	default <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
		return Optional.empty();
	}

	/**
	 * Parses this content into a basic mutable text without custom style or
	 * siblings. The resulting text may or may not have this content.
	 */
	default MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		return MutableText.of(this);
	}

	TextContent.Type<?> getType();

	public static record Type<T extends TextContent>(MapCodec<T> codec, String id) implements StringIdentifiable {
		@Override
		public String asString() {
			return this.id;
		}
	}
}
