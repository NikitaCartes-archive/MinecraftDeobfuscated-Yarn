package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.PacketByteBuf;

public interface Particle {
	ParticleType<?> getParticleType();

	void method_10294(PacketByteBuf packetByteBuf);

	String asString();

	public interface class_2395<T extends Particle> {
		T method_10296(ParticleType<T> particleType, StringReader stringReader) throws CommandSyntaxException;

		T method_10297(ParticleType<T> particleType, PacketByteBuf packetByteBuf);
	}
}
