package net.minecraft.network.packet.c2s.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.util.Arm;

public record SyncedClientOptions(
	String language,
	int viewDistance,
	ChatVisibility chatVisibility,
	boolean chatColorsEnabled,
	int playerModelParts,
	Arm mainArm,
	boolean filtersText,
	boolean allowsServerListing
) {
	public static final int MAX_LANGUAGE_CODE_LENGTH = 16;

	public SyncedClientOptions(PacketByteBuf buf) {
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

	public void write(PacketByteBuf buf) {
		buf.writeString(this.language);
		buf.writeByte(this.viewDistance);
		buf.writeEnumConstant(this.chatVisibility);
		buf.writeBoolean(this.chatColorsEnabled);
		buf.writeByte(this.playerModelParts);
		buf.writeEnumConstant(this.mainArm);
		buf.writeBoolean(this.filtersText);
		buf.writeBoolean(this.allowsServerListing);
	}

	public static SyncedClientOptions createDefault() {
		return new SyncedClientOptions("en_us", 2, ChatVisibility.FULL, true, 0, PlayerEntity.DEFAULT_MAIN_ARM, false, false);
	}
}
