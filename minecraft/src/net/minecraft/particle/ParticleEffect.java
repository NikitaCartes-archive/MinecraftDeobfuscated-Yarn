package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.PacketByteBuf;

public interface ParticleEffect {
	ParticleType<?> getType();

	void write(PacketByteBuf buf);

	String asString();

	public interface Factory<T extends ParticleEffect> {
		T read(ParticleType<T> type, StringReader reader) throws CommandSyntaxException;

		T read(ParticleType<T> type, PacketByteBuf buf);
	}
}
