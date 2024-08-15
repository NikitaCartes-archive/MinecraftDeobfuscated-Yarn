package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public abstract class PlayerMoveC2SPacket implements Packet<ServerPlayPacketListener> {
	private static final int CHANGE_POSITION_FLAG = 1;
	private static final int CHANGE_LOOK_FLAG = 2;
	protected final double x;
	protected final double y;
	protected final double z;
	protected final float yaw;
	protected final float pitch;
	protected final boolean onGround;
	protected final boolean horizontalCollision;
	protected final boolean changePosition;
	protected final boolean changeLook;

	static int toFlag(boolean changePosition, boolean changeLook) {
		int i = 0;
		if (changePosition) {
			i |= 1;
		}

		if (changeLook) {
			i |= 2;
		}

		return i;
	}

	static boolean changePosition(int flag) {
		return (flag & 1) != 0;
	}

	static boolean changeLook(int flag) {
		return (flag & 2) != 0;
	}

	protected PlayerMoveC2SPacket(
		double x, double y, double z, float yaw, float pitch, boolean onGround, boolean horizontalCollision, boolean changePosition, boolean changeLook
	) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
		this.horizontalCollision = horizontalCollision;
		this.changePosition = changePosition;
		this.changeLook = changeLook;
	}

	@Override
	public abstract PacketType<? extends PlayerMoveC2SPacket> getPacketId();

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
		return this.onGround;
	}

	public boolean horizontalCollision() {
		return this.horizontalCollision;
	}

	public boolean changesPosition() {
		return this.changePosition;
	}

	public boolean changesLook() {
		return this.changeLook;
	}

	public static class Full extends PlayerMoveC2SPacket {
		public static final PacketCodec<PacketByteBuf, PlayerMoveC2SPacket.Full> CODEC = Packet.createCodec(
			PlayerMoveC2SPacket.Full::write, PlayerMoveC2SPacket.Full::read
		);

		public Full(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean horizontalCollision) {
			super(x, y, z, yaw, pitch, onGround, horizontalCollision, true, true);
		}

		private static PlayerMoveC2SPacket.Full read(PacketByteBuf buf) {
			double d = buf.readDouble();
			double e = buf.readDouble();
			double f = buf.readDouble();
			float g = buf.readFloat();
			float h = buf.readFloat();
			short s = buf.readUnsignedByte();
			boolean bl = PlayerMoveC2SPacket.changePosition(s);
			boolean bl2 = PlayerMoveC2SPacket.changeLook(s);
			return new PlayerMoveC2SPacket.Full(d, e, f, g, h, bl, bl2);
		}

		private void write(PacketByteBuf buf) {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
			buf.writeByte(PlayerMoveC2SPacket.toFlag(this.onGround, this.horizontalCollision));
		}

		@Override
		public PacketType<PlayerMoveC2SPacket.Full> getPacketId() {
			return PlayPackets.MOVE_PLAYER_POS_ROT;
		}
	}

	public static class LookAndOnGround extends PlayerMoveC2SPacket {
		public static final PacketCodec<PacketByteBuf, PlayerMoveC2SPacket.LookAndOnGround> CODEC = Packet.createCodec(
			PlayerMoveC2SPacket.LookAndOnGround::write, PlayerMoveC2SPacket.LookAndOnGround::read
		);

		public LookAndOnGround(float yaw, float pitch, boolean onGround, boolean horizontalCollision) {
			super(0.0, 0.0, 0.0, yaw, pitch, onGround, horizontalCollision, false, true);
		}

		private static PlayerMoveC2SPacket.LookAndOnGround read(PacketByteBuf buf) {
			float f = buf.readFloat();
			float g = buf.readFloat();
			short s = buf.readUnsignedByte();
			boolean bl = PlayerMoveC2SPacket.changePosition(s);
			boolean bl2 = PlayerMoveC2SPacket.changeLook(s);
			return new PlayerMoveC2SPacket.LookAndOnGround(f, g, bl, bl2);
		}

		private void write(PacketByteBuf buf) {
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
			buf.writeByte(PlayerMoveC2SPacket.toFlag(this.onGround, this.horizontalCollision));
		}

		@Override
		public PacketType<PlayerMoveC2SPacket.LookAndOnGround> getPacketId() {
			return PlayPackets.MOVE_PLAYER_ROT;
		}
	}

	public static class OnGroundOnly extends PlayerMoveC2SPacket {
		public static final PacketCodec<PacketByteBuf, PlayerMoveC2SPacket.OnGroundOnly> CODEC = Packet.createCodec(
			PlayerMoveC2SPacket.OnGroundOnly::write, PlayerMoveC2SPacket.OnGroundOnly::read
		);

		public OnGroundOnly(boolean onGround, boolean horizontalCollision) {
			super(0.0, 0.0, 0.0, 0.0F, 0.0F, onGround, horizontalCollision, false, false);
		}

		private static PlayerMoveC2SPacket.OnGroundOnly read(PacketByteBuf buf) {
			short s = buf.readUnsignedByte();
			boolean bl = PlayerMoveC2SPacket.changePosition(s);
			boolean bl2 = PlayerMoveC2SPacket.changeLook(s);
			return new PlayerMoveC2SPacket.OnGroundOnly(bl, bl2);
		}

		private void write(PacketByteBuf buf) {
			buf.writeByte(PlayerMoveC2SPacket.toFlag(this.onGround, this.horizontalCollision));
		}

		@Override
		public PacketType<PlayerMoveC2SPacket.OnGroundOnly> getPacketId() {
			return PlayPackets.MOVE_PLAYER_STATUS_ONLY;
		}
	}

	public static class PositionAndOnGround extends PlayerMoveC2SPacket {
		public static final PacketCodec<PacketByteBuf, PlayerMoveC2SPacket.PositionAndOnGround> CODEC = Packet.createCodec(
			PlayerMoveC2SPacket.PositionAndOnGround::write, PlayerMoveC2SPacket.PositionAndOnGround::read
		);

		public PositionAndOnGround(double x, double y, double z, boolean onGround, boolean horizontalCollision) {
			super(x, y, z, 0.0F, 0.0F, onGround, horizontalCollision, true, false);
		}

		private static PlayerMoveC2SPacket.PositionAndOnGround read(PacketByteBuf buf) {
			double d = buf.readDouble();
			double e = buf.readDouble();
			double f = buf.readDouble();
			short s = buf.readUnsignedByte();
			boolean bl = PlayerMoveC2SPacket.changePosition(s);
			boolean bl2 = PlayerMoveC2SPacket.changeLook(s);
			return new PlayerMoveC2SPacket.PositionAndOnGround(d, e, f, bl, bl2);
		}

		private void write(PacketByteBuf buf) {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			buf.writeByte(PlayerMoveC2SPacket.toFlag(this.onGround, this.horizontalCollision));
		}

		@Override
		public PacketType<PlayerMoveC2SPacket.PositionAndOnGround> getPacketId() {
			return PlayPackets.MOVE_PLAYER_POS;
		}
	}
}
