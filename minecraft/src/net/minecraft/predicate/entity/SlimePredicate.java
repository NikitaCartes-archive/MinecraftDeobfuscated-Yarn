package net.minecraft.predicate.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;

public record SlimePredicate(NumberRange.IntRange size) implements EntitySubPredicate {
	public static final MapCodec<SlimePredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "size", NumberRange.IntRange.ANY).forGetter(SlimePredicate::size)
				)
				.apply(instance, SlimePredicate::new)
	);

	public static SlimePredicate of(NumberRange.IntRange size) {
		return new SlimePredicate(size);
	}

	@Override
	public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
		return entity instanceof SlimeEntity slimeEntity ? this.size.test(slimeEntity.getSize()) : false;
	}

	@Override
	public MapCodec<SlimePredicate> getCodec() {
		return EntitySubPredicateTypes.SLIME;
	}
}
