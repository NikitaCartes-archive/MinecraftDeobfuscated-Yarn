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
	private boolean field_21899;

	public ClientSettingsC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public ClientSettingsC2SPacket(String language, int viewDistance, ChatVisibility chatVisibility, boolean chatColors, int modelBitMask, Arm mainArm, boolean bl) {
		this.language = language;
		this.viewDistance = viewDistance;
		this.chatVisibility = chatVisibility;
		this.chatColors = chatColors;
		this.playerModelBitMask = modelBitMask;
		this.mainArm = mainArm;
		this.field_21899 = bl;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.language = buf.readString(16);
		this.viewDistance = buf.readByte();
		this.chatVisibility = buf.readEnumConstant(ChatVisibility.class);
		this.chatColors = buf.readBoolean();
		this.playerModelBitMask = buf.readUnsignedByte();
		this.mainArm = buf.readEnumConstant(Arm.class);
		this.field_21899 = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.language);
		buf.writeByte(this.viewDistance);
		buf.writeEnumConstant(this.chatVisibility);
		buf.writeBoolean(this.chatColors);
		buf.writeByte(this.playerModelBitMask);
		buf.writeEnumConstant(this.mainArm);
		buf.writeBoolean(this.field_21899);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
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

	public boolean method_24343() {
		return this.field_21899;
	}
}
