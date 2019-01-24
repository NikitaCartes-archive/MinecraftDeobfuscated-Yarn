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

public class PlaySoundIdClientPacket implements Packet<ClientPlayPacketListener> {
	private Identifier id;
	private SoundCategory category;
	private int fixedX;
	private int fixedY = Integer.MAX_VALUE;
	private int fixedZ;
	private float volume;
	private float pitch;

	public PlaySoundIdClientPacket() {
	}

	public PlaySoundIdClientPacket(Identifier identifier, SoundCategory soundCategory, Vec3d vec3d, float f, float g) {
		this.id = identifier;
		this.category = soundCategory;
		this.fixedX = (int)(vec3d.x * 8.0);
		this.fixedY = (int)(vec3d.y * 8.0);
		this.fixedZ = (int)(vec3d.z * 8.0);
		this.volume = f;
		this.pitch = g;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readIdentifier();
		this.category = packetByteBuf.readEnumConstant(SoundCategory.class);
		this.fixedX = packetByteBuf.readInt();
		this.fixedY = packetByteBuf.readInt();
		this.fixedZ = packetByteBuf.readInt();
		this.volume = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeIdentifier(this.id);
		packetByteBuf.writeEnumConstant(this.category);
		packetByteBuf.writeInt(this.fixedX);
		packetByteBuf.writeInt(this.fixedY);
		packetByteBuf.writeInt(this.fixedZ);
		packetByteBuf.writeFloat(this.volume);
		packetByteBuf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public Identifier getSoundId() {
		return this.id;
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

	public void method_11466(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlaySoundId(this);
	}
}
