package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.PacketByteBuf;

public class ClientSettingsC2SPacket implements Packet<ServerPlayPacketListener> {
	private String language;
	private int viewDistance;
	private ChatVisibility chatVisibility;
	private boolean field_12779;
	private int playerModelBitMask;
	private AbsoluteHand mainHand;

	public ClientSettingsC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public ClientSettingsC2SPacket(String string, int i, ChatVisibility chatVisibility, boolean bl, int j, AbsoluteHand absoluteHand) {
		this.language = string;
		this.viewDistance = i;
		this.chatVisibility = chatVisibility;
		this.field_12779 = bl;
		this.playerModelBitMask = j;
		this.mainHand = absoluteHand;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.language = packetByteBuf.readString(16);
		this.viewDistance = packetByteBuf.readByte();
		this.chatVisibility = packetByteBuf.readEnumConstant(ChatVisibility.class);
		this.field_12779 = packetByteBuf.readBoolean();
		this.playerModelBitMask = packetByteBuf.readUnsignedByte();
		this.mainHand = packetByteBuf.readEnumConstant(AbsoluteHand.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.language);
		packetByteBuf.writeByte(this.viewDistance);
		packetByteBuf.writeEnumConstant(this.chatVisibility);
		packetByteBuf.writeBoolean(this.field_12779);
		packetByteBuf.writeByte(this.playerModelBitMask);
		packetByteBuf.writeEnumConstant(this.mainHand);
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

	public boolean method_12135() {
		return this.field_12779;
	}

	public int getPlayerModelBitMask() {
		return this.playerModelBitMask;
	}

	public AbsoluteHand getMainHand() {
		return this.mainHand;
	}
}
