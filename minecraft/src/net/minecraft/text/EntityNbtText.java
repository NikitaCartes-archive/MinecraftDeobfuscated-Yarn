package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_7419;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;

public record EntityNbtText(String rawSelector, @Nullable EntitySelector selector) implements class_7419 {
	public EntityNbtText(String rawPath) {
		this(rawPath, parseSelector(rawPath));
	}

	@Nullable
	private static EntitySelector parseSelector(String rawSelector) {
		try {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(rawSelector));
			return entitySelectorReader.read();
		} catch (CommandSyntaxException var2) {
			return null;
		}
	}

	@Override
	public Stream<NbtCompound> toNbt(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		if (this.selector != null) {
			List<? extends Entity> list = this.selector.getEntities(serverCommandSource);
			return list.stream().map(NbtPredicate::entityToNbt);
		} else {
			return Stream.empty();
		}
	}

	public String toString() {
		return "entity=" + this.rawSelector;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof EntityNbtText entityNbtText && this.rawSelector.equals(entityNbtText.rawSelector)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.rawSelector});
	}
}
