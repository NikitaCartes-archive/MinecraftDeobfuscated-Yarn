package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class PlaySoundIdS2CPacket implements Packet<ClientPlayPacketListener> {
	private Identifier field_12170;
	private SoundCategory field_12171;
	private int fixedX;
	private int fixedY = Integer.MAX_VALUE;
	private int fixedZ;
	private float volume;
	private float pitch;

	public PlaySoundIdS2CPacket() {
	}

	public PlaySoundIdS2CPacket(Identifier identifier, SoundCategory soundCategory, Vec3d vec3d, float f, float g) {
		this.field_12170 = identifier;
		this.field_12171 = soundCategory;
		this.fixedX = (int)(vec3d.x * 8.0);
		this.fixedY = (int)(vec3d.y * 8.0);
		this.fixedZ = (int)(vec3d.z * 8.0);
		this.volume = f;
		this.pitch = g;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12170 = packetByteBuf.method_10810();
		this.field_12171 = packetByteBuf.readEnumConstant(SoundCategory.class);
		this.fixedX = packetByteBuf.readInt();
		this.fixedY = packetByteBuf.readInt();
		this.fixedZ = packetByteBuf.readInt();
		this.volume = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.method_10812(this.field_12170);
		packetByteBuf.writeEnumConstant(this.field_12171);
		packetByteBuf.writeInt(this.fixedX);
		packetByteBuf.writeInt(this.fixedY);
		packetByteBuf.writeInt(this.fixedZ);
		packetByteBuf.writeFloat(this.volume);
		packetByteBuf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public Identifier method_11460() {
		return this.field_12170;
	}

	@Environment(EnvType.CLIENT)
	public SoundCategory method_11459() {
		return this.field_12171;
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

	public void method_11466(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11104(this);
	}
}
