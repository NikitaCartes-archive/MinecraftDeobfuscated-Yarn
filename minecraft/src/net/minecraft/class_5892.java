package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class class_5892 implements Packet<ClientPlayPacketListener> {
	private final int field_29133;
	private final int field_29134;
	private final Text field_29135;

	public class_5892(DamageTracker damageTracker, Text text) {
		this(damageTracker.getEntity().getId(), damageTracker.method_33937(), text);
	}

	public class_5892(int i, int j, Text text) {
		this.field_29133 = i;
		this.field_29134 = j;
		this.field_29135 = text;
	}

	public class_5892(PacketByteBuf packetByteBuf) {
		this.field_29133 = packetByteBuf.readVarInt();
		this.field_29134 = packetByteBuf.readInt();
		this.field_29135 = packetByteBuf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.field_29133);
		buf.writeInt(this.field_29134);
		buf.writeText(this.field_29135);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34075(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public int method_34144() {
		return this.field_29133;
	}

	@Environment(EnvType.CLIENT)
	public Text method_34145() {
		return this.field_29135;
	}
}
