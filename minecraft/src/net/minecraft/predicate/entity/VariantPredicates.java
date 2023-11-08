package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class VariantPredicates<V> {
	private final Function<Entity, Optional<V>> variantGetter;
	private final TypeSpecificPredicate.Type deserializer;

	public static <V> VariantPredicates<V> create(Registry<V> registry, Function<Entity, Optional<V>> variantGetter) {
		return new VariantPredicates<>(registry.getCodec(), variantGetter);
	}

	public static <V> VariantPredicates<V> create(Codec<V> codec, Function<Entity, Optional<V>> variantGetter) {
		return new VariantPredicates<>(codec, variantGetter);
	}

	private VariantPredicates(Codec<V> codec, Function<Entity, Optional<V>> variantGetter) {
		this.variantGetter = variantGetter;
		MapCodec<VariantPredicates.Predicate<V>> mapCodec = RecordCodecBuilder.mapCodec(
			instance -> instance.group(codec.fieldOf("variant").forGetter(VariantPredicates.Predicate::variant)).apply(instance, this::createPredicate)
		);
		this.deserializer = new TypeSpecificPredicate.Type(mapCodec);
	}

	public TypeSpecificPredicate.Type getDeserializer() {
		return this.deserializer;
	}

	public VariantPredicates.Predicate<V> createPredicate(V variant) {
		return new VariantPredicates.Predicate<>(this.deserializer, this.variantGetter, variant);
	}

	public static record Predicate<V>(TypeSpecificPredicate.Type type, Function<Entity, Optional<V>> getter, V variant) implements TypeSpecificPredicate {
		@Override
		public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
			return ((Optional)this.getter.apply(entity)).filter(variant -> variant.equals(this.variant)).isPresent();
		}
	}
}
