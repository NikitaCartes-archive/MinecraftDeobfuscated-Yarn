/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;

public class DebugMobSpawningCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("debugmobspawning").requires(source -> source.hasPermissionLevel(2));
        for (SpawnGroup spawnGroup : SpawnGroup.values()) {
            literalArgumentBuilder.then(CommandManager.literal(spawnGroup.getName()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("at", BlockPosArgumentType.blockPos()).executes(context -> DebugMobSpawningCommand.execute((ServerCommandSource)context.getSource(), spawnGroup, BlockPosArgumentType.getLoadedBlockPos(context, "at")))));
        }
        dispatcher.register(literalArgumentBuilder);
    }

    private static int execute(ServerCommandSource source, SpawnGroup group, BlockPos pos) {
        SpawnHelper.spawnEntitiesInChunk(group, source.getWorld(), pos);
        return 1;
    }
}

