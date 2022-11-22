package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

public class VariantPredicates<V> {
	private static final String VARIANT_KEY = "variant";
	final Codec<V> codec;
	final Function<Entity, Optional<V>> variantGetter;
	final TypeSpecificPredicate.Deserializer deserializer;

	public static <V> VariantPredicates<V> create(Registry<V> registry, Function<Entity, Optional<V>> variantGetter) {
		return new VariantPredicates<>(registry.getCodec(), variantGetter);
	}

	public static <V> VariantPredicates<V> create(Codec<V> codec, Function<Entity, Optional<V>> variantGetter) {
		return new VariantPredicates<>(codec, variantGetter);
	}

	private VariantPredicates(Codec<V> codec, Function<Entity, Optional<V>> variantGetter) {
		this.codec = codec;
		this.variantGetter = variantGetter;
		this.deserializer = json -> {
			JsonElement jsonElement = json.get("variant");
			if (jsonElement == null) {
				throw new JsonParseException("Missing variant field");
			} else {
				V object = Util.getResult(codec.decode(new Dynamic<>(JsonOps.INSTANCE, jsonElement)), JsonParseException::new).getFirst();
				return this.createPredicate(object);
			}
		};
	}

	public TypeSpecificPredicate.Deserializer getDeserializer() {
		return this.deserializer;
	}

	public TypeSpecificPredicate createPredicate(V variant) {
		return new TypeSpecificPredicate() {
			@Override
			public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
				return ((Optional)VariantPredicates.this.variantGetter.apply(entity)).filter(variantxx -> variantxx.equals(variant)).isPresent();
			}

			@Override
			public JsonObject typeSpecificToJson() {
				JsonObject jsonObject = new JsonObject();
				jsonObject.add(
					"variant",
					Util.getResult(
						VariantPredicates.this.codec.encodeStart(JsonOps.INSTANCE, variant),
						string -> new JsonParseException("Can't serialize variant " + variant + ", message " + string)
					)
				);
				return jsonObject;
			}

			@Override
			public TypeSpecificPredicate.Deserializer getDeserializer() {
				return VariantPredicates.this.deserializer;
			}
		};
	}
}
