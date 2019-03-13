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
	private SoundEvent field_12661;
	private SoundCategory field_12660;
	private int fixedX;
	private int fixedY;
	private int fixedZ;
	private float volume;
	private float pitch;

	public PlaySoundS2CPacket() {
	}

	public PlaySoundS2CPacket(SoundEvent soundEvent, SoundCategory soundCategory, double d, double e, double f, float g, float h) {
		Validate.notNull(soundEvent, "sound");
		this.field_12661 = soundEvent;
		this.field_12660 = soundCategory;
		this.fixedX = (int)(d * 8.0);
		this.fixedY = (int)(e * 8.0);
		this.fixedZ = (int)(f * 8.0);
		this.volume = g;
		this.pitch = h;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12661 = Registry.SOUND_EVENT.get(packetByteBuf.readVarInt());
		this.field_12660 = packetByteBuf.readEnumConstant(SoundCategory.class);
		this.fixedX = packetByteBuf.readInt();
		this.fixedY = packetByteBuf.readInt();
		this.fixedZ = packetByteBuf.readInt();
		this.volume = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.field_12661));
		packetByteBuf.writeEnumConstant(this.field_12660);
		packetByteBuf.writeInt(this.fixedX);
		packetByteBuf.writeInt(this.fixedY);
		packetByteBuf.writeInt(this.fixedZ);
		packetByteBuf.writeFloat(this.volume);
		packetByteBuf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent method_11894() {
		return this.field_12661;
	}

	@Environment(EnvType.CLIENT)
	public SoundCategory method_11888() {
		return this.field_12660;
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

	public void method_11895(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11146(this);
	}
}
