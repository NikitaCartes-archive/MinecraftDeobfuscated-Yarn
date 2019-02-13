package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityVelocityUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int velocityX;
	private int velocityY;
	private int velocityZ;

	public EntityVelocityUpdateS2CPacket() {
	}

	public EntityVelocityUpdateS2CPacket(Entity entity) {
		this(entity.getEntityId(), entity.velocityX, entity.velocityY, entity.velocityZ);
	}

	public EntityVelocityUpdateS2CPacket(int i, double d, double e, double f) {
		this.id = i;
		double g = 3.9;
		if (d < -3.9) {
			d = -3.9;
		}

		if (e < -3.9) {
			e = -3.9;
		}

		if (f < -3.9) {
			f = -3.9;
		}

		if (d > 3.9) {
			d = 3.9;
		}

		if (e > 3.9) {
			e = 3.9;
		}

		if (f > 3.9) {
			f = 3.9;
		}

		this.velocityX = (int)(d * 8000.0);
		this.velocityY = (int)(e * 8000.0);
		this.velocityZ = (int)(f * 8000.0);
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
