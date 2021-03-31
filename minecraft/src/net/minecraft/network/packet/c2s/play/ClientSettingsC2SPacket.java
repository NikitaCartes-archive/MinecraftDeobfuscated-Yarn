package net.minecraft.network.packet.c2s.play;

import net.minecraft.client.option.ChatVisibility;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Arm;

public class ClientSettingsC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final int MAX_LANGUAGE_LENGTH = 16;
	private final String language;
	private final int viewDistance;
	private final ChatVisibility chatVisibility;
	private final boolean chatColors;
	private final int playerModelBitMask;
	private final Arm mainArm;
	private final boolean filterText;

	public ClientSettingsC2SPacket(
		String language, int viewDistance, ChatVisibility chatVisibility, boolean chatColors, int modelBitMask, Arm mainArm, boolean filterText
	) {
		this.language = language;
		this.viewDistance = viewDistance;
		this.chatVisibility = chatVisibility;
		this.chatColors = chatColors;
		this.playerModelBitMask = modelBitMask;
		this.mainArm = mainArm;
		this.filterText = filterText;
	}

	public ClientSettingsC2SPacket(PacketByteBuf buf) {
		this.language = buf.readString(16);
		this.viewDistance = buf.readByte();
		this.chatVisibility = buf.readEnumConstant(ChatVisibility.class);
		this.chatColors = buf.readBoolean();
		this.playerModelBitMask = buf.readUnsignedByte();
		this.mainArm = buf.readEnumConstant(Arm.class);
		this.filterText = buf.readBoolean();
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
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientSettings(this);
	}

	public String getLanguage() {
		return this.language;
	}

	public int getViewDistance() {
		return this.viewDistance;
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

	public boolean shouldFilterText() {
		return this.filterText;
	}
}
