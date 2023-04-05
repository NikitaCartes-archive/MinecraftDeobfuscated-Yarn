package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class IcebergFeature extends Feature<SingleStateFeatureConfig> {
	public IcebergFeature(Codec<SingleStateFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SingleStateFeatureConfig> context) {
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		blockPos = new BlockPos(blockPos.getX(), context.getGenerator().getSeaLevel(), blockPos.getZ());
		Random random = context.getRandom();
		boolean bl = random.nextDouble() > 0.7;
		BlockState blockState = context.getConfig().state;
		double d = random.nextDouble() * 2.0 * Math.PI;
		int i = 11 - random.nextInt(5);
		int j = 3 + random.nextInt(3);
		boolean bl2 = random.nextDouble() > 0.7;
		int k = 11;
		int l = bl2 ? random.nextInt(6) + 6 : random.nextInt(15) + 3;
		if (!bl2 && random.nextDouble() > 0.9) {
			l += random.nextInt(19) + 7;
		}

		int m = Math.min(l + random.nextInt(11), 18);
		int n = Math.min(l + random.nextInt(7) - random.nextInt(5), 11);
		int o = bl2 ? i : 11;

		for (int p = -o; p < o; p++) {
			for (int q = -o; q < o; q++) {
				for (int r = 0; r < l; r++) {
					int s = bl2 ? this.method_13417(r, l, n) : this.method_13419(random, r, l, n);
					if (bl2 || p < s) {
						this.placeAt(structureWorldAccess, random, blockPos, l, p, r, q, s, o, bl2, j, d, bl, blockState);
					}
				}
			}
		}

		this.method_13418(structureWorldAccess, blockPos, n, l, bl2, i);

		for (int p = -o; p < o; p++) {
			for (int q = -o; q < o; q++) {
				for (int rx = -1; rx > -m; rx--) {
					int s = bl2 ? MathHelper.ceil((float)o * (1.0F - (float)Math.pow((double)rx, 2.0) / ((float)m * 8.0F))) : o;
					int t = this.method_13427(random, -rx, m, n);
					if (p < t) {
						this.placeAt(structureWorldAccess, random, blockPos, m, p, rx, q, t, s, bl2, j, d, bl, blockState);
					}
				}
			}
		}

		boolean bl3 = bl2 ? random.nextDouble() > 0.1 : random.nextDouble() > 0.7;
		if (bl3) {
			this.method_13428(random, structureWorldAccess, n, l, blockPos, bl2, i, d, j);
		}

		return true;
	}

	private void method_13428(Random random, WorldAccess world, int i, int j, BlockPos pos, boolean bl, int k, double d, int l) {
		int m = random.nextBoolean() ? -1 : 1;
		int n = random.nextBoolean() ? -1 : 1;
		int o = random.nextInt(Math.max(i / 2 - 2, 1));
		if (random.nextBoolean()) {
			o = i / 2 + 1 - random.nextInt(Math.max(i - i / 2 - 1, 1));
		}

		int p = random.nextInt(Math.max(i / 2 - 2, 1));
		if (random.nextBoolean()) {
			p = i / 2 + 1 - random.nextInt(Math.max(i - i / 2 - 1, 1));
		}

		if (bl) {
			o = p = random.nextInt(Math.max(k - 5, 1));
		}

		BlockPos blockPos = new BlockPos(m * o, 0, n * p);
		double e = bl ? d + (Math.PI / 2) : random.nextDouble() * 2.0 * Math.PI;

		for (int q = 0; q < j - 3; q++) {
			int r = this.method_13419(random, q, j, i);
			this.method_13415(r, q, pos, world, false, e, blockPos, k, l);
		}

		for (int q = -1; q > -j + random.nextInt(5); q--) {
			int r = this.method_13427(random, -q, j, i);
			this.method_13415(r, q, pos, world, true, e, blockPos, k, l);
		}
	}

	private void method_13415(int i, int y, BlockPos pos, WorldAccess world, boolean placeWater, double d, BlockPos blockPos, int j, int k) {
		int l = i + 1 + j / 3;
		int m = Math.min(i - 3, 3) + k / 2 - 1;

		for (int n = -l; n < l; n++) {
			for (int o = -l; o < l; o++) {
				double e = this.getDistance(n, o, blockPos, l, m, d);
				if (e < 0.0) {
					BlockPos blockPos2 = pos.add(n, y, o);
					BlockState blockState = world.getBlockState(blockPos2);
					if (isSnowOrIce(blockState) || blockState.isOf(Blocks.SNOW_BLOCK)) {
						if (placeWater) {
							this.setBlockState(world, blockPos2, Blocks.WATER.getDefaultState());
						} else {
							this.setBlockState(world, blockPos2, Blocks.AIR.getDefaultState());
							this.clearSnowAbove(world, blockPos2);
						}
					}
				}
			}
		}
	}

	private void clearSnowAbove(WorldAccess world, BlockPos pos) {
		if (world.getBlockState(pos.up()).isOf(Blocks.SNOW)) {
			this.setBlockState(world, pos.up(), Blocks.AIR.getDefaultState());
		}
	}

	private void placeAt(
		WorldAccess world,
		Random random,
		BlockPos pos,
		int height,
		int offsetX,
		int offsetY,
		int offsetZ,
		int i,
		int j,
		boolean bl,
		int k,
		double randomSine,
		boolean placeSnow,
		BlockState state
	) {
		double d = bl
			? this.getDistance(offsetX, offsetZ, BlockPos.ORIGIN, j, this.decreaseValueNearTop(offsetY, height, k), randomSine)
			: this.method_13421(offsetX, offsetZ, BlockPos.ORIGIN, i, random);
		if (d < 0.0) {
			BlockPos blockPos = pos.add(offsetX, offsetY, offsetZ);
			double e = bl ? -0.5 : (double)(-6 - random.nextInt(3));
			if (d > e && random.nextDouble() > 0.9) {
				return;
			}

			this.placeBlockOrSnow(blockPos, world, random, height - offsetY, height, bl, placeSnow, state);
		}
	}

	private void placeBlockOrSnow(
		BlockPos pos, WorldAccess world, Random random, int heightRemaining, int height, boolean lessSnow, boolean placeSnow, BlockState state
	) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.isAir() || blockState.isOf(Blocks.SNOW_BLOCK) || blockState.isOf(Blocks.ICE) || blockState.isOf(Blocks.WATER)) {
			boolean bl = !lessSnow || random.nextDouble() > 0.05;
			int i = lessSnow ? 3 : 2;
			if (placeSnow && !blockState.isOf(Blocks.WATER) && (double)heightRemaining <= (double)random.nextInt(Math.max(1, height / i)) + (double)height * 0.6 && bl) {
				this.setBlockState(world, pos, Blocks.SNOW_BLOCK.getDefaultState());
			} else {
				this.setBlockState(world, pos, state);
			}
		}
	}

	private int decreaseValueNearTop(int y, int height, int value) {
		int i = value;
		if (y > 0 && height - y <= 3) {
			i = value - (4 - (height - y));
		}

		return i;
	}

	private double method_13421(int x, int z, BlockPos pos, int i, Random random) {
		float f = 10.0F * MathHelper.clamp(random.nextFloat(), 0.2F, 0.8F) / (float)i;
		return (double)f + Math.pow((double)(x - pos.getX()), 2.0) + Math.pow((double)(z - pos.getZ()), 2.0) - Math.pow((double)i, 2.0);
	}

	private double getDistance(int x, int z, BlockPos pos, int divisor1, int divisor2, double randomSine) {
		return Math.pow(((double)(x - pos.getX()) * Math.cos(randomSine) - (double)(z - pos.getZ()) * Math.sin(randomSine)) / (double)divisor1, 2.0)
			+ Math.pow(((double)(x - pos.getX()) * Math.sin(randomSine) + (double)(z - pos.getZ()) * Math.cos(randomSine)) / (double)divisor2, 2.0)
			- 1.0;
	}

	private int method_13419(Random random, int y, int height, int factor) {
		float f = 3.5F - random.nextFloat();
		float g = (1.0F - (float)Math.pow((double)y, 2.0) / ((float)height * f)) * (float)factor;
		if (height > 15 + random.nextInt(5)) {
			int i = y < 3 + random.nextInt(6) ? y / 2 : y;
			g = (1.0F - (float)i / ((float)height * f * 0.4F)) * (float)factor;
		}

		return MathHelper.ceil(g / 2.0F);
	}

	private int method_13417(int y, int height, int factor) {
		float f = 1.0F;
		float g = (1.0F - (float)Math.pow((double)y, 2.0) / ((float)height * 1.0F)) * (float)factor;
		return MathHelper.ceil(g / 2.0F);
	}

	private int method_13427(Random random, int y, int height, int factor) {
		float f = 1.0F + random.nextFloat() / 2.0F;
		float g = (1.0F - (float)y / ((float)height * f)) * (float)factor;
		return MathHelper.ceil(g / 2.0F);
	}

	private static boolean isSnowOrIce(BlockState state) {
		return state.isOf(Blocks.PACKED_ICE) || state.isOf(Blocks.SNOW_BLOCK) || state.isOf(Blocks.BLUE_ICE);
	}

	private boolean isAirBelow(BlockView world, BlockPos pos) {
		return world.getBlockState(pos.down()).isAir();
	}

	private void method_13418(WorldAccess world, BlockPos pos, int i, int height, boolean bl, int j) {
		int k = bl ? j : i / 2;

		for (int l = -k; l <= k; l++) {
			for (int m = -k; m <= k; m++) {
				for (int n = 0; n <= height; n++) {
					BlockPos blockPos = pos.add(l, n, m);
					BlockState blockState = world.getBlockState(blockPos);
					if (isSnowOrIce(blockState) || blockState.isOf(Blocks.SNOW)) {
						if (this.isAirBelow(world, blockPos)) {
							this.setBlockState(world, blockPos, Blocks.AIR.getDefaultState());
							this.setBlockState(world, blockPos.up(), Blocks.AIR.getDefaultState());
						} else if (isSnowOrIce(blockState)) {
							BlockState[] blockStates = new BlockState[]{
								world.getBlockState(blockPos.west()),
								world.getBlockState(blockPos.east()),
								world.getBlockState(blockPos.north()),
								world.getBlockState(blockPos.south())
							};
							int o = 0;

							for (BlockState blockState2 : blockStates) {
								if (!isSnowOrIce(blockState2)) {
									o++;
								}
							}

							if (o >= 3) {
								this.setBlockState(world, blockPos, Blocks.AIR.getDefaultState());
							}
						}
					}
				}
			}
		}
	}
}
