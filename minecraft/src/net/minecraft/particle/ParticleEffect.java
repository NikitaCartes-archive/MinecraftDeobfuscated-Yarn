package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface ParticleEffect {
	ParticleType<?> getType();

	String asString();

	@Deprecated
	public interface Factory<T extends ParticleEffect> {
		T read(ParticleType<T> type, StringReader reader) throws CommandSyntaxException;
	}
}
