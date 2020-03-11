package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
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
		this(entity.getEntityId(), entity.getVelocity());
	}

	public EntityVelocityUpdateS2CPacket(int id, Vec3d vec3d) {
		this.id = id;
		double d = 3.9;
		double e = MathHelper.clamp(vec3d.x, -3.9, 3.9);
		double f = MathHelper.clamp(vec3d.y, -3.9, 3.9);
		double g = MathHelper.clamp(vec3d.z, -3.9, 3.9);
		this.velocityX = (int)(e * 8000.0);
		this.velocityY = (int)(f * 8000.0);
		this.velocityZ = (int)(g * 8000.0);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.velocityX = buf.readShort();
		this.velocityY = buf.readShort();
		this.velocityZ = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeShort(this.velocityX);
		buf.writeShort(this.velocityY);
		buf.writeShort(this.velocityZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onVelocityUpdate(this);
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
