package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class TagQueryResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int transactionId;
	@Nullable
	private final CompoundTag tag;

	public TagQueryResponseS2CPacket(int transactionId, @Nullable CompoundTag tag) {
		this.transactionId = transactionId;
		this.tag = tag;
	}

	public TagQueryResponseS2CPacket(PacketByteBuf buf) {
		this.transactionId = buf.readVarInt();
		this.tag = buf.readCompoundTag();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.transactionId);
		buf.writeCompoundTag(this.tag);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTagQuery(this);
	}

	@Environment(EnvType.CLIENT)
	public int getTransactionId() {
		return this.transactionId;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public CompoundTag getTag() {
		return this.tag;
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
