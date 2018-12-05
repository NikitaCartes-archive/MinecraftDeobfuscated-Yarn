package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class class_2859 implements Packet<ServerPlayPacketListener> {
	private class_2859.class_2860 field_13021;
	private Identifier field_13020;

	public class_2859() {
	}

	@Environment(EnvType.CLIENT)
	public class_2859(class_2859.class_2860 arg, @Nullable Identifier identifier) {
		this.field_13021 = arg;
		this.field_13020 = identifier;
	}

	@Environment(EnvType.CLIENT)
	public static class_2859 method_12418(SimpleAdvancement simpleAdvancement) {
		return new class_2859(class_2859.class_2860.field_13024, simpleAdvancement.getId());
	}

	@Environment(EnvType.CLIENT)
	public static class_2859 method_12414() {
		return new class_2859(class_2859.class_2860.field_13023, null);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13021 = packetByteBuf.readEnumConstant(class_2859.class_2860.class);
		if (this.field_13021 == class_2859.class_2860.field_13024) {
			this.field_13020 = packetByteBuf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.field_13021);
		if (this.field_13021 == class_2859.class_2860.field_13024) {
			packetByteBuf.writeIdentifier(this.field_13020);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12058(this);
	}

	public class_2859.class_2860 method_12415() {
		return this.field_13021;
	}

	public Identifier method_12416() {
		return this.field_13020;
	}

	public static enum class_2860 {
		field_13024,
		field_13023;
	}
}
