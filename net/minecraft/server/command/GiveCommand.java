/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class GiveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("give").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.create()).executes(commandContext -> GiveCommand.execute((ServerCommandSource)commandContext.getSource(), ItemStackArgumentType.getItemStackArgument(commandContext, "item"), EntityArgumentType.getPlayers(commandContext, "targets"), 1))).then(CommandManager.argument("count", IntegerArgumentType.integer(1)).executes(commandContext -> GiveCommand.execute((ServerCommandSource)commandContext.getSource(), ItemStackArgumentType.getItemStackArgument(commandContext, "item"), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "count")))))));
    }

    private static int execute(ServerCommandSource serverCommandSource, ItemStackArgument itemStackArgument, Collection<ServerPlayerEntity> collection, int i) throws CommandSyntaxException {
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            int j = i;
            while (j > 0) {
                ItemEntity itemEntity;
                int k = Math.min(itemStackArgument.getItem().getMaxAmount(), j);
                j -= k;
                ItemStack itemStack = itemStackArgument.createStack(k, false);
                boolean bl = serverPlayerEntity.inventory.insertStack(itemStack);
                if (!bl || !itemStack.isEmpty()) {
                    itemEntity = serverPlayerEntity.dropItem(itemStack, false);
                    if (itemEntity == null) continue;
                    itemEntity.resetPickupDelay();
                    itemEntity.setOwner(serverPlayerEntity.getUuid());
                    continue;
                }
                itemStack.setAmount(1);
                itemEntity = serverPlayerEntity.dropItem(itemStack, false);
                if (itemEntity != null) {
                    itemEntity.method_6987();
                }
                serverPlayerEntity.world.playSound(null, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((serverPlayerEntity.getRand().nextFloat() - serverPlayerEntity.getRand().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                serverPlayerEntity.playerContainer.sendContentUpdates();
            }
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.give.success.single", i, itemStackArgument.createStack(i, false).toTextComponent(), collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.give.success.single", i, itemStackArgument.createStack(i, false).toTextComponent(), collection.size()), true);
        }
        return collection.size();
    }
}

