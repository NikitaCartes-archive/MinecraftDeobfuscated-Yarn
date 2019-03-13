package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.PacketByteBuf;

public interface ParticleParameters {
	ParticleType<?> method_10295();

	void method_10294(PacketByteBuf packetByteBuf);

	String asString();

	public interface Factory<T extends ParticleParameters> {
		T method_10296(ParticleType<T> particleType, StringReader stringReader) throws CommandSyntaxException;

		T method_10297(ParticleType<T> particleType, PacketByteBuf packetByteBuf);
	}
}
