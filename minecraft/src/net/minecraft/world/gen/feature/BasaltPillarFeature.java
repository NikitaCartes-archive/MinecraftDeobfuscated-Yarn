package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BasaltPillarFeature extends Feature<DefaultFeatureConfig> {
	public BasaltPillarFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean method_24433(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (structureWorldAccess.isAir(blockPos) && !structureWorldAccess.isAir(blockPos.up())) {
			BlockPos.Mutable mutable = blockPos.mutableCopy();
			BlockPos.Mutable mutable2 = blockPos.mutableCopy();
			boolean bl = true;
			boolean bl2 = true;
			boolean bl3 = true;
			boolean bl4 = true;

			while (structureWorldAccess.isAir(mutable)) {
				if (World.isHeightInvalid(mutable)) {
					return true;
				}

				structureWorldAccess.setBlockState(mutable, Blocks.field_22091.getDefaultState(), 2);
				bl = bl && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.field_11043));
				bl2 = bl2 && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.field_11035));
				bl3 = bl3 && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.field_11039));
				bl4 = bl4 && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.field_11034));
				mutable.move(Direction.field_11033);
			}

			mutable.move(Direction.field_11036);
			this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.field_11043));
			this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.field_11035));
			this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.field_11039));
			this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.field_11034));
			mutable.move(Direction.field_11033);
			BlockPos.Mutable mutable3 = new BlockPos.Mutable();

			for (int i = -3; i < 4; i++) {
				for (int j = -3; j < 4; j++) {
					int k = MathHelper.abs(i) * MathHelper.abs(j);
					if (random.nextInt(10) < 10 - k) {
						mutable3.set(mutable.add(i, 0, j));
						int l = 3;

						while (structureWorldAccess.isAir(mutable2.set(mutable3, Direction.field_11033))) {
							mutable3.move(Direction.field_11033);
							if (--l <= 0) {
								break;
							}
						}

						if (!structureWorldAccess.isAir(mutable2.set(mutable3, Direction.field_11033))) {
							structureWorldAccess.setBlockState(mutable3, Blocks.field_22091.getDefaultState(), 2);
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private void tryPlaceBasalt(WorldAccess world, Random random, BlockPos pos) {
		if (random.nextBoolean()) {
			world.setBlockState(pos, Blocks.field_22091.getDefaultState(), 2);
		}
	}

	private boolean stopOrPlaceBasalt(WorldAccess world, Random random, BlockPos pos) {
		if (random.nextInt(10) != 0) {
			world.setBlockState(pos, Blocks.field_22091.getDefaultState(), 2);
			return true;
		} else {
			return false;
		}
	}
}
