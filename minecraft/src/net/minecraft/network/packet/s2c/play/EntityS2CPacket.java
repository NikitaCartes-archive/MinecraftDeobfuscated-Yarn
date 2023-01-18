package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;

public abstract class EntityS2CPacket implements Packet<ClientPlayPacketListener> {
	protected final int id;
	protected final short deltaX;
	protected final short deltaY;
	protected final short deltaZ;
	protected final byte yaw;
	protected final byte pitch;
	protected final boolean onGround;
	protected final boolean rotate;
	protected final boolean positionChanged;

	protected EntityS2CPacket(
		int entityId, short deltaX, short deltaY, short deltaZ, byte yaw, byte pitch, boolean onGround, boolean rotate, boolean positionChanged
	) {
		this.id = entityId;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.deltaZ = deltaZ;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
		this.rotate = rotate;
		this.positionChanged = positionChanged;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntity(this);
	}

	public String toString() {
		return "Entity_" + super.toString();
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(this.id);
	}

	public short getDeltaX() {
		return this.deltaX;
	}

	public short getDeltaY() {
		return this.deltaY;
	}

	public short getDeltaZ() {
		return this.deltaZ;
	}

	public byte getYaw() {
		return this.yaw;
	}

	public byte getPitch() {
		return this.pitch;
	}

	public boolean hasRotation() {
		return this.rotate;
	}

	public boolean isPositionChanged() {
		return this.positionChanged;
	}

	public boolean isOnGround() {
		return this.onGround;
	}

	public static class MoveRelative extends EntityS2CPacket {
		public MoveRelative(int entityId, short deltaX, short deltaY, short deltaZ, boolean onGround) {
			super(entityId, deltaX, deltaY, deltaZ, (byte)0, (byte)0, onGround, false, true);
		}

		public static EntityS2CPacket.MoveRelative read(PacketByteBuf buf) {
			int i = buf.readVarInt();
			short s = buf.readShort();
			short t = buf.readShort();
			short u = buf.readShort();
			boolean bl = buf.readBoolean();
			return new EntityS2CPacket.MoveRelative(i, s, t, u, bl);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeVarInt(this.id);
			buf.writeShort(this.deltaX);
			buf.writeShort(this.deltaY);
			buf.writeShort(this.deltaZ);
			buf.writeBoolean(this.onGround);
		}
	}

	public static class Rotate extends EntityS2CPacket {
		public Rotate(int entityId, byte yaw, byte pitch, boolean onGround) {
			super(entityId, (short)0, (short)0, (short)0, yaw, pitch, onGround, true, false);
		}

		public static EntityS2CPacket.Rotate read(PacketByteBuf buf) {
			int i = buf.readVarInt();
			byte b = buf.readByte();
			byte c = buf.readByte();
			boolean bl = buf.readBoolean();
			return new EntityS2CPacket.Rotate(i, b, c, bl);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeVarInt(this.id);
			buf.writeByte(this.yaw);
			buf.writeByte(this.pitch);
			buf.writeBoolean(this.onGround);
		}
	}

	public static class RotateAndMoveRelative extends EntityS2CPacket {
		public RotateAndMoveRelative(int entityId, short deltaX, short deltaY, short deltaZ, byte yaw, byte pitch, boolean onGround) {
			super(entityId, deltaX, deltaY, deltaZ, yaw, pitch, onGround, true, true);
		}

		public static EntityS2CPacket.RotateAndMoveRelative read(PacketByteBuf buf) {
			int i = buf.readVarInt();
			short s = buf.readShort();
			short t = buf.readShort();
			short u = buf.readShort();
			byte b = buf.readByte();
			byte c = buf.readByte();
			boolean bl = buf.readBoolean();
			return new EntityS2CPacket.RotateAndMoveRelative(i, s, t, u, b, c, bl);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeVarInt(this.id);
			buf.writeShort(this.deltaX);
			buf.writeShort(this.deltaY);
			buf.writeShort(this.deltaZ);
			buf.writeByte(this.yaw);
			buf.writeByte(this.pitch);
			buf.writeBoolean(this.onGround);
		}
	}
}
