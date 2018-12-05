package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.IcebergFeatureConfig;

public class IcebergFeature extends Feature<IcebergFeatureConfig> {
	public IcebergFeature(Function<Dynamic<?>, ? extends IcebergFeatureConfig> function) {
		super(function);
	}

	public boolean method_13423(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, Random random, BlockPos blockPos, IcebergFeatureConfig icebergFeatureConfig
	) {
		blockPos = new BlockPos(blockPos.getX(), iWorld.getSeaLevel(), blockPos.getZ());
		boolean bl = random.nextDouble() > 0.7;
		BlockState blockState = icebergFeatureConfig.state;
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
						this.method_13426(iWorld, random, blockPos, l, p, r, q, s, o, bl2, j, d, bl, blockState);
					}
				}
			}
		}

		this.method_13418(iWorld, blockPos, n, l, bl2, i);

		for (int p = -o; p < o; p++) {
			for (int q = -o; q < o; q++) {
				for (int rx = -1; rx > -m; rx--) {
					int s = bl2 ? MathHelper.ceil((float)o * (1.0F - (float)Math.pow((double)rx, 2.0) / ((float)m * 8.0F))) : o;
					int t = this.method_13427(random, -rx, m, n);
					if (p < t) {
						this.method_13426(iWorld, random, blockPos, m, p, rx, q, t, s, bl2, j, d, bl, blockState);
					}
				}
			}
		}

		boolean bl3 = bl2 ? random.nextDouble() > 0.1 : random.nextDouble() > 0.7;
		if (bl3) {
			this.method_13428(random, iWorld, n, l, blockPos, bl2, i, d, j);
		}

		return true;
	}

	private void method_13428(Random random, IWorld iWorld, int i, int j, BlockPos blockPos, boolean bl, int k, double d, int l) {
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

		BlockPos blockPos2 = new BlockPos(0, 0, 0).add(m * o, 0, n * p);
		double e = bl ? d + (Math.PI / 2) : random.nextDouble() * 2.0 * Math.PI;

		for (int q = 0; q < j - 3; q++) {
			int r = this.method_13419(random, q, j, i);
			this.method_13415(r, q, blockPos, iWorld, false, e, blockPos2, k, l);
		}

		for (int q = -1; q > -j + random.nextInt(5); q--) {
			int r = this.method_13427(random, -q, j, i);
			this.method_13415(r, q, blockPos, iWorld, true, e, blockPos2, k, l);
		}
	}

	private void method_13415(int i, int j, BlockPos blockPos, IWorld iWorld, boolean bl, double d, BlockPos blockPos2, int k, int l) {
		int m = i + 1 + k / 3;
		int n = Math.min(i - 3, 3) + l / 2 - 1;

		for (int o = -m; o < m; o++) {
			for (int p = -m; p < m; p++) {
				double e = this.method_13424(o, p, blockPos2, m, n, d);
				if (e < 0.0) {
					BlockPos blockPos3 = blockPos.add(o, j, p);
					Block block = iWorld.getBlockState(blockPos3).getBlock();
					if (this.method_13420(block) || block == Blocks.field_10491) {
						if (bl) {
							this.method_13153(iWorld, blockPos3, Blocks.field_10382.getDefaultState());
						} else {
							this.method_13153(iWorld, blockPos3, Blocks.field_10124.getDefaultState());
							this.method_13422(iWorld, blockPos3);
						}
					}
				}
			}
		}
	}

	private void method_13422(IWorld iWorld, BlockPos blockPos) {
		if (iWorld.getBlockState(blockPos.up()).getBlock() == Blocks.field_10477) {
			this.method_13153(iWorld, blockPos.up(), Blocks.field_10124.getDefaultState());
		}
	}

	private void method_13426(
		IWorld iWorld, Random random, BlockPos blockPos, int i, int j, int k, int l, int m, int n, boolean bl, int o, double d, boolean bl2, BlockState blockState
	) {
		BlockPos blockPos2 = new BlockPos(0, 0, 0);
		double e = bl ? this.method_13424(j, l, blockPos2, n, this.method_13416(k, i, o), d) : this.method_13421(j, l, blockPos2, m, random);
		if (e < 0.0) {
			BlockPos blockPos3 = blockPos.add(j, k, l);
			double f = bl ? -0.5 : (double)(-6 - random.nextInt(3));
			if (e > f && random.nextDouble() > 0.9) {
				return;
			}

			this.method_13425(blockPos3, iWorld, random, i - k, i, bl, bl2, blockState);
		}
	}

	private void method_13425(BlockPos blockPos, IWorld iWorld, Random random, int i, int j, boolean bl, boolean bl2, BlockState blockState) {
		BlockState blockState2 = iWorld.getBlockState(blockPos);
		Block block = blockState2.getBlock();
		if (blockState2.getMaterial() == Material.AIR || block == Blocks.field_10491 || block == Blocks.field_10295 || block == Blocks.field_10382) {
			boolean bl3 = !bl || random.nextDouble() > 0.05;
			int k = bl ? 3 : 2;
			if (bl2 && block != Blocks.field_10382 && (double)i <= (double)random.nextInt(Math.max(1, j / k)) + (double)j * 0.6 && bl3) {
				this.method_13153(iWorld, blockPos, Blocks.field_10491.getDefaultState());
			} else {
				this.method_13153(iWorld, blockPos, blockState);
			}
		}
	}

	private int method_13416(int i, int j, int k) {
		int l = k;
		if (i > 0 && j - i <= 3) {
			l = k - (4 - (j - i));
		}

		return l;
	}

	private double method_13421(int i, int j, BlockPos blockPos, int k, Random random) {
		float f = 10.0F * MathHelper.clamp(random.nextFloat(), 0.2F, 0.8F) / (float)k;
		return (double)f + Math.pow((double)(i - blockPos.getX()), 2.0) + Math.pow((double)(j - blockPos.getZ()), 2.0) - Math.pow((double)k, 2.0);
	}

	private double method_13424(int i, int j, BlockPos blockPos, int k, int l, double d) {
		return Math.pow(((double)(i - blockPos.getX()) * Math.cos(d) - (double)(j - blockPos.getZ()) * Math.sin(d)) / (double)k, 2.0)
			+ Math.pow(((double)(i - blockPos.getX()) * Math.sin(d) + (double)(j - blockPos.getZ()) * Math.cos(d)) / (double)l, 2.0)
			- 1.0;
	}

	private int method_13419(Random random, int i, int j, int k) {
		float f = 3.5F - random.nextFloat();
		float g = (1.0F - (float)Math.pow((double)i, 2.0) / ((float)j * f)) * (float)k;
		if (j > 15 + random.nextInt(5)) {
			int l = i < 3 + random.nextInt(6) ? i / 2 : i;
			g = (1.0F - (float)l / ((float)j * f * 0.4F)) * (float)k;
		}

		return MathHelper.ceil(g / 2.0F);
	}

	private int method_13417(int i, int j, int k) {
		float f = 1.0F;
		float g = (1.0F - (float)Math.pow((double)i, 2.0) / ((float)j * 1.0F)) * (float)k;
		return MathHelper.ceil(g / 2.0F);
	}

	private int method_13427(Random random, int i, int j, int k) {
		float f = 1.0F + random.nextFloat() / 2.0F;
		float g = (1.0F - (float)i / ((float)j * f)) * (float)k;
		return MathHelper.ceil(g / 2.0F);
	}

	private boolean method_13420(Block block) {
		return block == Blocks.field_10225 || block == Blocks.field_10491 || block == Blocks.field_10384;
	}

	private boolean method_13414(BlockView blockView, BlockPos blockPos) {
		return blockView.getBlockState(blockPos.down()).getMaterial() == Material.AIR;
	}

	private void method_13418(IWorld iWorld, BlockPos blockPos, int i, int j, boolean bl, int k) {
		int l = bl ? k : i / 2;

		for (int m = -l; m <= l; m++) {
			for (int n = -l; n <= l; n++) {
				for (int o = 0; o <= j; o++) {
					BlockPos blockPos2 = blockPos.add(m, o, n);
					Block block = iWorld.getBlockState(blockPos2).getBlock();
					if (this.method_13420(block) || block == Blocks.field_10477) {
						if (this.method_13414(iWorld, blockPos2)) {
							this.method_13153(iWorld, blockPos2, Blocks.field_10124.getDefaultState());
							this.method_13153(iWorld, blockPos2.up(), Blocks.field_10124.getDefaultState());
						} else if (this.method_13420(block)) {
							Block[] blocks = new Block[]{
								iWorld.getBlockState(blockPos2.west()).getBlock(),
								iWorld.getBlockState(blockPos2.east()).getBlock(),
								iWorld.getBlockState(blockPos2.north()).getBlock(),
								iWorld.getBlockState(blockPos2.south()).getBlock()
							};
							int p = 0;

							for (Block block2 : blocks) {
								if (!this.method_13420(block2)) {
									p++;
								}
							}

							if (p >= 3) {
								this.method_13153(iWorld, blockPos2, Blocks.field_10124.getDefaultState());
							}
						}
					}
				}
			}
		}
	}
}
