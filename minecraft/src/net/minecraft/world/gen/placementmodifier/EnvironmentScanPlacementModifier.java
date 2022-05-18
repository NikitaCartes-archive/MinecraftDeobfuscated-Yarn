package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public class EnvironmentScanPlacementModifier extends PlacementModifier {
	private final Direction direction;
	private final BlockPredicate targetPredicate;
	private final BlockPredicate allowedSearchPredicate;
	private final int maxSteps;
	public static final Codec<EnvironmentScanPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Direction.VERTICAL_CODEC.fieldOf("direction_of_search").forGetter(environmentScanPlacementModifier -> environmentScanPlacementModifier.direction),
					BlockPredicate.BASE_CODEC.fieldOf("target_condition").forGetter(environmentScanPlacementModifier -> environmentScanPlacementModifier.targetPredicate),
					BlockPredicate.BASE_CODEC
						.optionalFieldOf("allowed_search_condition", BlockPredicate.alwaysTrue())
						.forGetter(environmentScanPlacementModifier -> environmentScanPlacementModifier.allowedSearchPredicate),
					Codec.intRange(1, 32).fieldOf("max_steps").forGetter(environmentScanPlacementModifier -> environmentScanPlacementModifier.maxSteps)
				)
				.apply(instance, EnvironmentScanPlacementModifier::new)
	);

	private EnvironmentScanPlacementModifier(Direction direction, BlockPredicate targetPredicate, BlockPredicate allowedSearchPredicate, int maxSteps) {
		this.direction = direction;
		this.targetPredicate = targetPredicate;
		this.allowedSearchPredicate = allowedSearchPredicate;
		this.maxSteps = maxSteps;
	}

	public static EnvironmentScanPlacementModifier of(Direction direction, BlockPredicate targetPredicate, BlockPredicate allowedSearchPredicate, int maxSteps) {
		return new EnvironmentScanPlacementModifier(direction, targetPredicate, allowedSearchPredicate, maxSteps);
	}

	public static EnvironmentScanPlacementModifier of(Direction direction, BlockPredicate targetPredicate, int maxSteps) {
		return of(direction, targetPredicate, BlockPredicate.alwaysTrue(), maxSteps);
	}

	@Override
	public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
		BlockPos.Mutable mutable = pos.mutableCopy();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		if (!this.allowedSearchPredicate.test(structureWorldAccess, mutable)) {
			return Stream.of();
		} else {
			for (int i = 0; i < this.maxSteps; i++) {
				if (this.targetPredicate.test(structureWorldAccess, mutable)) {
					return Stream.of(mutable);
				}

				mutable.move(this.direction);
				if (structureWorldAccess.isOutOfHeightLimit(mutable.getY())) {
					return Stream.of();
				}

				if (!this.allowedSearchPredicate.test(structureWorldAccess, mutable)) {
					break;
				}
			}

			return this.targetPredicate.test(structureWorldAccess, mutable) ? Stream.of(mutable) : Stream.of();
		}
	}

	@Override
	public PlacementModifierType<?> getType() {
		return PlacementModifierType.ENVIRONMENT_SCAN;
	}
}
