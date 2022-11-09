/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.predicate.entity.FishingHookPredicate;
import net.minecraft.predicate.entity.LightningBoltPredicate;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.predicate.entity.SlimePredicate;
import net.minecraft.predicate.entity.VariantPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public interface TypeSpecificPredicate {
    public static final TypeSpecificPredicate ANY = new TypeSpecificPredicate(){

        @Override
        public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
            return true;
        }

        @Override
        public JsonObject typeSpecificToJson() {
            return new JsonObject();
        }

        @Override
        public Deserializer getDeserializer() {
            return Deserializers.ANY;
        }
    };

    public static TypeSpecificPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "type_specific");
        String string = JsonHelper.getString(jsonObject, "type", null);
        if (string == null) {
            return ANY;
        }
        Deserializer deserializer = (Deserializer)Deserializers.TYPES.get(string);
        if (deserializer == null) {
            throw new JsonSyntaxException("Unknown sub-predicate type: " + string);
        }
        return deserializer.deserialize(jsonObject);
    }

    public boolean test(Entity var1, ServerWorld var2, @Nullable Vec3d var3);

    public JsonObject typeSpecificToJson();

    default public JsonElement toJson() {
        if (this.getDeserializer() == Deserializers.ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = this.typeSpecificToJson();
        String string = (String)Deserializers.TYPES.inverse().get(this.getDeserializer());
        jsonObject.addProperty("type", string);
        return jsonObject;
    }

    public Deserializer getDeserializer();

    public static TypeSpecificPredicate cat(CatVariant variant) {
        return Deserializers.CAT.createPredicate(variant);
    }

    public static TypeSpecificPredicate frog(FrogVariant variant) {
        return Deserializers.FROG.createPredicate(variant);
    }

    public static final class Deserializers {
        public static final Deserializer ANY = json -> ANY;
        public static final Deserializer LIGHTNING = LightningBoltPredicate::fromJson;
        public static final Deserializer FISHING_HOOK = FishingHookPredicate::fromJson;
        public static final Deserializer PLAYER = PlayerPredicate::fromJson;
        public static final Deserializer SLIME = SlimePredicate::fromJson;
        public static final VariantPredicates<CatVariant> CAT = VariantPredicates.create(Registries.CAT_VARIANT, entity -> {
            Optional<Object> optional;
            if (entity instanceof CatEntity) {
                CatEntity catEntity = (CatEntity)entity;
                optional = Optional.of(catEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<FrogVariant> FROG = VariantPredicates.create(Registries.FROG_VARIANT, entity -> {
            Optional<Object> optional;
            if (entity instanceof FrogEntity) {
                FrogEntity frogEntity = (FrogEntity)entity;
                optional = Optional.of(frogEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final BiMap<String, Deserializer> TYPES = ImmutableBiMap.of("any", ANY, "lightning", LIGHTNING, "fishing_hook", FISHING_HOOK, "player", PLAYER, "slime", SLIME, "cat", CAT.getDeserializer(), "frog", FROG.getDeserializer());
    }

    public static interface Deserializer {
        public TypeSpecificPredicate deserialize(JsonObject var1);
    }
}

