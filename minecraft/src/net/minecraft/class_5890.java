package net.minecraft;

import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class class_5890 implements Packet<ClientPlayPacketListener> {
	private final int field_29131;
	private final int field_29132;

	public class_5890(DamageTracker damageTracker) {
		this(damageTracker.method_33937(), damageTracker.getTimeSinceLastAttack());
	}

	public class_5890(int i, int j) {
		this.field_29131 = i;
		this.field_29132 = j;
	}

	public class_5890(PacketByteBuf packetByteBuf) {
		this.field_29132 = packetByteBuf.readVarInt();
		this.field_29131 = packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.field_29132);
		buf.writeInt(this.field_29131);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34073(this);
	}
}
