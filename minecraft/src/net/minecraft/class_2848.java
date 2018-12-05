package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2848 implements Packet<ServerPlayPacketListener> {
	private int field_12977;
	private class_2848.class_2849 field_12978;
	private int field_12976;

	public class_2848() {
	}

	@Environment(EnvType.CLIENT)
	public class_2848(Entity entity, class_2848.class_2849 arg) {
		this(entity, arg, 0);
	}

	@Environment(EnvType.CLIENT)
	public class_2848(Entity entity, class_2848.class_2849 arg, int i) {
		this.field_12977 = entity.getEntityId();
		this.field_12978 = arg;
		this.field_12976 = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12977 = packetByteBuf.readVarInt();
		this.field_12978 = packetByteBuf.readEnumConstant(class_2848.class_2849.class);
		this.field_12976 = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_12977);
		packetByteBuf.writeEnumConstant(this.field_12978);
		packetByteBuf.writeVarInt(this.field_12976);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12045(this);
	}

	public class_2848.class_2849 method_12365() {
		return this.field_12978;
	}

	public int method_12366() {
		return this.field_12976;
	}

	public static enum class_2849 {
		field_12979,
		field_12984,
		field_12986,
		field_12981,
		field_12985,
		field_12987,
		field_12980,
		field_12988,
		field_12982;
	}
}
