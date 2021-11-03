package net.minecraft.network.packet.c2s.play;

import java.lang.runtime.ObjectMethods;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Arm;

public final class ClientSettingsC2SPacket extends Record implements Packet<ServerPlayPacketListener> {
	private final String language;
	private final int viewDistance;
	private final ChatVisibility chatVisibility;
	private final boolean chatColors;
	private final int playerModelBitMask;
	private final Arm mainArm;
	private final boolean filterText;
	private final boolean allowsListing;
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

	public ClientSettingsC2SPacket(
		String language, int viewDistance, ChatVisibility chatVisibility, boolean chatColors, int modelBitMask, Arm mainArm, boolean filterText, boolean bl
	) {
		this.language = language;
		this.viewDistance = viewDistance;
		this.chatVisibility = chatVisibility;
		this.chatColors = chatColors;
		this.playerModelBitMask = modelBitMask;
		this.mainArm = mainArm;
		this.filterText = filterText;
		this.allowsListing = bl;
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",ClientSettingsC2SPacket,"language;viewDistance;chatVisibility;chatColors;modelCustomisation;mainHand;textFilteringEnabled;allowsListing",ClientSettingsC2SPacket::language,ClientSettingsC2SPacket::viewDistance,ClientSettingsC2SPacket::chatVisibility,ClientSettingsC2SPacket::chatColors,ClientSettingsC2SPacket::playerModelBitMask,ClientSettingsC2SPacket::mainArm,ClientSettingsC2SPacket::filterText,ClientSettingsC2SPacket::allowsListing>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",ClientSettingsC2SPacket,"language;viewDistance;chatVisibility;chatColors;modelCustomisation;mainHand;textFilteringEnabled;allowsListing",ClientSettingsC2SPacket::language,ClientSettingsC2SPacket::viewDistance,ClientSettingsC2SPacket::chatVisibility,ClientSettingsC2SPacket::chatColors,ClientSettingsC2SPacket::playerModelBitMask,ClientSettingsC2SPacket::mainArm,ClientSettingsC2SPacket::filterText,ClientSettingsC2SPacket::allowsListing>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",ClientSettingsC2SPacket,"language;viewDistance;chatVisibility;chatColors;modelCustomisation;mainHand;textFilteringEnabled;allowsListing",ClientSettingsC2SPacket::language,ClientSettingsC2SPacket::viewDistance,ClientSettingsC2SPacket::chatVisibility,ClientSettingsC2SPacket::chatColors,ClientSettingsC2SPacket::playerModelBitMask,ClientSettingsC2SPacket::mainArm,ClientSettingsC2SPacket::filterText,ClientSettingsC2SPacket::allowsListing>(
			this, object
		);
	}

	public String language() {
		return this.language;
	}

	public int viewDistance() {
		return this.viewDistance;
	}

	public ChatVisibility chatVisibility() {
		return this.chatVisibility;
	}

	public boolean chatColors() {
		return this.chatColors;
	}

	public int playerModelBitMask() {
		return this.playerModelBitMask;
	}

	public Arm mainArm() {
		return this.mainArm;
	}

	public boolean filterText() {
		return this.filterText;
	}

	public boolean allowsListing() {
		return this.allowsListing;
	}
}
