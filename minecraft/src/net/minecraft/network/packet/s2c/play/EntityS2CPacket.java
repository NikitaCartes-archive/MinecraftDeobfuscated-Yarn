package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
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

public class EntityS2CPacket implements Packet<ClientPlayPacketListener> {
	protected int id;
	protected short deltaX;
	protected short deltaY;
	protected short deltaZ;
	protected byte yaw;
	protected byte pitch;
	protected boolean onGround;
	protected boolean rotate;
	protected boolean positionChanged;

	public static long encodePacketCoordinate(double coord) {
		return MathHelper.lfloor(coord * 4096.0);
	}

	@Environment(EnvType.CLIENT)
	public static double method_30301(long l) {
		return (double)l / 4096.0;
	}

	@Environment(EnvType.CLIENT)
	public Vec3d method_30302(Vec3d vec3d) {
		double d = this.deltaX == 0 ? vec3d.x : method_30301(encodePacketCoordinate(vec3d.x) + (long)this.deltaX);
		double e = this.deltaY == 0 ? vec3d.y : method_30301(encodePacketCoordinate(vec3d.y) + (long)this.deltaY);
		double f = this.deltaZ == 0 ? vec3d.z : method_30301(encodePacketCoordinate(vec3d.z) + (long)this.deltaZ);
		return new Vec3d(d, e, f);
	}

	public static Vec3d decodePacketCoordinates(long x, long y, long z) {
		return new Vec3d((double)x, (double)y, (double)z).multiply(2.4414062E-4F);
	}

	public EntityS2CPacket() {
	}

	public EntityS2CPacket(int entityId) {
		this.id = entityId;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
	}

	public void method_11651(ClientPlayPacketListener clientPlayPacketListener) {
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
		public MoveRelative() {
			this.positionChanged = true;
		}

		public MoveRelative(int entityId, short deltaX, short deltaY, short deltaZ, boolean onGround) {
			super(entityId);
			this.deltaX = deltaX;
			this.deltaY = deltaY;
			this.deltaZ = deltaZ;
			this.onGround = onGround;
			this.positionChanged = true;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			super.read(buf);
			this.deltaX = buf.readShort();
			this.deltaY = buf.readShort();
			this.deltaZ = buf.readShort();
			this.onGround = buf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			super.write(buf);
			buf.writeShort(this.deltaX);
			buf.writeShort(this.deltaY);
			buf.writeShort(this.deltaZ);
			buf.writeBoolean(this.onGround);
		}
	}

	public static class Rotate extends EntityS2CPacket {
		public Rotate() {
			this.rotate = true;
		}

		public Rotate(int entityId, byte yaw, byte pitch, boolean onGround) {
			super(entityId);
			this.yaw = yaw;
			this.pitch = pitch;
			this.rotate = true;
			this.onGround = onGround;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			super.read(buf);
			this.yaw = buf.readByte();
			this.pitch = buf.readByte();
			this.onGround = buf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			super.write(buf);
			buf.writeByte(this.yaw);
			buf.writeByte(this.pitch);
			buf.writeBoolean(this.onGround);
		}
	}

	public static class RotateAndMoveRelative extends EntityS2CPacket {
		public RotateAndMoveRelative() {
			this.rotate = true;
			this.positionChanged = true;
		}

		public RotateAndMoveRelative(int entityId, short deltaX, short deltaY, short deltaZ, byte yaw, byte pitch, boolean onGround) {
			super(entityId);
			this.deltaX = deltaX;
			this.deltaY = deltaY;
			this.deltaZ = deltaZ;
			this.yaw = yaw;
			this.pitch = pitch;
			this.onGround = onGround;
			this.rotate = true;
			this.positionChanged = true;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			super.read(buf);
			this.deltaX = buf.readShort();
			this.deltaY = buf.readShort();
			this.deltaZ = buf.readShort();
			this.yaw = buf.readByte();
			this.pitch = buf.readByte();
			this.onGround = buf.readBoolean();
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			super.write(buf);
			buf.writeShort(this.deltaX);
			buf.writeShort(this.deltaY);
			buf.writeShort(this.deltaZ);
			buf.writeByte(this.yaw);
			buf.writeByte(this.pitch);
			buf.writeBoolean(this.onGround);
		}
	}
}
