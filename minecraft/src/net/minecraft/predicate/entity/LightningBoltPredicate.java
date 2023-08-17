package net.minecraft.predicate.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;

public record LightningBoltPredicate(NumberRange.IntRange blocksSetOnFire, Optional<EntityPredicate> entityStruck) implements TypeSpecificPredicate {
	public static final MapCodec<LightningBoltPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "blocks_set_on_fire", NumberRange.IntRange.ANY)
						.forGetter(LightningBoltPredicate::blocksSetOnFire),
					Codecs.createStrictOptionalFieldCodec(EntityPredicate.CODEC, "entity_struck").forGetter(LightningBoltPredicate::entityStruck)
				)
				.apply(instance, LightningBoltPredicate::new)
	);

	public static LightningBoltPredicate of(NumberRange.IntRange blocksSetOnFire) {
		return new LightningBoltPredicate(blocksSetOnFire, Optional.empty());
	}

	@Override
	public TypeSpecificPredicate.Type type() {
		return TypeSpecificPredicate.Deserializers.LIGHTNING;
	}

	@Override
	public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
		return !(entity instanceof LightningEntity lightningEntity)
			? false
			: this.blocksSetOnFire.test(lightningEntity.getBlocksSetOnFire())
				&& (
					this.entityStruck.isEmpty()
						|| lightningEntity.getStruckEntities().anyMatch(struckEntity -> ((EntityPredicate)this.entityStruck.get()).test(world, pos, struckEntity))
				);
	}
}
