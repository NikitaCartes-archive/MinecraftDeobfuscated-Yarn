/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class LightningBoltPredicate {
    public static final LightningBoltPredicate ANY = new LightningBoltPredicate(NumberRange.IntRange.ANY, EntityPredicate.ANY);
    private static final String BLOCKS_SET_ON_FIRE_KEY = "blocks_set_on_fire";
    private static final String ENTITY_STRUCK_KEY = "entity_struck";
    private final NumberRange.IntRange blocksSetOnFire;
    private final EntityPredicate entityStruck;

    private LightningBoltPredicate(NumberRange.IntRange blocksSetOnFire, EntityPredicate entityStruck) {
        this.blocksSetOnFire = blocksSetOnFire;
        this.entityStruck = entityStruck;
    }

    public static LightningBoltPredicate of(NumberRange.IntRange blocksSetOnFire) {
        return new LightningBoltPredicate(blocksSetOnFire, EntityPredicate.ANY);
    }

    public static LightningBoltPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "lightning");
        return new LightningBoltPredicate(NumberRange.IntRange.fromJson(jsonObject.get(BLOCKS_SET_ON_FIRE_KEY)), EntityPredicate.fromJson(jsonObject.get(ENTITY_STRUCK_KEY)));
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(BLOCKS_SET_ON_FIRE_KEY, this.blocksSetOnFire.toJson());
        jsonObject.add(ENTITY_STRUCK_KEY, this.entityStruck.toJson());
        return jsonObject;
    }

    public boolean test(Entity lightningBolt, ServerWorld world, @Nullable Vec3d vec3d) {
        if (this == ANY) {
            return true;
        }
        if (!(lightningBolt instanceof LightningEntity)) {
            return false;
        }
        LightningEntity lightningEntity = (LightningEntity)lightningBolt;
        return this.blocksSetOnFire.test(lightningEntity.getBlocksSetOnFire()) && (this.entityStruck == EntityPredicate.ANY || lightningEntity.getStruckEntities().anyMatch(entity -> this.entityStruck.test(world, vec3d, (Entity)entity)));
    }
}

