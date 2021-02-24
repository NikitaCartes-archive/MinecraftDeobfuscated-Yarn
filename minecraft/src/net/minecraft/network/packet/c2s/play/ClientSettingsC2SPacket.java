package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Arm;

public class ClientSettingsC2SPacket implements Packet<ServerPlayPacketListener> {
	private final String language;
	private final int viewDistance;
	private final ChatVisibility chatVisibility;
	private final boolean chatColors;
	private final int playerModelBitMask;
	private final Arm mainArm;
	private final boolean field_28961;

	@Environment(EnvType.CLIENT)
	public ClientSettingsC2SPacket(String language, int viewDistance, ChatVisibility chatVisibility, boolean chatColors, int modelBitMask, Arm mainArm, boolean bl) {
		this.language = language;
		this.viewDistance = viewDistance;
		this.chatVisibility = chatVisibility;
		this.chatColors = chatColors;
		this.playerModelBitMask = modelBitMask;
		this.mainArm = mainArm;
		this.field_28961 = bl;
	}

	public ClientSettingsC2SPacket(PacketByteBuf packetByteBuf) {
		this.language = packetByteBuf.readString(16);
		this.viewDistance = packetByteBuf.readByte();
		this.chatVisibility = packetByteBuf.readEnumConstant(ChatVisibility.class);
		this.chatColors = packetByteBuf.readBoolean();
		this.playerModelBitMask = packetByteBuf.readUnsignedByte();
		this.mainArm = packetByteBuf.readEnumConstant(Arm.class);
		this.field_28961 = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.language);
		buf.writeByte(this.viewDistance);
		buf.writeEnumConstant(this.chatVisibility);
		buf.writeBoolean(this.chatColors);
		buf.writeByte(this.playerModelBitMask);
		buf.writeEnumConstant(this.mainArm);
		buf.writeBoolean(this.field_28961);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientSettings(this);
	}

	public ChatVisibility getChatVisibility() {
		return this.chatVisibility;
	}

	public boolean hasChatColors() {
		return this.chatColors;
	}

	public int getPlayerModelBitMask() {
		return this.playerModelBitMask;
	}

	public Arm getMainArm() {
		return this.mainArm;
	}

	public boolean method_33894() {
		return this.field_28961;
	}
}
