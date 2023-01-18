package net.minecraft.network.packet.c2s.play;

import net.minecraft.client.option.ChatVisibility;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Arm;

public record ClientSettingsC2SPacket(
	String language,
	int viewDistance,
	ChatVisibility chatVisibility,
	boolean chatColors,
	int playerModelBitMask,
	Arm mainArm,
	boolean filterText,
	boolean allowsListing
) implements Packet<ServerPlayPacketListener> {
	public static final int MAX_LANGUAGE_LENGTH = 16;

	public ClientSettingsC2SPacket(PacketByteBuf buf) {
		this(
			buf.readString(16),
			buf.readByte(),
			buf.readEnumConstant(ChatVisibility.class),
			buf.readBoolean(),
			buf.readUnsignedByte(),
			buf.readEnumConstant(Arm.class),
			buf.readBoolean(),
			buf.readBoolean()
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.language);
		buf.writeByte(this.viewDistance);
		buf.writeEnumConstant(this.chatVisibility);
		buf.writeBoolean(this.chatColors);
		buf.writeByte(this.playerModelBitMask);
		buf.writeEnumConstant(this.mainArm);
		buf.writeBoolean(this.filterText);
		buf.writeBoolean(this.allowsListing);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientSettings(this);
	}
}
