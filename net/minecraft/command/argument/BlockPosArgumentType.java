/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.command.argument.LookingPosArgument;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPosArgumentType
implements ArgumentType<PosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos.unloaded"));
    public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos.outofworld"));
    public static final SimpleCommandExceptionType OUT_OF_BOUNDS_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos.outofbounds"));

    public static BlockPosArgumentType blockPos() {
        return new BlockPosArgumentType();
    }

    public static BlockPos getLoadedBlockPos(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        ServerWorld serverWorld = context.getSource().getWorld();
        return BlockPosArgumentType.getLoadedBlockPos(context, serverWorld, name);
    }

    public static BlockPos getLoadedBlockPos(CommandContext<ServerCommandSource> context, ServerWorld world, String name) throws CommandSyntaxException {
        BlockPos blockPos = BlockPosArgumentType.getBlockPos(context, name);
        if (!world.isChunkLoaded(blockPos)) {
            throw UNLOADED_EXCEPTION.create();
        }
        if (!world.isInBuildLimit(blockPos)) {
            throw OUT_OF_WORLD_EXCEPTION.create();
        }
        return blockPos;
    }

    public static BlockPos getBlockPos(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, PosArgument.class).toAbsoluteBlockPos(context.getSource());
    }

    public static BlockPos getValidBlockPos(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        BlockPos blockPos = BlockPosArgumentType.getBlockPos(context, name);
        if (!World.isValid(blockPos)) {
            throw OUT_OF_BOUNDS_EXCEPTION.create();
        }
        return blockPos;
    }

    @Override
    public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            return LookingPosArgument.parse(stringReader);
        }
        return DefaultPosArgument.parse(stringReader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            String string = builder.getRemaining();
            Collection<CommandSource.RelativePosition> collection = !string.isEmpty() && string.charAt(0) == '^' ? Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL) : ((CommandSource)context.getSource()).getBlockPositionSuggestions();
            return CommandSource.suggestPositions(string, collection, builder, CommandManager.getCommandValidator(this::parse));
        }
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }
}

