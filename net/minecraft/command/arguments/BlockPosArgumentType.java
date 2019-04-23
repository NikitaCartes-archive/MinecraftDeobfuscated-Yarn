/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.arguments.DefaultPosArgument;
import net.minecraft.command.arguments.LookingPosArgument;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class BlockPosArgumentType
implements ArgumentType<PosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.unloaded", new Object[0]));
    public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.outofworld", new Object[0]));

    public static BlockPosArgumentType create() {
        return new BlockPosArgumentType();
    }

    public static BlockPos getLoadedBlockPos(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        BlockPos blockPos = commandContext.getArgument(string, PosArgument.class).toAbsoluteBlockPos(commandContext.getSource());
        if (!commandContext.getSource().getWorld().isBlockLoaded(blockPos)) {
            throw UNLOADED_EXCEPTION.create();
        }
        commandContext.getSource().getWorld();
        if (!ServerWorld.isValid(blockPos)) {
            throw OUT_OF_WORLD_EXCEPTION.create();
        }
        return blockPos;
    }

    public static BlockPos getBlockPos(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return commandContext.getArgument(string, PosArgument.class).toAbsoluteBlockPos(commandContext.getSource());
    }

    public PosArgument method_9699(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            return LookingPosArgument.parse(stringReader);
        }
        return DefaultPosArgument.parse(stringReader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (commandContext.getSource() instanceof CommandSource) {
            String string = suggestionsBuilder.getRemaining();
            Collection<CommandSource.RelativePosition> collection = !string.isEmpty() && string.charAt(0) == '^' ? Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL) : ((CommandSource)commandContext.getSource()).getBlockPositionSuggestions();
            return CommandSource.suggestPositions(string, collection, suggestionsBuilder, CommandManager.getCommandValidator(this::method_9699));
        }
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.method_9699(stringReader);
    }
}

