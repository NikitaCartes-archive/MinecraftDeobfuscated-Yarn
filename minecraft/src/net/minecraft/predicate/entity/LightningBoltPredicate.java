package net.minecraft.predicate.entity;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class LightningBoltPredicate implements TypeSpecificPredicate {
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

	public static LightningBoltPredicate fromJson(JsonObject json) {
		return new LightningBoltPredicate(NumberRange.IntRange.fromJson(json.get("blocks_set_on_fire")), EntityPredicate.fromJson(json.get("entity_struck")));
	}

	@Override
	public JsonObject typeSpecificToJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("blocks_set_on_fire", this.blocksSetOnFire.toJson());
		jsonObject.add("entity_struck", this.entityStruck.toJson());
		return jsonObject;
	}

	@Override
	public TypeSpecificPredicate.Deserializer getDeserializer() {
		return TypeSpecificPredicate.Deserializers.LIGHTNING;
	}

	@Override
	public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
		return !(entity instanceof LightningEntity lightningEntity)
			? false
			: this.blocksSetOnFire.test(lightningEntity.getBlocksSetOnFire())
				&& (
					this.entityStruck == EntityPredicate.ANY || lightningEntity.getStruckEntities().anyMatch(struckEntity -> this.entityStruck.test(world, pos, struckEntity))
				);
	}
}
