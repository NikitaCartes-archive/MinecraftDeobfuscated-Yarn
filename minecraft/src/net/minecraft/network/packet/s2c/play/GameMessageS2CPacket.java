package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;

public record GameMessageS2CPacket(Text content, boolean overlay) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, GameMessageS2CPacket> CODEC = Packet.createCodec(GameMessageS2CPacket::write, GameMessageS2CPacket::new);

	private GameMessageS2CPacket(PacketByteBuf buf) {
		this(buf.readUnlimitedText(), buf.readBoolean());
	}

	private void write(PacketByteBuf buf) {
		buf.writeText(this.content);
		buf.writeBoolean(this.overlay);
	}

	@Override
	public PacketType<GameMessageS2CPacket> getPacketId() {
		return PlayPackets.SYSTEM_CHAT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameMessage(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
