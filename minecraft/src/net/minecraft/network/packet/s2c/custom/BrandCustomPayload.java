package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BrandCustomPayload(String brand) implements CustomPayload {
	public static final Identifier ID = new Identifier("brand");

	public BrandCustomPayload(PacketByteBuf buf) {
		this(buf.readString());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.brand);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
