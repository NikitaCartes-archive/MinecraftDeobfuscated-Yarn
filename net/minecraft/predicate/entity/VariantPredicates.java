/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class VariantPredicates<V> {
    private static final String VARIANT_KEY = "variant";
    final Registry<V> registry;
    final Function<Entity, Optional<V>> variantGetter;
    final TypeSpecificPredicate.Deserializer deserializer;

    public static <V> VariantPredicates<V> create(Registry<V> registry, Function<Entity, Optional<V>> variantGetter) {
        return new VariantPredicates<V>(registry, variantGetter);
    }

    private VariantPredicates(Registry<V> registry, Function<Entity, Optional<V>> variantGetter) {
        this.registry = registry;
        this.variantGetter = variantGetter;
        this.deserializer = json -> {
            String string = JsonHelper.getString(json, VARIANT_KEY);
            Object object = registry.get(Identifier.tryParse(string));
            if (object == null) {
                throw new JsonSyntaxException("Unknown variant: " + string);
            }
            return this.createPredicate(object);
        };
    }

    public TypeSpecificPredicate.Deserializer getDeserializer() {
        return this.deserializer;
    }

    public TypeSpecificPredicate createPredicate(final V variant) {
        return new TypeSpecificPredicate(){

            @Override
            public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
                return VariantPredicates.this.variantGetter.apply(entity).filter(variant -> variant.equals(variant)).isPresent();
            }

            @Override
            public JsonObject typeSpecificToJson() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(VariantPredicates.VARIANT_KEY, VariantPredicates.this.registry.getId(variant).toString());
                return jsonObject;
            }

            @Override
            public TypeSpecificPredicate.Deserializer getDeserializer() {
                return VariantPredicates.this.deserializer;
            }
        };
    }
}

