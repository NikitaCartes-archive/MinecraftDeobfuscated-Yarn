package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerLookC2SPacket implements Packet<ServerPlayPacketListener> {
	private float yaw;
	private float pitch;
	private boolean jumping;
	private boolean sneaking;

	public PlayerLookC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerLookC2SPacket(float f, float g, boolean bl, boolean bl2) {
		this.yaw = f;
		this.pitch = g;
		this.jumping = bl;
		this.sneaking = bl2;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.yaw = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
		byte b = packetByteBuf.readByte();
		this.jumping = (b & 1) > 0;
		this.sneaking = (b & 2) > 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeFloat(this.yaw);
		packetByteBuf.writeFloat(this.pitch);
		byte b = 0;
		if (this.jumping) {
			b = (byte)(b | 1);
		}

		if (this.sneaking) {
			b = (byte)(b | 2);
		}

		packetByteBuf.writeByte(b);
	}

	public void method_12369(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12067(this);
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public boolean isJumping() {
		return this.jumping;
	}

	public boolean isSneaking() {
		return this.sneaking;
	}
}
