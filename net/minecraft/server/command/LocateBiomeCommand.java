/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public class LocateBiomeCommand {
    private static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("commands.locatebiome.notFound", id));
    private static final int RADIUS = 6400;
    private static final int HORIZONTAL_BLOCK_CHECK_INTERVAL = 32;
    private static final int VERTICAL_BLOCK_CHECK_INTERVAL = 64;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("locatebiome").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("biome", RegistryPredicateArgumentType.registryPredicate(Registry.BIOME_KEY)).executes(context -> LocateBiomeCommand.execute((ServerCommandSource)context.getSource(), RegistryPredicateArgumentType.getBiomePredicate(context, "biome")))));
    }

    private static int execute(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<Biome> biome) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(source.getPosition());
        Pair<BlockPos, RegistryEntry<Biome>> pair = source.getWorld().locateBiome(biome, blockPos, 6400, 32, 64);
        if (pair == null) {
            throw NOT_FOUND_EXCEPTION.create(biome.asString());
        }
        return LocateCommand.sendCoordinates(source, biome, blockPos, pair, "commands.locatebiome.success", true);
    }
}

