package net.minecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.random.Random;

public abstract class class_8285<T> extends class_8275<RegistryKey<T>, RegistryKey<T>> {
	protected final RegistryKey<? extends Registry<T>> field_43488;
	protected final Map<RegistryKey<T>, RegistryKey<T>> field_43489 = new HashMap();

	public class_8285(RegistryKey<? extends Registry<T>> registryKey) {
		super(RegistryKey.createCodec(registryKey), RegistryKey.createCodec(registryKey));
		this.field_43488 = registryKey;
	}

	protected void method_50138(RegistryKey<T> registryKey, RegistryKey<T> registryKey2) {
		this.field_43489.put(registryKey, registryKey2);
	}

	protected void method_50136(RegistryKey<T> registryKey) {
		this.field_43489.remove(registryKey);
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43489.entrySet().stream().map(entry -> new class_8275.class_8276((RegistryKey)entry.getKey(), (RegistryKey)entry.getValue()));
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		Registry<T> registry = minecraftServer.getRegistryManager().get(this.field_43488);
		return registry.getRandom(random)
			.stream()
			.flatMap(
				reference -> {
					RegistryKey<T> registryKey = reference.registryKey();
					return Stream.generate(() -> registry.getRandom(random))
						.flatMap(Optional::stream)
						.filter(referencex -> !referencex.matchesKey(registryKey))
						.limit((long)i)
						.map(referencex -> new class_8275.class_8276(registryKey, referencex.registryKey()));
				}
			);
	}
}
