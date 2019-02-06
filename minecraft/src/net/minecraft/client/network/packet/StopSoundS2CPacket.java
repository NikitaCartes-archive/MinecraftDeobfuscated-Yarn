package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class StopSoundS2CPacket implements Packet<ClientPlayPacketListener> {
	private Identifier soundId;
	private SoundCategory category;

	public StopSoundS2CPacket() {
	}

	public StopSoundS2CPacket(@Nullable Identifier identifier, @Nullable SoundCategory soundCategory) {
		this.soundId = identifier;
		this.category = soundCategory;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		int i = packetByteBuf.readByte();
		if ((i & 1) > 0) {
			this.category = packetByteBuf.readEnumConstant(SoundCategory.class);
		}

		if ((i & 2) > 0) {
			this.soundId = packetByteBuf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		if (this.category != null) {
			if (this.soundId != null) {
				packetByteBuf.writeByte(3);
				packetByteBuf.writeEnumConstant(this.category);
				packetByteBuf.writeIdentifier(this.soundId);
			} else {
				packetByteBuf.writeByte(1);
				packetByteBuf.writeEnumConstant(this.category);
			}
		} else if (this.soundId != null) {
			packetByteBuf.writeByte(2);
			packetByteBuf.writeIdentifier(this.soundId);
		} else {
			packetByteBuf.writeByte(0);
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Identifier getSoundId() {
		return this.soundId;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public SoundCategory getCategory() {
		return this.category;
	}

	public void method_11905(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11082(this);
	}
}
