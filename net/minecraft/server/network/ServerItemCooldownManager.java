/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network;

import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerItemCooldownManager
extends ItemCooldownManager {
    private final ServerPlayerEntity player;

    public ServerItemCooldownManager(ServerPlayerEntity serverPlayerEntity) {
        this.player = serverPlayerEntity;
    }

    @Override
    protected void onCooldownUpdate(Item item, int duration) {
        super.onCooldownUpdate(item, duration);
        this.player.networkHandler.sendPacket(new CooldownUpdateS2CPacket(item, duration));
    }

    @Override
    protected void onCooldownUpdate(Item item) {
        super.onCooldownUpdate(item);
        this.player.networkHandler.sendPacket(new CooldownUpdateS2CPacket(item, 0));
    }
}

