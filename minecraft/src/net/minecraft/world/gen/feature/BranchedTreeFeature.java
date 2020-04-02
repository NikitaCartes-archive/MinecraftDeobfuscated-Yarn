package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;

public abstract class BranchedTreeFeature<T extends BranchedTreeFeatureConfig> extends AbstractTreeFeature<T> {
	public BranchedTreeFeature(Function<Dynamic<?>, ? extends T> function) {
		super(function);
	}

	public Optional<BlockPos> findPositionToGenerate(
		ModifiableTestableWorld world, int trunkHeight, int baseHeight, BlockPos pos, BranchedTreeFeatureConfig config
	) {
		BlockPos blockPos;
		if (!config.skipFluidCheck) {
			int i = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR, pos).getY();
			int j = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY();
			blockPos = new BlockPos(pos.getX(), i, pos.getZ());
			if (j - i > config.maxFluidDepth) {
				return Optional.empty();
			}
		} else {
			blockPos = pos;
		}

		if (blockPos.getY() >= 1 && blockPos.getY() + trunkHeight + 1 <= 256) {
			for (int i = 0; i <= trunkHeight + 1; i++) {
				int j = config.foliagePlacer.getRadiusForPlacement(trunkHeight, baseHeight, i);
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int k = -j; k <= j; k++) {
					int l = -j;

					while (l <= j) {
						if (i + blockPos.getY() >= 0 && i + blockPos.getY() < 256) {
							mutable.set(k + blockPos.getX(), i + blockPos.getY(), l + blockPos.getZ());
							if (canTreeReplace(world, mutable) && (config.noVines || !isVine(world, mutable))) {
								l++;
								continue;
							}

							return Optional.empty();
						}

						return Optional.empty();
					}
				}
			}

			return isDirtOrGrass(world, blockPos.down()) && blockPos.getY() < 256 - trunkHeight - 1 ? Optional.of(blockPos) : Optional.empty();
		} else {
			return Optional.empty();
		}
	}
}
