package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityVelocityUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int velocityX;
	private int velocityY;
	private int velocityZ;

	public EntityVelocityUpdateS2CPacket() {
	}

	public EntityVelocityUpdateS2CPacket(Entity entity) {
		this(entity.getEntityId(), entity.method_18798());
	}

	public EntityVelocityUpdateS2CPacket(int i, Vec3d vec3d) {
		this.id = i;
		double d = 3.9;
		double e = MathHelper.clamp(vec3d.x, -3.9, 3.9);
		double f = MathHelper.clamp(vec3d.y, -3.9, 3.9);
		double g = MathHelper.clamp(vec3d.z, -3.9, 3.9);
		this.velocityX = (int)(e * 8000.0);
		this.velocityY = (int)(f * 8000.0);
		this.velocityZ = (int)(g * 8000.0);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.velocityX = packetByteBuf.readShort();
		this.velocityY = packetByteBuf.readShort();
		this.velocityZ = packetByteBuf.readShort();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeShort(this.velocityX);
		packetByteBuf.writeShort(this.velocityY);
		packetByteBuf.writeShort(this.velocityZ);
	}

	public void method_11817(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11132(this);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public int getVelocityX() {
		return this.velocityX;
	}

	@Environment(EnvType.CLIENT)
	public int getVelocityY() {
		return this.velocityY;
	}

	@Environment(EnvType.CLIENT)
	public int getVelocityZ() {
		return this.velocityZ;
	}
}
