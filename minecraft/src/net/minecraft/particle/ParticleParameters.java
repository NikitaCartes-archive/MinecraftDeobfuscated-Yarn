package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.PacketByteBuf;

public interface ParticleParameters {
	ParticleType<?> getType();

	void write(PacketByteBuf packetByteBuf);

	String asString();

	public interface Factory<T extends ParticleParameters> {
		T read(ParticleType<T> particleType, StringReader stringReader) throws CommandSyntaxException;

		T read(ParticleType<T> particleType, PacketByteBuf packetByteBuf);
	}
}
