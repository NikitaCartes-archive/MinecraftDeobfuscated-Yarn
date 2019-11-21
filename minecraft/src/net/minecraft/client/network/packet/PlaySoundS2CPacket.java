package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public class PlaySoundS2CPacket implements Packet<ClientPlayPacketListener> {
	private SoundEvent sound;
	private SoundCategory category;
	private int fixedX;
	private int fixedY;
	private int fixedZ;
	private float volume;
	private float pitch;

	public PlaySoundS2CPacket() {
	}

	public PlaySoundS2CPacket(SoundEvent sound, SoundCategory category, double x, double y, double z, float volume, float pitch) {
		Validate.notNull(sound, "sound");
		this.sound = sound;
		this.category = category;
		this.fixedX = (int)(x * 8.0);
		this.fixedY = (int)(y * 8.0);
		this.fixedZ = (int)(z * 8.0);
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.sound = Registry.SOUND_EVENT.get(buf.readVarInt());
		this.category = buf.readEnumConstant(SoundCategory.class);
		this.fixedX = buf.readInt();
		this.fixedY = buf.readInt();
		this.fixedZ = buf.readInt();
		this.volume = buf.readFloat();
		this.pitch = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.sound));
		buf.writeEnumConstant(this.category);
		buf.writeInt(this.fixedX);
		buf.writeInt(this.fixedY);
		buf.writeInt(this.fixedZ);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent getSound() {
		return this.sound;
	}

	@Environment(EnvType.CLIENT)
	public SoundCategory getCategory() {
		return this.category;
	}

	@Environment(EnvType.CLIENT)
	public double getX() {
		return (double)((float)this.fixedX / 8.0F);
	}

	@Environment(EnvType.CLIENT)
	public double getY() {
		return (double)((float)this.fixedY / 8.0F);
	}

	@Environment(EnvType.CLIENT)
	public double getZ() {
		return (double)((float)this.fixedZ / 8.0F);
	}

	@Environment(EnvType.CLIENT)
	public float getVolume() {
		return this.volume;
	}

	@Environment(EnvType.CLIENT)
	public float getPitch() {
		return this.pitch;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlaySound(this);
	}
}
