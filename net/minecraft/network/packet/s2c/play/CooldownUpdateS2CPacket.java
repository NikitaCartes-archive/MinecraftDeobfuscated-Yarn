/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class CooldownUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final Item item;
    private final int cooldown;

    public CooldownUpdateS2CPacket(Item item, int cooldown) {
        this.item = item;
        this.cooldown = cooldown;
    }

    public CooldownUpdateS2CPacket(PacketByteBuf buf) {
        this.item = Item.byRawId(buf.readVarInt());
        this.cooldown = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(Item.getRawId(this.item));
        buf.writeVarInt(this.cooldown);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onCooldownUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Item getItem() {
        return this.item;
    }

    @Environment(value=EnvType.CLIENT)
    public int getCooldown() {
        return this.cooldown;
    }
}

