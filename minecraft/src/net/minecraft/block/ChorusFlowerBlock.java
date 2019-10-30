package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ChorusFlowerBlock extends Block {
	public static final IntProperty AGE = Properties.AGE_5;
	private final ChorusPlantBlock plantBlock;

	protected ChorusFlowerBlock(ChorusPlantBlock plantBlock, Block.Settings settings) {
		super(settings);
		this.plantBlock = plantBlock;
		this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, true);
		} else {
			BlockPos blockPos = pos.up();
			if (world.isAir(blockPos) && blockPos.getY() < 256) {
				int i = (Integer)state.get(AGE);
				if (i < 5) {
					boolean bl = false;
					boolean bl2 = false;
					BlockState blockState = world.getBlockState(pos.method_10074());
					Block block = blockState.getBlock();
					if (block == Blocks.END_STONE) {
						bl = true;
					} else if (block == this.plantBlock) {
						int j = 1;

						for (int k = 0; k < 4; k++) {
							Block block2 = world.getBlockState(pos.down(j + 1)).getBlock();
							if (block2 != this.plantBlock) {
								if (block2 == Blocks.END_STONE) {
									bl2 = true;
								}
								break;
							}

							j++;
						}

						if (j < 2 || j <= random.nextInt(bl2 ? 5 : 4)) {
							bl = true;
						}
					} else if (blockState.isAir()) {
						bl = true;
					}

					if (bl && isSurroundedByAir(world, blockPos, null) && world.isAir(pos.up(2))) {
						world.setBlockState(pos, this.plantBlock.withConnectionProperties(world, pos), 2);
						this.grow(world, blockPos, i);
					} else if (i < 4) {
						int j = random.nextInt(4);
						if (bl2) {
							j++;
						}

						boolean bl3 = false;

						for (int l = 0; l < j; l++) {
							Direction direction = Direction.Type.HORIZONTAL.random(random);
							BlockPos blockPos2 = pos.offset(direction);
							if (world.isAir(blockPos2) && world.isAir(blockPos2.method_10074()) && isSurroundedByAir(world, blockPos2, direction.getOpposite())) {
								this.grow(world, blockPos2, i + 1);
								bl3 = true;
							}
						}

						if (bl3) {
							world.setBlockState(pos, this.plantBlock.withConnectionProperties(world, pos), 2);
						} else {
							this.die(world, pos);
						}
					} else {
						this.die(world, pos);
					}
				}
			}
		}
	}

	private void grow(World world, BlockPos pos, int age) {
		world.setBlockState(pos, this.getDefaultState().with(AGE, Integer.valueOf(age)), 2);
		world.playLevelEvent(1033, pos, 0);
	}

	private void die(World world, BlockPos pos) {
		world.setBlockState(pos, this.getDefaultState().with(AGE, Integer.valueOf(5)), 2);
		world.playLevelEvent(1034, pos, 0);
	}

	private static boolean isSurroundedByAir(WorldView worldView, BlockPos pos, @Nullable Direction exceptDirection) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (direction != exceptDirection && !worldView.isAir(pos.offset(direction))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (facing != Direction.UP && !state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.method_10074());
		Block block = blockState.getBlock();
		if (block != this.plantBlock && block != Blocks.END_STONE) {
			if (!blockState.isAir()) {
				return false;
			} else {
				boolean bl = false;

				for (Direction direction : Direction.Type.HORIZONTAL) {
					BlockState blockState2 = world.getBlockState(pos.offset(direction));
					if (blockState2.getBlock() == this.plantBlock) {
						if (bl) {
							return false;
						}

						bl = true;
					} else if (!blockState2.isAir()) {
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
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	public static void generate(IWorld world, BlockPos pos, Random random, int size) {
		world.setBlockState(pos, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).withConnectionProperties(world, pos), 2);
		generate(world, pos, random, pos, size, 0);
	}

	private static void generate(IWorld world, BlockPos pos, Random random, BlockPos rootPos, int size, int layer) {
		ChorusPlantBlock chorusPlantBlock = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
		int i = random.nextInt(4) + 1;
		if (layer == 0) {
			i++;
		}

		for (int j = 0; j < i; j++) {
			BlockPos blockPos = pos.up(j + 1);
			if (!isSurroundedByAir(world, blockPos, null)) {
				return;
			}

			world.setBlockState(blockPos, chorusPlantBlock.withConnectionProperties(world, blockPos), 2);
			world.setBlockState(blockPos.method_10074(), chorusPlantBlock.withConnectionProperties(world, blockPos.method_10074()), 2);
		}

		boolean bl = false;
		if (layer < 4) {
			int k = random.nextInt(4);
			if (layer == 0) {
				k++;
			}

			for (int l = 0; l < k; l++) {
				Direction direction = Direction.Type.HORIZONTAL.random(random);
				BlockPos blockPos2 = pos.up(i).offset(direction);
				if (Math.abs(blockPos2.getX() - rootPos.getX()) < size
					&& Math.abs(blockPos2.getZ() - rootPos.getZ()) < size
					&& world.isAir(blockPos2)
					&& world.isAir(blockPos2.method_10074())
					&& isSurroundedByAir(world, blockPos2, direction.getOpposite())) {
					bl = true;
					world.setBlockState(blockPos2, chorusPlantBlock.withConnectionProperties(world, blockPos2), 2);
					world.setBlockState(
						blockPos2.offset(direction.getOpposite()), chorusPlantBlock.withConnectionProperties(world, blockPos2.offset(direction.getOpposite())), 2
					);
					generate(world, blockPos2, random, rootPos, size, layer + 1);
				}
			}
		}

		if (!bl) {
			world.setBlockState(pos.up(i), Blocks.CHORUS_FLOWER.getDefaultState().with(AGE, Integer.valueOf(5)), 2);
		}
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hitResult, Entity entity) {
		BlockPos blockPos = hitResult.getBlockPos();
		world.breakBlock(blockPos, true, entity);
	}
}
