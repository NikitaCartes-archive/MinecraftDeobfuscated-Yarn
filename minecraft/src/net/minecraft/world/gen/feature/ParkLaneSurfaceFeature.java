package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ParkLaneSurfaceFeature extends Feature<DefaultFeatureConfig> {
	public ParkLaneSurfaceFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		ChunkPos chunkPos = new ChunkPos(blockPos);
		blockPos = chunkPos.getBlockPos(7, 0, 7);
		blockPos = blockPos.withY(structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos).getY()).down();
		if (blockPos.getY() >= 8 && structureWorldAccess.getBiome(blockPos).matchesKey(BiomeKeys.ARBORETUM)) {
			Direction[] directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
			boolean[] bls = new boolean[]{false, false, false, false};
			int i = 0;

			for (int j = 0; j < 4; j++) {
				if (this.method_59250(structureWorldAccess, chunkPos, blockPos, directions[j])) {
					i++;
					bls[j] = !this.method_59251(structureWorldAccess, blockPos, directions[j]);
				}
			}

			if (i == 0) {
				return false;
			} else {
				int jx = i == 4 ? 1 : 0;
				int k = structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos).getY();

				for (int l = -1 - jx; l <= 2 + jx; l++) {
					for (int m = -1 - jx; m <= 2 + jx; m++) {
						BlockPos blockPos2 = blockPos.add(l, 0, m);
						BlockPos blockPos3 = structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos2).down();
						if (!this.method_59255(structureWorldAccess, blockPos3)) {
							return false;
						}
					}
				}

				for (int l = -1 - jx; l <= 2 + jx; l++) {
					for (int mx = -1 - jx; mx <= 2 + jx; mx++) {
						BlockPos blockPos2 = blockPos.add(l, 0, mx);
						this.method_59257(structureWorldAccess, blockPos2.withY(k - 1), null, false);
					}
				}

				for (int l = 0; l < 4; l++) {
					if (bls[l]) {
						this.method_59253(structureWorldAccess, blockPos, directions[l], jx == 1);
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	private boolean method_59255(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		BlockState blockState = structureWorldAccess.getBlockState(blockPos);
		return blockState.isOf(Blocks.PEELGRASS_BLOCK)
			|| blockState.isOf(Blocks.POISON_PATH)
			|| blockState.isOf(Blocks.POTATO_PLANKS)
			|| blockState.isOf(Blocks.LANTERN)
			|| blockState.isOf(Blocks.POTATO_FENCE);
	}

	private boolean method_59250(StructureWorldAccess structureWorldAccess, ChunkPos chunkPos, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction, 16);
		if (!structureWorldAccess.getBiome(blockPos2).matchesKey(BiomeKeys.ARBORETUM)) {
			return false;
		} else {
			int i = structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos).getY();
			int j = structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos2).getY();
			if (Math.abs(i - j) > 10) {
				return false;
			} else {
				long l = structureWorldAccess.getSeed();
				ChunkPos chunkPos2 = chunkPos;
				blockPos.offset(Direction.NORTH);
				if (direction == Direction.NORTH || direction == Direction.EAST) {
					chunkPos2 = new ChunkPos(chunkPos.x + direction.getOffsetX(), chunkPos.z + direction.getOffsetZ());
				}

				l += (long)chunkPos2.hashCode();
				Random random = new Random(l);
				if (direction == Direction.NORTH || direction == Direction.SOUTH) {
					random.nextFloat();
				}

				boolean bl = random.nextFloat() < 0.7F;
				if (!bl) {
					return false;
				} else {
					for (int k = -1; k <= 2; k++) {
						for (int m = -2; m < 18; m++) {
							BlockPos blockPos3 = this.method_59252(structureWorldAccess, blockPos, direction, k, m);
							if (!this.method_59255(structureWorldAccess, blockPos3)) {
								return false;
							}
						}
					}

					return true;
				}
			}
		}
	}

	private boolean method_59251(StructureWorldAccess structureWorldAccess, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction, 16);
		int i = structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos).getY();
		int j = structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos2).getY();
		BlockPos blockPos3 = blockPos.offset(direction, 4);
		blockPos3 = structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos3).down();
		return !structureWorldAccess.getBlockState(blockPos3).isOf(Blocks.POISON_PATH) && !structureWorldAccess.getBlockState(blockPos3).isOf(Blocks.POTATO_PLANKS)
			? j < i
			: true;
	}

	private void method_59253(StructureWorldAccess structureWorldAccess, BlockPos blockPos, Direction direction, boolean bl) {
		if (this.method_59256(structureWorldAccess, blockPos, direction)) {
			for (int i = -1; i <= 2; i++) {
				Direction direction2 = null;
				if (i == -1 || i == 2) {
					direction2 = this.method_59254(direction, i < 0);
				}

				boolean bl2 = false;

				for (int j = 2; j < 14; j++) {
					if (bl2 && (double)structureWorldAccess.getRandom().nextFloat() < 0.3) {
						bl2 = false;
					}

					BlockPos blockPos2 = this.method_59252(structureWorldAccess, blockPos, direction, i, j);
					bl2 = this.method_59257(structureWorldAccess, blockPos2, (!bl || j != 2) && j != 13 ? direction2 : null, bl2);
				}
			}
		} else {
			int i = blockPos.getY();
			int k = structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos.offset(direction, 16)).getY() - 1;

			for (int l = -1; l <= 2; l++) {
				Direction direction3 = null;
				if (l == -1 || l == 2) {
					direction3 = this.method_59254(direction, l < 0);
				}

				boolean bl3 = false;

				for (int m = 2; m < 14; m++) {
					if (bl3 && (double)structureWorldAccess.getRandom().nextFloat() < 0.3) {
						bl3 = false;
					}

					int n = Math.round(MathHelper.lerp(((float)m - 2.0F) / 12.0F, (float)i, (float)k));
					BlockPos blockPos3 = this.method_59252(structureWorldAccess, blockPos, direction, l, m).withY(n);
					bl3 = this.method_59257(structureWorldAccess, blockPos3, (!bl || m != 2) && m != 13 ? direction3 : null, bl3);
				}
			}
		}
	}

	@Nullable
	private Direction method_59254(Direction direction, boolean bl) {
		if (direction.getAxis() == Direction.NORTH.getAxis()) {
			return bl ? Direction.WEST : Direction.EAST;
		} else if (direction.getAxis() == Direction.EAST.getAxis()) {
			return bl ? Direction.NORTH : Direction.SOUTH;
		} else {
			return null;
		}
	}

	private BlockPos method_59252(StructureWorldAccess structureWorldAccess, BlockPos blockPos, Direction direction, int i, int j) {
		Vec3i vec3i = direction.getVector();
		BlockPos blockPos2 = blockPos.add(
			(vec3i.getX() > 0 ? 1 : 0) + vec3i.getX() * j + Math.abs(vec3i.getZ()) * i, 0, (vec3i.getZ() > 0 ? 1 : 0) + vec3i.getZ() * j + Math.abs(vec3i.getX()) * i
		);
		return structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos2).down();
	}

	private boolean method_59256(StructureWorldAccess structureWorldAccess, BlockPos blockPos, Direction direction) {
		int i = blockPos.getY();

		for (int j = -1; j <= 2; j++) {
			int k = i;

			for (int l = 2; l < 14; l++) {
				int m = this.method_59252(structureWorldAccess, blockPos, direction, j, l).getY();
				if (Math.abs(k - m) > 1) {
					return false;
				}

				k = m;
			}
		}

		return true;
	}

	private boolean method_59257(StructureWorldAccess structureWorldAccess, BlockPos blockPos, @Nullable Direction direction, boolean bl) {
		int i = structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos).getY() - 1;
		int j = Math.min(i, blockPos.getY() + 10);

		for (int k = blockPos.getY() + 1; k <= j; k++) {
			structureWorldAccess.setBlockState(blockPos.withY(k), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
		}

		boolean bl2 = structureWorldAccess.getBlockState(blockPos.down()).isAir();
		structureWorldAccess.setBlockState(blockPos, (bl2 ? Blocks.POTATO_PLANKS : Blocks.POISON_PATH).getDefaultState(), Block.NOTIFY_ALL);
		if (direction != null) {
			BlockPos blockPos2 = blockPos.up();
			BlockPos blockPos3 = blockPos2.offset(direction);
			if (bl2 && structureWorldAccess.getBlockState(blockPos3).isAir()) {
				structureWorldAccess.setBlockState(blockPos2, Blocks.POTATO_FENCE.getDefaultState(), Block.NOTIFY_ALL);
				structureWorldAccess.getChunk(blockPos2).markBlockForPostProcessing(blockPos2);
				return true;
			}

			if (structureWorldAccess.getBlockState(blockPos3).isAir()
				&& this.method_59255(structureWorldAccess, blockPos.offset(direction))
				&& (bl || structureWorldAccess.getRandom().nextFloat() < 0.6F)) {
				structureWorldAccess.setBlockState(blockPos3, Blocks.POTATO_FENCE.getDefaultState(), Block.NOTIFY_ALL);
				structureWorldAccess.getChunk(blockPos3).markBlockForPostProcessing(blockPos3);
				if (structureWorldAccess.getRandom().nextFloat() < 0.1F) {
					structureWorldAccess.setBlockState(blockPos3.up(), Blocks.LANTERN.getDefaultState(), Block.NOTIFY_ALL);
				}

				return true;
			}
		}

		return false;
	}
}
