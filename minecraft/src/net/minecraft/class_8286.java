package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public abstract class class_8286<T> extends class_8294<RegistryKey<T>> {
	private final String field_43490;
	private final RegistryKey<? extends Registry<T>> field_43491;

	protected class_8286(String string, RegistryKey<? extends Registry<T>> registryKey) {
		this.field_43490 = string;
		this.field_43491 = registryKey;
	}

	@Override
	protected Codec<RegistryKey<T>> method_50185() {
		return RegistryKey.createCodec(this.field_43491);
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return Stream.generate(() -> Unit.INSTANCE)
			.flatMap(unit -> minecraftServer.getRegistryManager().get(this.field_43491).getRandom(random).stream())
			.map(RegistryEntry.Reference::registryKey)
			.distinct()
			.filter(registryKey -> !this.method_50256(registryKey))
			.limit((long)i)
			.map(object -> new class_8294.class_8295(object));
	}

	protected Text method_50187(RegistryKey<T> registryKey) {
		return this.method_50189(Text.translatable(Util.createTranslationKey(this.field_43490, registryKey.getValue())));
	}

	protected abstract Text method_50189(Text text);
}
