package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

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
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "lightning");
			return new LightningBoltPredicate(
				NumberRange.IntRange.fromJson(jsonObject.get("blocks_set_on_fire")), EntityPredicate.fromJson(jsonObject.get("entity_struck"))
			);
		} else {
			return ANY;
		}
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("blocks_set_on_fire", this.blocksSetOnFire.toJson());
			jsonObject.add("entity_struck", this.entityStruck.toJson());
			return jsonObject;
		}
	}

	public boolean test(Entity lightningBolt, ServerWorld world, @Nullable Vec3d vec3d) {
		if (this == ANY) {
			return true;
		} else {
			return !(lightningBolt instanceof LightningEntity lightningEntity)
				? false
				: this.blocksSetOnFire.test(lightningEntity.getBlocksSetOnFire())
					&& (this.entityStruck == EntityPredicate.ANY || lightningEntity.getStruckEntities().anyMatch(entity -> this.entityStruck.test(world, vec3d, entity)));
		}
	}
}
