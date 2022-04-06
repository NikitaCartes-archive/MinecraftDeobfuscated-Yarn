package net.minecraft.predicate.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class VariantPredicates<V> {
	private static final String VARIANT_KEY = "variant";
	final Registry<V> registry;
	final Function<Entity, Optional<V>> variantGetter;
	final TypeSpecificPredicate.Deserializer deserializer;

	public static <V> VariantPredicates<V> create(Registry<V> registry, Function<Entity, Optional<V>> variantGetter) {
		return new VariantPredicates<>(registry, variantGetter);
	}

	private VariantPredicates(Registry<V> registry, Function<Entity, Optional<V>> variantGetter) {
		this.registry = registry;
		this.variantGetter = variantGetter;
		this.deserializer = json -> {
			String string = JsonHelper.getString(json, "variant");
			V object = registry.get(Identifier.tryParse(string));
			if (object == null) {
				throw new JsonSyntaxException("Unknown variant: " + string);
			} else {
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
				jsonObject.addProperty("variant", VariantPredicates.this.registry.getId(variant).toString());
				return jsonObject;
			}

			@Override
			public TypeSpecificPredicate.Deserializer getDeserializer() {
				return VariantPredicates.this.deserializer;
			}
		};
	}
}
