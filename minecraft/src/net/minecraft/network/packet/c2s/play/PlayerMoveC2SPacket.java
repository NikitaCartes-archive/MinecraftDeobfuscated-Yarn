package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class PlayerMoveC2SPacket implements Packet<ServerPlayPacketListener> {
	protected double x;
	protected double y;
	protected double z;
	protected float yaw;
	protected float pitch;
	protected boolean onGround;
	protected boolean changePosition;
	protected boolean changeLook;

	public PlayerMoveC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerMoveC2SPacket(boolean onGround) {
		this.onGround = onGround;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerMove(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.onGround = buf.readUnsignedByte() != 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.onGround ? 1 : 0);
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
		return this.onGround;
	}

	public static class Both extends PlayerMoveC2SPacket {
		public Both() {
			this.changePosition = true;
			this.changeLook = true;
		}

		@Environment(EnvType.CLIENT)
		public Both(double x, double y, double z, float yaw, float pitch, boolean onGround) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.yaw = yaw;
			this.pitch = pitch;
			this.onGround = onGround;
			this.changeLook = true;
			this.changePosition = true;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			this.yaw = buf.readFloat();
			this.pitch = buf.readFloat();
			super.read(buf);
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
			super.write(buf);
		}
	}

	public static class LookOnly extends PlayerMoveC2SPacket {
		public LookOnly() {
			this.changeLook = true;
		}

		@Environment(EnvType.CLIENT)
		public LookOnly(float yaw, float pitch, boolean onGround) {
			this.yaw = yaw;
			this.pitch = pitch;
			this.onGround = onGround;
			this.changeLook = true;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			this.yaw = buf.readFloat();
			this.pitch = buf.readFloat();
			super.read(buf);
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
			super.write(buf);
		}
	}

	public static class PositionOnly extends PlayerMoveC2SPacket {
		public PositionOnly() {
			this.changePosition = true;
		}

		@Environment(EnvType.CLIENT)
		public PositionOnly(double x, double y, double z, boolean onGround) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.onGround = onGround;
			this.changePosition = true;
		}

		@Override
		public void read(PacketByteBuf buf) throws IOException {
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			super.read(buf);
		}

		@Override
		public void write(PacketByteBuf buf) throws IOException {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			super.write(buf);
		}
	}
}
