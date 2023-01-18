package net.minecraft.network.packet.s2c.play;

import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;

public class CooldownUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Item item;
	private final int cooldown;

	public CooldownUpdateS2CPacket(Item item, int cooldown) {
		this.item = item;
		this.cooldown = cooldown;
	}

	public CooldownUpdateS2CPacket(PacketByteBuf buf) {
		this.item = buf.readRegistryValue(Registries.ITEM);
		this.cooldown = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeRegistryValue(Registries.ITEM, this.item);
		buf.writeVarInt(this.cooldown);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCooldownUpdate(this);
	}

	public Item getItem() {
		return this.item;
	}

	public int getCooldown() {
		return this.cooldown;
	}
}
