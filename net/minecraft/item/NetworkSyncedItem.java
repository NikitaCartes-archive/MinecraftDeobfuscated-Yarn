/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NetworkSyncedItem
extends Item {
    public NetworkSyncedItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isNetworkSynced() {
        return true;
    }

    @Nullable
    public Packet<?> createSyncPacket(ItemStack itemStack, World world, PlayerEntity playerEntity) {
        return null;
    }
}

