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
	public static final Function<String, DataCommand.ObjectType> field_13800 = string -> new DataCommand.ObjectType() {
			@Override
			public DataCommandObject getObject(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
				return new EntityDataObject(EntityArgumentType.getEntity(context, string));
			}

			@Override
			public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(
				ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder
			) {
				return argument.then(
					CommandManager.literal("entity")
						.then((ArgumentBuilder<ServerCommandSource, ?>)argumentAdder.apply(CommandManager.argument(string, EntityArgumentType.entity())))
				);
			}
		};
	private final Entity field_13801;

	public EntityDataObject(Entity entity) {
		this.field_13801 = entity;
	}

	@Override
	public void setTag(CompoundTag tag) throws CommandSyntaxException {
		if (this.field_13801 instanceof PlayerEntity) {
			throw INVALID_ENTITY_EXCEPTION.create();
		} else {
			UUID uUID = this.field_13801.getUuid();
			this.field_13801.fromTag(tag);
			this.field_13801.setUuid(uUID);
		}
	}

	@Override
	public CompoundTag getTag() {
		return NbtPredicate.entityToTag(this.field_13801);
	}

	@Override
	public Text feedbackModify() {
		return new TranslatableText("commands.data.entity.modified", this.field_13801.getDisplayName());
	}

	@Override
	public Text feedbackQuery(Tag tag) {
		return new TranslatableText("commands.data.entity.query", this.field_13801.getDisplayName(), tag.toText());
	}

	@Override
	public Text feedbackGet(NbtPathArgumentType.NbtPath nbtPath, double scale, int result) {
		return new TranslatableText("commands.data.entity.get", nbtPath, this.field_13801.getDisplayName(), String.format(Locale.ROOT, "%.2f", scale), result);
	}
}
