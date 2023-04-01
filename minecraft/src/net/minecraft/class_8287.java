package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public abstract class class_8287<T> implements class_8289 {
	private final RegistryKey<? extends Registry<T>> field_43492;
	final RegistryKey<T> field_43493;
	RegistryKey<T> field_43494;
	private final Codec<class_8291> field_43495;

	public class_8287(RegistryKey<? extends Registry<T>> registryKey, RegistryKey<T> registryKey2) {
		this.field_43492 = registryKey;
		this.field_43493 = registryKey2;
		this.field_43494 = registryKey2;
		this.field_43495 = class_8289.method_50202(
			RegistryKey.createCodec(registryKey).xmap(registryKeyx -> new class_8287.class_8288(registryKeyx), arg -> arg.field_43498)
		);
	}

	@Override
	public Codec<class_8291> method_50120() {
		return this.field_43495;
	}

	public RegistryKey<T> method_50193() {
		return this.field_43494;
	}

	public RegistryKey<T> method_50200() {
		return this.field_43493;
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43494.equals(this.field_43493) ? Stream.empty() : Stream.of(new class_8287.class_8288(this.field_43494));
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		Registry<T> registry = minecraftServer.getRegistryManager().get(this.field_43492);
		return Stream.generate(() -> registry.getRandom(random))
			.flatMap(Optional::stream)
			.filter(reference -> !reference.matchesKey(this.field_43493) && !reference.matchesKey(this.field_43494))
			.limit((long)i)
			.map(reference -> new class_8287.class_8288(reference.registryKey()));
	}

	protected abstract Text method_50194(RegistryKey<T> registryKey);

	class class_8288 implements class_8291.class_8292 {
		final RegistryKey<T> field_43498;

		class_8288(RegistryKey<T> registryKey) {
			this.field_43498 = registryKey;
		}

		@Override
		public class_8289 method_50121() {
			return class_8287.this;
		}

		@Override
		public void method_50122(class_8290 arg) {
			class_8287.this.field_43494 = switch (arg) {
				case APPROVE -> this.field_43498;
				case REPEAL -> class_8287.this.field_43493;
			};
		}

		@Override
		public Text method_50123() {
			return class_8287.this.method_50194(this.field_43498);
		}
	}
}
