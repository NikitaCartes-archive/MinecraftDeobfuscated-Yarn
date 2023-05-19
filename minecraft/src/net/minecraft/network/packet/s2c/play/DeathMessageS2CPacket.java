package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public class DeathMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int entityId;
	private final Text message;

	public DeathMessageS2CPacket(int entityId, Text message) {
		this.entityId = entityId;
		this.message = message;
	}

	public DeathMessageS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.message = buf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeText(this.message);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onDeathMessage(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public Text getMessage() {
		return this.message;
	}
}
