package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

public interface class_7417 {
	class_7417 field_39004 = new class_7417() {
		public String toString() {
			return "empty";
		}
	};

	default <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
		return Optional.empty();
	}

	default <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
		return Optional.empty();
	}

	default MutableText parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
		return MutableText.method_43477(this);
	}
}
