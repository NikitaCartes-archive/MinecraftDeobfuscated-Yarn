/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class DamagePredicate {
    public static final DamagePredicate ANY = Builder.create().build();
    private final NumberRange.FloatRange dealt;
    private final NumberRange.FloatRange taken;
    private final EntityPredicate sourceEntity;
    private final Boolean blocked;
    private final DamageSourcePredicate type;

    public DamagePredicate() {
        this.dealt = NumberRange.FloatRange.ANY;
        this.taken = NumberRange.FloatRange.ANY;
        this.sourceEntity = EntityPredicate.ANY;
        this.blocked = null;
        this.type = DamageSourcePredicate.EMPTY;
    }

    public DamagePredicate(NumberRange.FloatRange dealt, NumberRange.FloatRange taken, EntityPredicate sourceEntity, @Nullable Boolean blocked, DamageSourcePredicate type) {
        this.dealt = dealt;
        this.taken = taken;
        this.sourceEntity = sourceEntity;
        this.blocked = blocked;
        this.type = type;
    }

    public boolean test(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
        if (this == ANY) {
            return true;
        }
        if (!this.dealt.test(dealt)) {
            return false;
        }
        if (!this.taken.test(taken)) {
            return false;
        }
        if (!this.sourceEntity.test(player, source.getAttacker())) {
            return false;
        }
        if (this.blocked != null && this.blocked != blocked) {
            return false;
        }
        return this.type.test(player, source);
    }

    public static DamagePredicate deserialize(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "damage");
        NumberRange.FloatRange floatRange = NumberRange.FloatRange.fromJson(jsonObject.get("dealt"));
        NumberRange.FloatRange floatRange2 = NumberRange.FloatRange.fromJson(jsonObject.get("taken"));
        Boolean boolean_ = jsonObject.has("blocked") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, "blocked")) : null;
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("source_entity"));
        DamageSourcePredicate damageSourcePredicate = DamageSourcePredicate.deserialize(jsonObject.get("type"));
        return new DamagePredicate(floatRange, floatRange2, entityPredicate, boolean_, damageSourcePredicate);
    }

    public JsonElement serialize() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("dealt", this.dealt.toJson());
        jsonObject.add("taken", this.taken.toJson());
        jsonObject.add("source_entity", this.sourceEntity.toJson());
        jsonObject.add("type", this.type.serialize());
        if (this.blocked != null) {
            jsonObject.addProperty("blocked", this.blocked);
        }
        return jsonObject;
    }

    public static class Builder {
        private NumberRange.FloatRange dealt = NumberRange.FloatRange.ANY;
        private NumberRange.FloatRange taken = NumberRange.FloatRange.ANY;
        private EntityPredicate sourceEntity = EntityPredicate.ANY;
        private Boolean blocked;
        private DamageSourcePredicate type = DamageSourcePredicate.EMPTY;

        public static Builder create() {
            return new Builder();
        }

        public Builder blocked(Boolean blocked) {
            this.blocked = blocked;
            return this;
        }

        public Builder type(DamageSourcePredicate.Builder builder) {
            this.type = builder.build();
            return this;
        }

        public DamagePredicate build() {
            return new DamagePredicate(this.dealt, this.taken, this.sourceEntity, this.blocked, this.type);
        }
    }
}

