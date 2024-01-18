package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class StopSoundS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, StopSoundS2CPacket> CODEC = Packet.createCodec(StopSoundS2CPacket::write, StopSoundS2CPacket::new);
	private static final int CATEGORY_MASK = 1;
	private static final int SOUND_ID_MASK = 2;
	@Nullable
	private final Identifier soundId;
	@Nullable
	private final SoundCategory category;

	public StopSoundS2CPacket(@Nullable Identifier soundId, @Nullable SoundCategory category) {
		this.soundId = soundId;
		this.category = category;
	}

	private StopSoundS2CPacket(PacketByteBuf buf) {
		int i = buf.readByte();
		if ((i & 1) > 0) {
			this.category = buf.readEnumConstant(SoundCategory.class);
		} else {
			this.category = null;
		}

		if ((i & 2) > 0) {
			this.soundId = buf.readIdentifier();
		} else {
			this.soundId = null;
		}
	}

	private void write(PacketByteBuf buf) {
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

	@Override
	public PacketType<StopSoundS2CPacket> getPacketId() {
		return PlayPackets.STOP_SOUND;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStopSound(this);
	}

	@Nullable
	public Identifier getSoundId() {
		return this.soundId;
	}

	@Nullable
	public SoundCategory getCategory() {
		return this.category;
	}
}
