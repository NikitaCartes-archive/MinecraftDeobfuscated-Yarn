package net.minecraft;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class class_4580 implements DataCommandObject {
	private static final SuggestionProvider<ServerCommandSource> field_20856 = (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(
			method_22842(commandContext).method_22542(), suggestionsBuilder
		);
	public static final Function<String, DataCommand.ObjectType> field_20855 = string -> new DataCommand.ObjectType() {
			@Override
			public DataCommandObject getObject(CommandContext<ServerCommandSource> commandContext) {
				return new class_4580(class_4580.method_22842(commandContext), IdentifierArgumentType.getIdentifier(commandContext, string));
			}

			@Override
			public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(
				ArgumentBuilder<ServerCommandSource, ?> argumentBuilder,
				Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> function
			) {
				return argumentBuilder.then(
					CommandManager.literal("storage")
						.then(
							(ArgumentBuilder<ServerCommandSource, ?>)function.apply(
								CommandManager.argument(string, IdentifierArgumentType.identifier()).suggests(class_4580.field_20856)
							)
						)
				);
			}
		};
	private final class_4565 field_20857;
	private final Identifier field_20858;

	private static class_4565 method_22842(CommandContext<ServerCommandSource> commandContext) {
		return commandContext.getSource().getMinecraftServer().method_22827();
	}

	private class_4580(class_4565 arg, Identifier identifier) {
		this.field_20857 = arg;
		this.field_20858 = identifier;
	}

	@Override
	public void setTag(CompoundTag compoundTag) {
		this.field_20857.method_22547(this.field_20858, compoundTag);
	}

	@Override
	public CompoundTag getTag() {
		return this.field_20857.method_22546(this.field_20858);
	}

	@Override
	public Text getModifiedFeedback() {
		return new TranslatableText("commands.data.storage.modified", this.field_20858);
	}

	@Override
	public Text getQueryFeedback(Tag tag) {
		return new TranslatableText("commands.data.storage.query", this.field_20858, tag.toText());
	}

	@Override
	public Text getGetFeedback(NbtPathArgumentType.NbtPath nbtPath, double d, int i) {
		return new TranslatableText("commands.data.storage.get", nbtPath, this.field_20858, String.format(Locale.ROOT, "%.2f", d), i);
	}
}
