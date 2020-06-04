package net.minecraft;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class class_5381<E> implements Codec<Supplier<E>> {
	private final RegistryKey<Registry<E>> field_25507;
	private final Codec<E> field_25508;

	public static <E> class_5381<E> method_29749(RegistryKey<Registry<E>> registryKey, Codec<E> codec) {
		return new class_5381<>(registryKey, codec);
	}

	private class_5381(RegistryKey<Registry<E>> registryKey, Codec<E> codec) {
		this.field_25507 = registryKey;
		this.field_25508 = codec;
	}

	public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicOps, T object) {
		return dynamicOps instanceof class_5384
			? ((class_5384)dynamicOps).method_29772(supplier.get(), object, this.field_25507, this.field_25508)
			: this.field_25508.encode((E)supplier.get(), dynamicOps, object);
	}

	@Override
	public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> dynamicOps, T object) {
		return dynamicOps instanceof class_5382
			? ((class_5382)dynamicOps).method_29759(object, this.field_25507, this.field_25508)
			: this.field_25508.decode(dynamicOps, object).map(pair -> pair.mapFirst(objectx -> () -> objectx));
	}

	public String toString() {
		return "RegistryFileCodec[" + this.field_25507 + " " + this.field_25508 + "]";
	}
}
