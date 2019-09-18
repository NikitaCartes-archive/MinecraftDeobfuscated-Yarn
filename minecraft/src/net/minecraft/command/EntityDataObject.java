package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class EntityDataObject implements DataCommandObject {
	private static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.data.entity.invalid"));
	public static final Function<String, DataCommand.ObjectType> TYPE_FACTORY = string -> new DataCommand.ObjectType() {
			@Override
			public DataCommandObject getObject(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
				return new EntityDataObject(EntityArgumentType.getEntity(commandContext, string));
			}

			@Override
			public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(
				ArgumentBuilder<ServerCommandSource, ?> argumentBuilder,
				Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> function
			) {
				return argumentBuilder.then(
					CommandManager.literal("entity")
						.then((ArgumentBuilder<ServerCommandSource, ?>)function.apply(CommandManager.argument(string, EntityArgumentType.entity())))
				);
			}
		};
	private final Entity entity;

	public EntityDataObject(Entity entity) {
		this.entity = entity;
	}

	@Override
	public void setTag(CompoundTag compoundTag) throws CommandSyntaxException {
		if (this.entity instanceof PlayerEntity) {
			throw INVALID_ENTITY_EXCEPTION.create();
		} else {
			UUID uUID = this.entity.getUuid();
			this.entity.fromTag(compoundTag);
			this.entity.setUuid(uUID);
		}
	}

	@Override
	public CompoundTag getTag() {
		return NbtPredicate.entityToTag(this.entity);
	}

	@Override
	public Text feedbackModify() {
		return new TranslatableText("commands.data.entity.modified", this.entity.getDisplayName());
	}

	@Override
	public Text feedbackQuery(Tag tag) {
		return new TranslatableText("commands.data.entity.query", this.entity.getDisplayName(), tag.toText());
	}

	@Override
	public Text feedbackGet(NbtPathArgumentType.NbtPath nbtPath, double d, int i) {
		return new TranslatableText("commands.data.entity.get", nbtPath, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", d), i);
	}
}
