package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;

public record EntityNbtDataSource(String rawSelector, @Nullable EntitySelector selector) implements NbtDataSource {
	public static final MapCodec<EntityNbtDataSource> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Codec.STRING.fieldOf("entity").forGetter(EntityNbtDataSource::rawSelector)).apply(instance, EntityNbtDataSource::new)
	);
	public static final NbtDataSource.Type<EntityNbtDataSource> TYPE = new NbtDataSource.Type<>(CODEC, "entity");

	public EntityNbtDataSource(String rawPath) {
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
	public Stream<NbtCompound> get(ServerCommandSource source) throws CommandSyntaxException {
		if (this.selector != null) {
			List<? extends Entity> list = this.selector.getEntities(source);
			return list.stream().map(NbtPredicate::entityToNbt);
		} else {
			return Stream.empty();
		}
	}

	@Override
	public NbtDataSource.Type<?> getType() {
		return TYPE;
	}

	public String toString() {
		return "entity=" + this.rawSelector;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof EntityNbtDataSource entityNbtDataSource && this.rawSelector.equals(entityNbtDataSource.rawSelector)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.rawSelector.hashCode();
	}
}
