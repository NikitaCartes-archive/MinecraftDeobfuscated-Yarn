package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class OpenScreenS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, OpenScreenS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		OpenScreenS2CPacket::getSyncId,
		PacketCodecs.registryValue(RegistryKeys.SCREEN_HANDLER),
		OpenScreenS2CPacket::getScreenHandlerType,
		TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC,
		OpenScreenS2CPacket::getName,
		OpenScreenS2CPacket::new
	);
	private final int syncId;
	private final ScreenHandlerType<?> screenHandlerId;
	private final Text name;

	public OpenScreenS2CPacket(int syncId, ScreenHandlerType<?> screenHandlerId, Text name) {
		this.syncId = syncId;
		this.screenHandlerId = screenHandlerId;
		this.name = name;
	}

	@Override
	public PacketType<OpenScreenS2CPacket> getPacketId() {
		return PlayPackets.OPEN_SCREEN;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenScreen(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public ScreenHandlerType<?> getScreenHandlerType() {
		return this.screenHandlerId;
	}

	public Text getName() {
		return this.name;
	}
}
