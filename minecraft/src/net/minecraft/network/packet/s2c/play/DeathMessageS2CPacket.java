package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;

public class DeathMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, DeathMessageS2CPacket> CODEC = Packet.createCodec(DeathMessageS2CPacket::write, DeathMessageS2CPacket::new);
	private final int entityId;
	private final Text message;

	public DeathMessageS2CPacket(int entityId, Text message) {
		this.entityId = entityId;
		this.message = message;
	}

	private DeathMessageS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.message = buf.readUnlimitedText();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeText(this.message);
	}

	@Override
	public PacketIdentifier<DeathMessageS2CPacket> getPacketId() {
		return PlayPackets.PLAYER_COMBAT_KILL;
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
