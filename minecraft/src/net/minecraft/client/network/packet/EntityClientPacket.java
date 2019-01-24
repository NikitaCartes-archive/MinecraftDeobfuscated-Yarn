package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class EntityClientPacket implements Packet<ClientPlayPacketListener> {
	protected int id;
	protected int deltaX;
	protected int deltaY;
	protected int deltaZ;
	protected byte yaw;
	protected byte pitch;
	protected boolean onGround;
	protected boolean rotate;

	public EntityClientPacket() {
	}

	public EntityClientPacket(int i) {
		this.id = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
	}

	public void method_11651(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityUpdate(this);
	}

	public String toString() {
		return "Entity_" + super.toString();
	}

	@Environment(EnvType.CLIENT)
	public Entity getEntity(World world) {
		return world.getEntityById(this.id);
	}

	@Environment(EnvType.CLIENT)
	public int getDeltaXShort() {
		return this.deltaX;
	}

	@Environment(EnvType.CLIENT)
	public int getDeltaYShort() {
		return this.deltaY;
	}

	@Environment(EnvType.CLIENT)
	public int getDeltaZShort() {
		return this.deltaZ;
	}

	@Environment(EnvType.CLIENT)
	public byte getYaw() {
		return this.yaw;
	}

	@Environment(EnvType.CLIENT)
	public byte getPitch() {
		return this.pitch;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasRotation() {
		return this.rotate;
	}

	@Environment(EnvType.CLIENT)
	public boolean isOnGround() {
		return this.onGround;
	}

	public static class MoveRelative extends EntityClientPacket {
		public MoveRelative() {
		}

		public MoveRelative(int i, long l, long m, long n, boolean bl) {
			super(i);
			this.deltaX = (int)l;
			this.deltaY = (int)m;
			this.deltaZ = (int)n;
			this.onGround = bl;
		}

		@Override
		public void read(PacketByteBuf packetByteBuf) throws IOException {
			super.read(packetByteBuf);
			this.deltaX = packetByteBuf.readShort();
			this.deltaY = packetByteBuf.readShort();
			this.deltaZ = packetByteBuf.readShort();
			this.onGround = packetByteBuf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf packetByteBuf) throws IOException {
			super.write(packetByteBuf);
			packetByteBuf.writeShort(this.deltaX);
			packetByteBuf.writeShort(this.deltaY);
			packetByteBuf.writeShort(this.deltaZ);
			packetByteBuf.writeBoolean(this.onGround);
		}
	}

	public static class Rotate extends EntityClientPacket {
		public Rotate() {
			this.rotate = true;
		}

		public Rotate(int i, byte b, byte c, boolean bl) {
			super(i);
			this.yaw = b;
			this.pitch = c;
			this.rotate = true;
			this.onGround = bl;
		}

		@Override
		public void read(PacketByteBuf packetByteBuf) throws IOException {
			super.read(packetByteBuf);
			this.yaw = packetByteBuf.readByte();
			this.pitch = packetByteBuf.readByte();
			this.onGround = packetByteBuf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf packetByteBuf) throws IOException {
			super.write(packetByteBuf);
			packetByteBuf.writeByte(this.yaw);
			packetByteBuf.writeByte(this.pitch);
			packetByteBuf.writeBoolean(this.onGround);
		}
	}

	public static class RotateAndMoveRelative extends EntityClientPacket {
		public RotateAndMoveRelative() {
			this.rotate = true;
		}

		public RotateAndMoveRelative(int i, long l, long m, long n, byte b, byte c, boolean bl) {
			super(i);
			this.deltaX = (int)l;
			this.deltaY = (int)m;
			this.deltaZ = (int)n;
			this.yaw = b;
			this.pitch = c;
			this.onGround = bl;
			this.rotate = true;
		}

		@Override
		public void read(PacketByteBuf packetByteBuf) throws IOException {
			super.read(packetByteBuf);
			this.deltaX = packetByteBuf.readShort();
			this.deltaY = packetByteBuf.readShort();
			this.deltaZ = packetByteBuf.readShort();
			this.yaw = packetByteBuf.readByte();
			this.pitch = packetByteBuf.readByte();
			this.onGround = packetByteBuf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf packetByteBuf) throws IOException {
			super.write(packetByteBuf);
			packetByteBuf.writeShort(this.deltaX);
			packetByteBuf.writeShort(this.deltaY);
			packetByteBuf.writeShort(this.deltaZ);
			packetByteBuf.writeByte(this.yaw);
			packetByteBuf.writeByte(this.pitch);
			packetByteBuf.writeBoolean(this.onGround);
		}
	}
}
