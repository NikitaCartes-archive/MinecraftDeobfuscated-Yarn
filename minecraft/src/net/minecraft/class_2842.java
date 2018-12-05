package net.minecraft;

import java.io.IOException;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2842 implements Packet<ServerPlayPacketListener> {
	private boolean field_12949;
	private boolean field_12948;
	private boolean field_12947;
	private boolean field_12946;
	private float field_12945;
	private float field_12944;

	public class_2842() {
	}

	public class_2842(PlayerAbilities playerAbilities) {
		this.method_12340(playerAbilities.invulnerable);
		this.method_12343(playerAbilities.flying);
		this.method_12337(playerAbilities.allowFlying);
		this.method_12342(playerAbilities.creativeMode);
		this.method_12338(playerAbilities.getFlySpeed());
		this.method_12341(playerAbilities.getWalkSpeed());
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		byte b = packetByteBuf.readByte();
		this.method_12340((b & 1) > 0);
		this.method_12343((b & 2) > 0);
		this.method_12337((b & 4) > 0);
		this.method_12342((b & 8) > 0);
		this.method_12338(packetByteBuf.readFloat());
		this.method_12341(packetByteBuf.readFloat());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		byte b = 0;
		if (this.method_12344()) {
			b = (byte)(b | 1);
		}

		if (this.method_12346()) {
			b = (byte)(b | 2);
		}

		if (this.method_12347()) {
			b = (byte)(b | 4);
		}

		if (this.method_12345()) {
			b = (byte)(b | 8);
		}

		packetByteBuf.writeByte(b);
		packetByteBuf.writeFloat(this.field_12945);
		packetByteBuf.writeFloat(this.field_12944);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12083(this);
	}

	public boolean method_12344() {
		return this.field_12949;
	}

	public void method_12340(boolean bl) {
		this.field_12949 = bl;
	}

	public boolean method_12346() {
		return this.field_12948;
	}

	public void method_12343(boolean bl) {
		this.field_12948 = bl;
	}

	public boolean method_12347() {
		return this.field_12947;
	}

	public void method_12337(boolean bl) {
		this.field_12947 = bl;
	}

	public boolean method_12345() {
		return this.field_12946;
	}

	public void method_12342(boolean bl) {
		this.field_12946 = bl;
	}

	public void method_12338(float f) {
		this.field_12945 = f;
	}

	public void method_12341(float f) {
		this.field_12944 = f;
	}
}
