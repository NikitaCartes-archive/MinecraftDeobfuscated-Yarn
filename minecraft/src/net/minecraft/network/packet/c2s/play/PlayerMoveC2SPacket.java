package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public abstract class PlayerMoveC2SPacket implements Packet<ServerPlayPacketListener> {
	protected final double x;
	protected final double y;
	protected final double z;
	protected final float yaw;
	protected final float pitch;
	protected final boolean field_29179;
	protected final boolean changePosition;
	protected final boolean changeLook;

	protected PlayerMoveC2SPacket(double d, double e, double f, float g, float h, boolean bl, boolean bl2, boolean bl3) {
		this.x = d;
		this.y = e;
		this.z = f;
		this.yaw = g;
		this.pitch = h;
		this.field_29179 = bl;
		this.changePosition = bl2;
		this.changeLook = bl3;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerMove(this);
	}

	public double getX(double currentX) {
		return this.changePosition ? this.x : currentX;
	}

	public double getY(double currentY) {
		return this.changePosition ? this.y : currentY;
	}

	public double getZ(double currentZ) {
		return this.changePosition ? this.z : currentZ;
	}

	public float getYaw(float currentYaw) {
		return this.changeLook ? this.yaw : currentYaw;
	}

	public float getPitch(float currentPitch) {
		return this.changeLook ? this.pitch : currentPitch;
	}

	public boolean isOnGround() {
		return this.field_29179;
	}

	public static class Both extends PlayerMoveC2SPacket {
		public Both(double x, double y, double z, float yaw, float pitch, boolean onGround) {
			super(x, y, z, yaw, pitch, onGround, true, true);
		}

		public static PlayerMoveC2SPacket.Both method_34222(PacketByteBuf packetByteBuf) {
			double d = packetByteBuf.readDouble();
			double e = packetByteBuf.readDouble();
			double f = packetByteBuf.readDouble();
			float g = packetByteBuf.readFloat();
			float h = packetByteBuf.readFloat();
			boolean bl = packetByteBuf.readUnsignedByte() != 0;
			return new PlayerMoveC2SPacket.Both(d, e, f, g, h, bl);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
			buf.writeByte(this.field_29179 ? 1 : 0);
		}
	}

	public static class LookOnly extends PlayerMoveC2SPacket {
		public LookOnly(float yaw, float pitch, boolean onGround) {
			super(0.0, 0.0, 0.0, yaw, pitch, onGround, false, true);
		}

		public static PlayerMoveC2SPacket.LookOnly method_34223(PacketByteBuf packetByteBuf) {
			float f = packetByteBuf.readFloat();
			float g = packetByteBuf.readFloat();
			boolean bl = packetByteBuf.readUnsignedByte() != 0;
			return new PlayerMoveC2SPacket.LookOnly(f, g, bl);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
			buf.writeByte(this.field_29179 ? 1 : 0);
		}
	}

	public static class PositionOnly extends PlayerMoveC2SPacket {
		public PositionOnly(double x, double y, double z, boolean onGround) {
			super(x, y, z, 0.0F, 0.0F, onGround, true, false);
		}

		public static PlayerMoveC2SPacket.PositionOnly method_34221(PacketByteBuf packetByteBuf) {
			double d = packetByteBuf.readDouble();
			double e = packetByteBuf.readDouble();
			double f = packetByteBuf.readDouble();
			boolean bl = packetByteBuf.readUnsignedByte() != 0;
			return new PlayerMoveC2SPacket.PositionOnly(d, e, f, bl);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			buf.writeByte(this.field_29179 ? 1 : 0);
		}
	}

	public static class class_5911 extends PlayerMoveC2SPacket {
		public class_5911(boolean bl) {
			super(0.0, 0.0, 0.0, 0.0F, 0.0F, bl, false, false);
		}

		public static PlayerMoveC2SPacket.class_5911 method_34224(PacketByteBuf packetByteBuf) {
			boolean bl = packetByteBuf.readUnsignedByte() != 0;
			return new PlayerMoveC2SPacket.class_5911(bl);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeByte(this.field_29179 ? 1 : 0);
		}
	}
}
