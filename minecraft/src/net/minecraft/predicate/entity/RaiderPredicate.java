package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record RaiderPredicate(boolean hasRaid, boolean isCaptain) implements EntitySubPredicate {
	public static final MapCodec<RaiderPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.BOOL.optionalFieldOf("has_raid", Boolean.valueOf(false)).forGetter(RaiderPredicate::hasRaid),
					Codec.BOOL.optionalFieldOf("is_captain", Boolean.valueOf(false)).forGetter(RaiderPredicate::isCaptain)
				)
				.apply(instance, RaiderPredicate::new)
	);
	public static final RaiderPredicate CAPTAIN_WITHOUT_RAID = new RaiderPredicate(false, true);

	@Override
	public MapCodec<RaiderPredicate> getCodec() {
		return EntitySubPredicateTypes.RAIDER;
	}

	@Override
	public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
		return !(entity instanceof RaiderEntity raiderEntity) ? false : raiderEntity.hasRaid() == this.hasRaid && raiderEntity.isCaptain() == this.isCaptain;
	}
}
