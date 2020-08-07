package net.minecraft;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class class_5505<E> extends MapCodec<Registry<E>> {
	private final RegistryKey<? extends Registry<E>> field_26737;

	public static <E> class_5505<E> method_31148(RegistryKey<? extends Registry<E>> registryKey) {
		return new class_5505<>(registryKey);
	}

	private class_5505(RegistryKey<? extends Registry<E>> registryKey) {
		this.field_26737 = registryKey;
	}

	public <T> RecordBuilder<T> method_31147(Registry<E> registry, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
		return recordBuilder;
	}

	@Override
	public <T> DataResult<Registry<E>> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
		return dynamicOps instanceof RegistryOps ? ((RegistryOps)dynamicOps).method_31152(this.field_26737) : DataResult.error("Not a registry ops");
	}

	public String toString() {
		return "RegistryLookupCodec[" + this.field_26737 + "]";
	}

	@Override
	public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
		return Stream.empty();
	}
}
