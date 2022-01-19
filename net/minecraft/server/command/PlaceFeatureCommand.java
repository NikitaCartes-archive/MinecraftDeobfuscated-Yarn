/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class PlaceFeatureCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.placefeature.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("placefeature").requires(source -> source.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("feature", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.AVAILABLE_FEATURES).executes(context -> PlaceFeatureCommand.execute((ServerCommandSource)context.getSource(), IdentifierArgumentType.getConfiguredFeatureEntry(context, "feature"), new BlockPos(((ServerCommandSource)context.getSource()).getPosition())))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(context -> PlaceFeatureCommand.execute((ServerCommandSource)context.getSource(), IdentifierArgumentType.getConfiguredFeatureEntry(context, "feature"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"))))));
    }

    public static int execute(ServerCommandSource source, IdentifierArgumentType.RegistryEntry<ConfiguredFeature<?, ?>> configuredFeatureEntry, BlockPos pos) throws CommandSyntaxException {
        ServerWorld serverWorld = source.getWorld();
        ConfiguredFeature<?, ?> configuredFeature = configuredFeatureEntry.resource();
        if (!configuredFeature.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), serverWorld.getRandom(), pos)) {
            throw FAILED_EXCEPTION.create();
        }
        Identifier identifier = configuredFeatureEntry.id();
        source.sendFeedback(new TranslatableText("commands.placefeature.success", identifier, pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }
}

