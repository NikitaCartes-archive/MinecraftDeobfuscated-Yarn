/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StorageDataObject
implements DataCommandObject {
    static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestIdentifiers(StorageDataObject.of(context).getIds(), builder);
    public static final Function<String, DataCommand.ObjectType> TYPE_FACTORY = argumentName -> new DataCommand.ObjectType((String)argumentName){
        final /* synthetic */ String argumentName;
        {
            this.argumentName = string;
        }

        @Override
        public DataCommandObject getObject(CommandContext<ServerCommandSource> context) {
            return new StorageDataObject(StorageDataObject.of(context), IdentifierArgumentType.getIdentifier(context, this.argumentName));
        }

        @Override
        public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
            return argument.then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("storage").then(argumentAdder.apply(CommandManager.argument(this.argumentName, IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER))));
        }
    };
    private final DataCommandStorage storage;
    private final Identifier id;

    static DataCommandStorage of(CommandContext<ServerCommandSource> context) {
        return context.getSource().getServer().getDataCommandStorage();
    }

    StorageDataObject(DataCommandStorage storage, Identifier id) {
        this.storage = storage;
        this.id = id;
    }

    @Override
    public void setNbt(NbtCompound nbt) {
        this.storage.set(this.id, nbt);
    }

    @Override
    public NbtCompound getNbt() {
        return this.storage.get(this.id);
    }

    @Override
    public Text feedbackModify() {
        return Text.translatable("commands.data.storage.modified", this.id);
    }

    @Override
    public Text feedbackQuery(NbtElement element) {
        return Text.translatable("commands.data.storage.query", this.id, NbtHelper.toPrettyPrintedText(element));
    }

    @Override
    public Text feedbackGet(NbtPathArgumentType.NbtPath path, double scale, int result) {
        return Text.translatable("commands.data.storage.get", path, this.id, String.format(Locale.ROOT, "%.2f", scale), result);
    }
}

