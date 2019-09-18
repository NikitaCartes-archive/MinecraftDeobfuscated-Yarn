package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
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

public class StorageDataObject implements DataCommandObject {
	private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(
			of(commandContext).getIds(), suggestionsBuilder
		);
	public static final Function<String, DataCommand.ObjectType> TYPE_FACTORY = string -> new DataCommand.ObjectType() {
			@Override
			public DataCommandObject getObject(CommandContext<ServerCommandSource> commandContext) {
				return new StorageDataObject(StorageDataObject.of(commandContext), IdentifierArgumentType.getIdentifier(commandContext, string));
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
								CommandManager.argument(string, IdentifierArgumentType.identifier()).suggests(StorageDataObject.SUGGESTION_PROVIDER)
							)
						)
				);
			}
		};
	private final DataCommandStorage storage;
	private final Identifier id;

	private static DataCommandStorage of(CommandContext<ServerCommandSource> commandContext) {
		return commandContext.getSource().getMinecraftServer().getDataCommandStorage();
	}

	private StorageDataObject(DataCommandStorage dataCommandStorage, Identifier identifier) {
		this.storage = dataCommandStorage;
		this.id = identifier;
	}

	@Override
	public void setTag(CompoundTag compoundTag) {
		this.storage.set(this.id, compoundTag);
	}

	@Override
	public CompoundTag getTag() {
		return this.storage.get(this.id);
	}

	@Override
	public Text feedbackModify() {
		return new TranslatableText("commands.data.storage.modified", this.id);
	}

	@Override
	public Text feedbackQuery(Tag tag) {
		return new TranslatableText("commands.data.storage.query", this.id, tag.toText());
	}

	@Override
	public Text feedbackGet(NbtPathArgumentType.NbtPath nbtPath, double d, int i) {
		return new TranslatableText("commands.data.storage.get", nbtPath, this.id, String.format(Locale.ROOT, "%.2f", d), i);
	}
}
