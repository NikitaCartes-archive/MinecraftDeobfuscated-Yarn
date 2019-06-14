package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class ChorusFlowerBlock extends Block {
	public static final IntProperty field_10762 = Properties.field_12482;
	private final ChorusPlantBlock field_10763;

	protected ChorusFlowerBlock(ChorusPlantBlock chorusPlantBlock, Block.Settings settings) {
		super(settings);
		this.field_10763 = chorusPlantBlock;
		this.method_9590(this.field_10647.method_11664().method_11657(field_10762, Integer.valueOf(0)));
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(world, blockPos)) {
			world.breakBlock(blockPos, true);
		} else {
			BlockPos blockPos2 = blockPos.up();
			if (world.isAir(blockPos2) && blockPos2.getY() < 256) {
				int i = (Integer)blockState.method_11654(field_10762);
				if (i < 5) {
					boolean bl = false;
					boolean bl2 = false;
					BlockState blockState2 = world.method_8320(blockPos.down());
					Block block = blockState2.getBlock();
					if (block == Blocks.field_10471) {
						bl = true;
					} else if (block == this.field_10763) {
						int j = 1;

						for (int k = 0; k < 4; k++) {
							Block block2 = world.method_8320(blockPos.down(j + 1)).getBlock();
							if (block2 != this.field_10763) {
								if (block2 == Blocks.field_10471) {
									bl2 = true;
								}
								break;
							}

							j++;
						}

						if (j < 2 || j <= random.nextInt(bl2 ? 5 : 4)) {
							bl = true;
						}
					} else if (blockState2.isAir()) {
						bl = true;
					}

					if (bl && isSurroundedByAir(world, blockPos2, null) && world.isAir(blockPos.up(2))) {
						world.method_8652(blockPos, this.field_10763.method_9759(world, blockPos), 2);
						this.grow(world, blockPos2, i);
					} else if (i < 4) {
						int j = random.nextInt(4);
						if (bl2) {
							j++;
						}

						boolean bl3 = false;

						for (int l = 0; l < j; l++) {
							Direction direction = Direction.Type.field_11062.random(random);
							BlockPos blockPos3 = blockPos.offset(direction);
							if (world.isAir(blockPos3) && world.isAir(blockPos3.down()) && isSurroundedByAir(world, blockPos3, direction.getOpposite())) {
								this.grow(world, blockPos3, i + 1);
								bl3 = true;
							}
						}

						if (bl3) {
							world.method_8652(blockPos, this.field_10763.method_9759(world, blockPos), 2);
						} else {
							this.die(world, blockPos);
						}
					} else {
						this.die(world, blockPos);
					}
				}
			}
		}
	}

	private void grow(World world, BlockPos blockPos, int i) {
		world.method_8652(blockPos, this.method_9564().method_11657(field_10762, Integer.valueOf(i)), 2);
		world.playLevelEvent(1033, blockPos, 0);
	}

	private void die(World world, BlockPos blockPos) {
		world.method_8652(blockPos, this.method_9564().method_11657(field_10762, Integer.valueOf(5)), 2);
		world.playLevelEvent(1034, blockPos, 0);
	}

	private static boolean isSurroundedByAir(ViewableWorld viewableWorld, BlockPos blockPos, @Nullable Direction direction) {
		for (Direction direction2 : Direction.Type.field_11062) {
			if (direction2 != direction && !viewableWorld.isAir(blockPos.offset(direction2))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction != Direction.field_11036 && !blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.method_8397().schedule(blockPos, this, 1);
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState2 = viewableWorld.method_8320(blockPos.down());
		Block block = blockState2.getBlock();
		if (block != this.field_10763 && block != Blocks.field_10471) {
			if (!blockState2.isAir()) {
				return false;
			} else {
				boolean bl = false;

				for (Direction direction : Direction.Type.field_11062) {
					BlockState blockState3 = viewableWorld.method_8320(blockPos.offset(direction));
					if (blockState3.getBlock() == this.field_10763) {
						if (bl) {
							return false;
						}

						bl = true;
					} else if (!blockState3.isAir()) {
						return false;
					}
				}

				return bl;
			}
		} else {
			return true;
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10762);
	}

	public static void generate(IWorld iWorld, BlockPos blockPos, Random random, int i) {
		iWorld.method_8652(blockPos, ((ChorusPlantBlock)Blocks.field_10021).method_9759(iWorld, blockPos), 2);
		generate(iWorld, blockPos, random, blockPos, i, 0);
	}

	private static void generate(IWorld iWorld, BlockPos blockPos, Random random, BlockPos blockPos2, int i, int j) {
		ChorusPlantBlock chorusPlantBlock = (ChorusPlantBlock)Blocks.field_10021;
		int k = random.nextInt(4) + 1;
		if (j == 0) {
			k++;
		}

		for (int l = 0; l < k; l++) {
			BlockPos blockPos3 = blockPos.up(l + 1);
			if (!isSurroundedByAir(iWorld, blockPos3, null)) {
				return;
			}

			iWorld.method_8652(blockPos3, chorusPlantBlock.method_9759(iWorld, blockPos3), 2);
			iWorld.method_8652(blockPos3.down(), chorusPlantBlock.method_9759(iWorld, blockPos3.down()), 2);
		}

		boolean bl = false;
		if (j < 4) {
			int m = random.nextInt(4);
			if (j == 0) {
				m++;
			}

			for (int n = 0; n < m; n++) {
				Direction direction = Direction.Type.field_11062.random(random);
				BlockPos blockPos4 = blockPos.up(k).offset(direction);
				if (Math.abs(blockPos4.getX() - blockPos2.getX()) < i
					&& Math.abs(blockPos4.getZ() - blockPos2.getZ()) < i
					&& iWorld.isAir(blockPos4)
					&& iWorld.isAir(blockPos4.down())
					&& isSurroundedByAir(iWorld, blockPos4, direction.getOpposite())) {
					bl = true;
					iWorld.method_8652(blockPos4, chorusPlantBlock.method_9759(iWorld, blockPos4), 2);
					iWorld.method_8652(blockPos4.offset(direction.getOpposite()), chorusPlantBlock.method_9759(iWorld, blockPos4.offset(direction.getOpposite())), 2);
					generate(iWorld, blockPos4, random, blockPos2, i, j + 1);
				}
			}
		}

		if (!bl) {
			iWorld.method_8652(blockPos.up(k), Blocks.field_10528.method_9564().method_11657(field_10762, Integer.valueOf(5)), 2);
		}
	}

	@Override
	public void method_19286(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		BlockPos blockPos = blockHitResult.getBlockPos();
		dropStack(world, blockPos, new ItemStack(this));
		world.breakBlock(blockPos, true);
	}
}
