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
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class EntityDataObject implements DataCommandObject {
	private static final SimpleCommandExceptionType field_13799 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.data.entity.invalid"));
	public static final Function<String, DataCommand.class_3167> field_13800 = string -> new DataCommand.class_3167() {
			@Override
			public DataCommandObject method_13924(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
				return new EntityDataObject(EntityArgumentType.method_9313(commandContext, string));
			}

			@Override
			public ArgumentBuilder<ServerCommandSource, ?> method_13925(
				ArgumentBuilder<ServerCommandSource, ?> argumentBuilder,
				Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> function
			) {
				return argumentBuilder.then(
					ServerCommandManager.literal("entity")
						.then((ArgumentBuilder<ServerCommandSource, ?>)function.apply(ServerCommandManager.argument(string, EntityArgumentType.oneEntity())))
				);
			}
		};
	private final Entity field_13801;

	public EntityDataObject(Entity entity) {
		this.field_13801 = entity;
	}

	@Override
	public void method_13880(CompoundTag compoundTag) throws CommandSyntaxException {
		if (this.field_13801 instanceof PlayerEntity) {
			throw field_13799.create();
		} else {
			UUID uUID = this.field_13801.getUuid();
			this.field_13801.method_5651(compoundTag);
			this.field_13801.setUuid(uUID);
		}
	}

	@Override
	public CompoundTag method_13881() {
		return NbtPredicate.method_9076(this.field_13801);
	}

	@Override
	public TextComponent method_13883() {
		return new TranslatableTextComponent("commands.data.entity.modified", this.field_13801.method_5476());
	}

	@Override
	public TextComponent method_13882(Tag tag) {
		return new TranslatableTextComponent("commands.data.entity.query", this.field_13801.method_5476(), tag.method_10715());
	}

	@Override
	public TextComponent method_13879(NbtPathArgumentType.class_2209 arg, double d, int i) {
		return new TranslatableTextComponent("commands.data.entity.get", arg, this.field_13801.method_5476(), String.format(Locale.ROOT, "%.2f", d), i);
	}
}
