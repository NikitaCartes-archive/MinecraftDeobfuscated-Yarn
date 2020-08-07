package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class StopSoundS2CPacket implements Packet<ClientPlayPacketListener> {
	private Identifier soundId;
	private SoundCategory category;

	public StopSoundS2CPacket() {
	}

	public StopSoundS2CPacket(@Nullable Identifier soundId, @Nullable SoundCategory category) {
		this.soundId = soundId;
		this.category = category;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int i = buf.readByte();
		if ((i & 1) > 0) {
			this.category = buf.readEnumConstant(SoundCategory.class);
		}

		if ((i & 2) > 0) {
			this.soundId = buf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		if (this.category != null) {
			if (this.soundId != null) {
				buf.writeByte(3);
				buf.writeEnumConstant(this.category);
				buf.writeIdentifier(this.soundId);
			} else {
				buf.writeByte(1);
				buf.writeEnumConstant(this.category);
			}
		} else if (this.soundId != null) {
			buf.writeByte(2);
			buf.writeIdentifier(this.soundId);
		} else {
			buf.writeByte(0);
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
		clientPlayPacketListener.onStopSound(this);
	}
}
