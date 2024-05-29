package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public record LocationCheckLootCondition(Optional<LocationPredicate> predicate, BlockPos offset) implements LootCondition {
	private static final MapCodec<BlockPos> OFFSET_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.INT.optionalFieldOf("offsetX", Integer.valueOf(0)).forGetter(Vec3i::getX),
					Codec.INT.optionalFieldOf("offsetY", Integer.valueOf(0)).forGetter(Vec3i::getY),
					Codec.INT.optionalFieldOf("offsetZ", Integer.valueOf(0)).forGetter(Vec3i::getZ)
				)
				.apply(instance, BlockPos::new)
	);
	public static final MapCodec<LocationCheckLootCondition> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					LocationPredicate.CODEC.optionalFieldOf("predicate").forGetter(LocationCheckLootCondition::predicate),
					OFFSET_CODEC.forGetter(LocationCheckLootCondition::offset)
				)
				.apply(instance, LocationCheckLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.LOCATION_CHECK;
	}

	public boolean test(LootContext lootContext) {
		Vec3d vec3d = lootContext.get(LootContextParameters.ORIGIN);
		return vec3d != null
			&& (
				this.predicate.isEmpty()
					|| ((LocationPredicate)this.predicate.get())
						.test(
							lootContext.getWorld(), vec3d.getX() + (double)this.offset.getX(), vec3d.getY() + (double)this.offset.getY(), vec3d.getZ() + (double)this.offset.getZ()
						)
			);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return Set.of(LootContextParameters.ORIGIN);
	}

	public static LootCondition.Builder builder(LocationPredicate.Builder predicateBuilder) {
		return () -> new LocationCheckLootCondition(Optional.of(predicateBuilder.build()), BlockPos.ORIGIN);
	}

	public static LootCondition.Builder builder(LocationPredicate.Builder predicateBuilder, BlockPos pos) {
		return () -> new LocationCheckLootCondition(Optional.of(predicateBuilder.build()), pos);
	}
}
