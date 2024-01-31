package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.registry.RegistryWrapper;

public interface ParticleEffect {
	ParticleType<?> getType();

	String asString(RegistryWrapper.WrapperLookup registryLookup);

	@Deprecated
	public interface Factory<T extends ParticleEffect> {
		T read(ParticleType<T> type, StringReader reader, RegistryWrapper.WrapperLookup registryLookup) throws CommandSyntaxException;
	}
}
