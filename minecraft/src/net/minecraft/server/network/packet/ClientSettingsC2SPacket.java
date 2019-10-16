package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Arm;
import net.minecraft.util.PacketByteBuf;

public class ClientSettingsC2SPacket implements Packet<ServerPlayPacketListener> {
	private String language;
	private int viewDistance;
	private ChatVisibility chatVisibility;
	private boolean chatColors;
	private int playerModelBitMask;
	private Arm mainArm;

	public ClientSettingsC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public ClientSettingsC2SPacket(String string, int i, ChatVisibility chatVisibility, boolean bl, int j, Arm arm) {
		this.language = string;
		this.viewDistance = i;
		this.chatVisibility = chatVisibility;
		this.chatColors = bl;
		this.playerModelBitMask = j;
		this.mainArm = arm;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.language = packetByteBuf.readString(16);
		this.viewDistance = packetByteBuf.readByte();
		this.chatVisibility = packetByteBuf.readEnumConstant(ChatVisibility.class);
		this.chatColors = packetByteBuf.readBoolean();
		this.playerModelBitMask = packetByteBuf.readUnsignedByte();
		this.mainArm = packetByteBuf.readEnumConstant(Arm.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.language);
		packetByteBuf.writeByte(this.viewDistance);
		packetByteBuf.writeEnumConstant(this.chatVisibility);
		packetByteBuf.writeBoolean(this.chatColors);
		packetByteBuf.writeByte(this.playerModelBitMask);
		packetByteBuf.writeEnumConstant(this.mainArm);
	}

	public void method_12133(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientSettings(this);
	}

	public String getLanguage() {
		return this.language;
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
}
