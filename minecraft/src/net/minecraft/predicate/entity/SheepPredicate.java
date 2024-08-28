package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;

public record SheepPredicate(Optional<Boolean> sheared, Optional<DyeColor> color) implements EntitySubPredicate {
	public static final MapCodec<SheepPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.BOOL.optionalFieldOf("sheared").forGetter(SheepPredicate::sheared), DyeColor.CODEC.optionalFieldOf("color").forGetter(SheepPredicate::color)
				)
				.apply(instance, SheepPredicate::new)
	);

	@Override
	public MapCodec<SheepPredicate> getCodec() {
		return EntitySubPredicateTypes.SHEEP;
	}

	@Override
	public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
		if (entity instanceof SheepEntity sheepEntity) {
			return this.sheared.isPresent() && sheepEntity.isSheared() != this.sheared.get()
				? false
				: !this.color.isPresent() || sheepEntity.getColor() == this.color.get();
		} else {
			return false;
		}
	}

	public static SheepPredicate unsheared(DyeColor color) {
		return new SheepPredicate(Optional.of(false), Optional.of(color));
	}
}
