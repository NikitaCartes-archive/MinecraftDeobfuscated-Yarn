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

public class MapItem
extends Item {
    public MapItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isMap() {
        return true;
    }

    @Nullable
    public Packet<?> createMapPacket(ItemStack itemStack, World world, PlayerEntity playerEntity) {
        return null;
    }
}

