package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;

public record FishingHookPredicate(Optional<Boolean> inOpenWater) implements TypeSpecificPredicate {
	public static final FishingHookPredicate ALL = new FishingHookPredicate(Optional.empty());
	public static final MapCodec<FishingHookPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "in_open_water").forGetter(FishingHookPredicate::inOpenWater))
				.apply(instance, FishingHookPredicate::new)
	);

	public static FishingHookPredicate of(boolean inOpenWater) {
		return new FishingHookPredicate(Optional.of(inOpenWater));
	}

	@Override
	public TypeSpecificPredicate.Type type() {
		return TypeSpecificPredicate.Deserializers.FISHING_HOOK;
	}

	@Override
	public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
		if (this.inOpenWater.isEmpty()) {
			return true;
		} else {
			return entity instanceof FishingBobberEntity fishingBobberEntity ? (Boolean)this.inOpenWater.get() == fishingBobberEntity.isInOpenWater() : false;
		}
	}
}
