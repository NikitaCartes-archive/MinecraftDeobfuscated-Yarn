package net.minecraft;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public final class class_5380<E> implements Codec<SimpleRegistry<E>> {
	private final Codec<SimpleRegistry<E>> field_25504;
	private final RegistryKey<Registry<E>> field_25505;
	private final Codec<E> field_25506;

	public static <E> class_5380<E> method_29745(RegistryKey<Registry<E>> registryKey, Lifecycle lifecycle, Codec<E> codec) {
		return new class_5380<>(registryKey, lifecycle, codec);
	}

	private class_5380(RegistryKey<Registry<E>> registryKey, Lifecycle lifecycle, Codec<E> codec) {
		this.field_25504 = SimpleRegistry.method_29724(registryKey, lifecycle, codec);
		this.field_25505 = registryKey;
		this.field_25506 = codec;
	}

	public <T> DataResult<T> encode(SimpleRegistry<E> simpleRegistry, DynamicOps<T> dynamicOps, T object) {
		return this.field_25504.encode(simpleRegistry, dynamicOps, object);
	}

	@Override
	public <T> DataResult<Pair<SimpleRegistry<E>, T>> decode(DynamicOps<T> dynamicOps, T object) {
		DataResult<Pair<SimpleRegistry<E>, T>> dataResult = this.field_25504.decode(dynamicOps, object);
		return dynamicOps instanceof class_5382
			? dataResult.flatMap(
				pair -> ((class_5382)dynamicOps)
						.method_29755((SimpleRegistry<E>)pair.getFirst(), this.field_25505, this.field_25506)
						.map(simpleRegistry -> Pair.of(simpleRegistry, pair.getSecond()))
			)
			: dataResult;
	}

	public String toString() {
		return "RegistryFileCodec[" + this.field_25505 + " " + this.field_25506 + "]";
	}
}
