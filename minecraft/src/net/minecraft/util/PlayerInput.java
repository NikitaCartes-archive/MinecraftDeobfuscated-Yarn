package net.minecraft.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record PlayerInput(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
	private static final byte FORWARD = 1;
	private static final byte BACKWARD = 2;
	private static final byte LEFT = 4;
	private static final byte RIGHT = 8;
	private static final byte JUMP = 16;
	private static final byte SNEAK = 32;
	private static final byte SPRINT = 64;
	public static final PacketCodec<PacketByteBuf, PlayerInput> PACKET_CODEC = new PacketCodec<PacketByteBuf, PlayerInput>() {
		public void encode(PacketByteBuf packetByteBuf, PlayerInput playerInput) {
			byte b = 0;
			b = (byte)(b | (playerInput.forward() ? 1 : 0));
			b = (byte)(b | (playerInput.backward() ? 2 : 0));
			b = (byte)(b | (playerInput.left() ? 4 : 0));
			b = (byte)(b | (playerInput.right() ? 8 : 0));
			b = (byte)(b | (playerInput.jump() ? 16 : 0));
			b = (byte)(b | (playerInput.sneak() ? 32 : 0));
			b = (byte)(b | (playerInput.sprint() ? 64 : 0));
			packetByteBuf.writeByte(b);
		}

		public PlayerInput decode(PacketByteBuf packetByteBuf) {
			byte b = packetByteBuf.readByte();
			boolean bl = (b & 1) != 0;
			boolean bl2 = (b & 2) != 0;
			boolean bl3 = (b & 4) != 0;
			boolean bl4 = (b & 8) != 0;
			boolean bl5 = (b & 16) != 0;
			boolean bl6 = (b & 32) != 0;
			boolean bl7 = (b & 64) != 0;
			return new PlayerInput(bl, bl2, bl3, bl4, bl5, bl6, bl7);
		}
	};
	public static PlayerInput DEFAULT = new PlayerInput(false, false, false, false, false, false, false);
}
