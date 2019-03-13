package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerInputC2SPacket implements Packet<ServerPlayPacketListener> {
	private float sideways;
	private float forward;
	private boolean jumping;
	private boolean sneaking;

	public PlayerInputC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerInputC2SPacket(float f, float g, boolean bl, boolean bl2) {
		this.sideways = f;
		this.forward = g;
		this.jumping = bl;
		this.sneaking = bl2;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.sideways = packetByteBuf.readFloat();
		this.forward = packetByteBuf.readFloat();
		byte b = packetByteBuf.readByte();
		this.jumping = (b & 1) > 0;
		this.sneaking = (b & 2) > 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeFloat(this.sideways);
		packetByteBuf.writeFloat(this.forward);
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

	public float getSideways() {
		return this.sideways;
	}

	public float getForward() {
		return this.forward;
	}

	public boolean isJumping() {
		return this.jumping;
	}

	public boolean isSneaking() {
		return this.sneaking;
	}
}
