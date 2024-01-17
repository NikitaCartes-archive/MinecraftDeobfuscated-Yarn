package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.Identifier;

public class SelectAdvancementTabS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, SelectAdvancementTabS2CPacket> CODEC = Packet.createCodec(
		SelectAdvancementTabS2CPacket::write, SelectAdvancementTabS2CPacket::new
	);
	@Nullable
	private final Identifier tabId;

	public SelectAdvancementTabS2CPacket(@Nullable Identifier tabId) {
		this.tabId = tabId;
	}

	private SelectAdvancementTabS2CPacket(PacketByteBuf buf) {
		this.tabId = buf.readNullable(PacketByteBuf::readIdentifier);
	}

	private void write(PacketByteBuf buf) {
		buf.writeNullable(this.tabId, PacketByteBuf::writeIdentifier);
	}

	@Override
	public PacketIdentifier<SelectAdvancementTabS2CPacket> getPacketId() {
		return PlayPackets.SELECT_ADVANCEMENTS_TAB;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSelectAdvancementTab(this);
	}

	@Nullable
	public Identifier getTabId() {
		return this.tabId;
	}
}
