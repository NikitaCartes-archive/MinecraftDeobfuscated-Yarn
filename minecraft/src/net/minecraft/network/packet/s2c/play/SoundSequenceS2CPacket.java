package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class SoundSequenceS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, SoundSequenceS2CPacket> CODEC = Packet.createCodec(SoundSequenceS2CPacket::write, SoundSequenceS2CPacket::new);
	private final List<SoundSequenceS2CPacket.Sound> sounds;

	public SoundSequenceS2CPacket(List<SoundSequenceS2CPacket.Sound> sounds) {
		this.sounds = sounds;
	}

	private SoundSequenceS2CPacket(RegistryByteBuf buf) {
		int i = buf.readInt();
		Builder<SoundSequenceS2CPacket.Sound> builder = ImmutableList.builder();

		for (int j = 0; j < i; j++) {
			builder.add(new SoundSequenceS2CPacket.Sound(buf.readInt(), PlaySoundS2CPacket.CODEC.decode(buf)));
		}

		this.sounds = builder.build();
	}

	private void write(RegistryByteBuf buf) {
		buf.writeInt(this.sounds.size());

		for (SoundSequenceS2CPacket.Sound sound : this.sounds) {
			buf.writeInt(sound.ticks);
			PlaySoundS2CPacket.CODEC.encode(buf, sound.packet);
		}
	}

	@Override
	public PacketType<SoundSequenceS2CPacket> getPacketId() {
		return PlayPackets.SOUND_SEQUENCE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSoundSequence(this);
	}

	public List<SoundSequenceS2CPacket.Sound> getSounds() {
		return this.sounds;
	}

	public static record Sound(int ticks, PlaySoundS2CPacket packet) {
	}
}
