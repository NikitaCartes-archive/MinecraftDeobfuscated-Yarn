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
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.predicate.entity.FishingHookPredicate;
import net.minecraft.predicate.entity.LightningBoltPredicate;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.predicate.entity.SlimePredicate;
import net.minecraft.predicate.entity.VariantPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerType;
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
        public static final VariantPredicates<AxolotlEntity.Variant> AXOLOTL = VariantPredicates.create(AxolotlEntity.Variant.CODEC, entity -> {
            Optional<Object> optional;
            if (entity instanceof AxolotlEntity) {
                AxolotlEntity axolotlEntity = (AxolotlEntity)entity;
                optional = Optional.of(axolotlEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<BoatEntity.Type> BOAT = VariantPredicates.create(BoatEntity.Type.CODEC, entity -> {
            Optional<Object> optional;
            if (entity instanceof BoatEntity) {
                BoatEntity boatEntity = (BoatEntity)entity;
                optional = Optional.of(boatEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<FoxEntity.Type> FOX = VariantPredicates.create(FoxEntity.Type.CODEC, entity -> {
            Optional<Object> optional;
            if (entity instanceof FoxEntity) {
                FoxEntity foxEntity = (FoxEntity)entity;
                optional = Optional.of(foxEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<MooshroomEntity.Type> MOOSHROOM = VariantPredicates.create(MooshroomEntity.Type.CODEC, entity -> {
            Optional<Object> optional;
            if (entity instanceof MooshroomEntity) {
                MooshroomEntity mooshroomEntity = (MooshroomEntity)entity;
                optional = Optional.of(mooshroomEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<RegistryEntry<PaintingVariant>> PAINTING = VariantPredicates.create(Registries.PAINTING_VARIANT.createEntryCodec(), entity -> {
            Optional<Object> optional;
            if (entity instanceof PaintingEntity) {
                PaintingEntity paintingEntity = (PaintingEntity)entity;
                optional = Optional.of(paintingEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<RabbitEntity.RabbitType> RABBIT = VariantPredicates.create(RabbitEntity.RabbitType.CODEC, entity -> {
            Optional<Object> optional;
            if (entity instanceof RabbitEntity) {
                RabbitEntity rabbitEntity = (RabbitEntity)entity;
                optional = Optional.of(rabbitEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<HorseColor> HORSE = VariantPredicates.create(HorseColor.CODEC, entity -> {
            Optional<Object> optional;
            if (entity instanceof HorseEntity) {
                HorseEntity horseEntity = (HorseEntity)entity;
                optional = Optional.of(horseEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<LlamaEntity.Variant> LLAMA = VariantPredicates.create(LlamaEntity.Variant.CODEC, entity -> {
            Optional<Object> optional;
            if (entity instanceof LlamaEntity) {
                LlamaEntity llamaEntity = (LlamaEntity)entity;
                optional = Optional.of(llamaEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<VillagerType> VILLAGER = VariantPredicates.create(Registries.VILLAGER_TYPE.getCodec(), entity -> {
            Optional<Object> optional;
            if (entity instanceof VillagerDataContainer) {
                VillagerDataContainer villagerDataContainer = (VillagerDataContainer)((Object)entity);
                optional = Optional.of(villagerDataContainer.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<ParrotEntity.Variant> PARROT = VariantPredicates.create(ParrotEntity.Variant.CODEC, entity -> {
            Optional<Object> optional;
            if (entity instanceof ParrotEntity) {
                ParrotEntity parrotEntity = (ParrotEntity)entity;
                optional = Optional.of(parrotEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final VariantPredicates<TropicalFishEntity.Variety> TROPICAL_FISH = VariantPredicates.create(TropicalFishEntity.Variety.CODEC, entity -> {
            Optional<Object> optional;
            if (entity instanceof TropicalFishEntity) {
                TropicalFishEntity tropicalFishEntity = (TropicalFishEntity)entity;
                optional = Optional.of(tropicalFishEntity.getVariant());
            } else {
                optional = Optional.empty();
            }
            return optional;
        });
        public static final BiMap<String, Deserializer> TYPES = ((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)ImmutableBiMap.builder().put("any", ANY)).put("lightning", LIGHTNING)).put("fishing_hook", FISHING_HOOK)).put("player", PLAYER)).put("slime", SLIME)).put("cat", CAT.getDeserializer())).put("frog", FROG.getDeserializer())).put("axolotl", AXOLOTL.getDeserializer())).put("boat", BOAT.getDeserializer())).put("fox", FOX.getDeserializer())).put("mooshroom", MOOSHROOM.getDeserializer())).put("painting", PAINTING.getDeserializer())).put("rabbit", RABBIT.getDeserializer())).put("horse", HORSE.getDeserializer())).put("llama", LLAMA.getDeserializer())).put("villager", VILLAGER.getDeserializer())).put("parrot", PARROT.getDeserializer())).put("tropical_fish", TROPICAL_FISH.getDeserializer())).buildOrThrow();
    }

    public static interface Deserializer {
        public TypeSpecificPredicate deserialize(JsonObject var1);
    }
}

