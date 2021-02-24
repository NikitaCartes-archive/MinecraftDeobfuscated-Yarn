package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

	public static long encodePacketCoordinate(double coord) {
		return MathHelper.lfloor(coord * 4096.0);
	}

	@Environment(EnvType.CLIENT)
	public static double decodePacketCoordinate(long coord) {
		return (double)coord / 4096.0;
	}

	@Environment(EnvType.CLIENT)
	public Vec3d calculateDeltaPosition(Vec3d orig) {
		double d = this.deltaX == 0 ? orig.x : decodePacketCoordinate(encodePacketCoordinate(orig.x) + (long)this.deltaX);
		double e = this.deltaY == 0 ? orig.y : decodePacketCoordinate(encodePacketCoordinate(orig.y) + (long)this.deltaY);
		double f = this.deltaZ == 0 ? orig.z : decodePacketCoordinate(encodePacketCoordinate(orig.z) + (long)this.deltaZ);
		return new Vec3d(d, e, f);
	}

	public static Vec3d decodePacketCoordinates(long x, long y, long z) {
		return new Vec3d((double)x, (double)y, (double)z).multiply(2.4414062E-4F);
	}

	protected EntityS2CPacket(int entityId, short s, short t, short u, byte b, byte c, boolean bl, boolean bl2, boolean bl3) {
		this.id = entityId;
		this.deltaX = s;
		this.deltaY = t;
		this.deltaZ = u;
		this.yaw = b;
		this.pitch = c;
		this.onGround = bl;
		this.rotate = bl2;
		this.positionChanged = bl3;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityUpdate(this);
	}

	public String toString() {
		return "Entity_" + super.toString();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Entity getEntity(World world) {
		return world.getEntityById(this.id);
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
	public boolean isPositionChanged() {
		return this.positionChanged;
	}

	@Environment(EnvType.CLIENT)
	public boolean isOnGround() {
		return this.onGround;
	}

	public static class MoveRelative extends EntityS2CPacket {
		public MoveRelative(int entityId, short deltaX, short deltaY, short deltaZ, boolean onGround) {
			super(entityId, deltaX, deltaY, deltaZ, (byte)0, (byte)0, onGround, false, true);
		}

		public static EntityS2CPacket.MoveRelative method_34138(PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			short s = packetByteBuf.readShort();
			short t = packetByteBuf.readShort();
			short u = packetByteBuf.readShort();
			boolean bl = packetByteBuf.readBoolean();
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

		public static EntityS2CPacket.Rotate method_34140(PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			byte b = packetByteBuf.readByte();
			byte c = packetByteBuf.readByte();
			boolean bl = packetByteBuf.readBoolean();
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

		public static EntityS2CPacket.RotateAndMoveRelative method_34139(PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			short s = packetByteBuf.readShort();
			short t = packetByteBuf.readShort();
			short u = packetByteBuf.readShort();
			byte b = packetByteBuf.readByte();
			byte c = packetByteBuf.readByte();
			boolean bl = packetByteBuf.readBoolean();
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
