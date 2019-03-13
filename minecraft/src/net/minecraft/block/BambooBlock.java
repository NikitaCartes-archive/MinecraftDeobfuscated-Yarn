package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.SwordItem;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class BambooBlock extends Block implements Fertilizable {
	protected static final VoxelShape field_9912 = Block.method_9541(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
	protected static final VoxelShape field_9915 = Block.method_9541(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
	protected static final VoxelShape field_9913 = Block.method_9541(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
	public static final IntegerProperty field_9914 = Properties.field_12521;
	public static final EnumProperty<BambooLeaves> field_9917 = Properties.field_12516;
	public static final IntegerProperty field_9916 = Properties.field_12549;

	public BambooBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_9914, Integer.valueOf(0))
				.method_11657(field_9917, BambooLeaves.field_12469)
				.method_11657(field_9916, Integer.valueOf(0))
		);
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_9914, field_9917, field_9916);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	@Override
	public boolean method_9579(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		VoxelShape voxelShape = blockState.method_11654(field_9917) == BambooLeaves.field_12468 ? field_9915 : field_9912;
		Vec3d vec3d = blockState.method_11599(blockView, blockPos);
		return voxelShape.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Vec3d vec3d = blockState.method_11599(blockView, blockPos);
		return field_9913.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.method_8037());
		if (!fluidState.isEmpty()) {
			return null;
		} else {
			BlockState blockState = itemPlacementContext.method_8045().method_8320(itemPlacementContext.method_8037().down());
			if (blockState.method_11602(BlockTags.field_15497)) {
				Block block = blockState.getBlock();
				if (block == Blocks.field_10108) {
					return this.method_9564().method_11657(field_9914, Integer.valueOf(0));
				} else if (block == Blocks.field_10211) {
					int i = blockState.method_11654(field_9914) > 0 ? 1 : 0;
					return this.method_9564().method_11657(field_9914, Integer.valueOf(i));
				} else {
					return Blocks.field_10108.method_9564();
				}
			} else {
				return null;
			}
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Integer)blockState.method_11654(field_9916) == 0) {
			if (random.nextInt(3) == 0 && world.method_8623(blockPos.up()) && world.method_8624(blockPos.up(), 0) >= 9) {
				int i = this.method_9386(world, blockPos) + 1;
				if (i < 16) {
					this.method_9385(blockState, world, blockPos, random, i);
				}
			}
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.method_8320(blockPos.down()).method_11602(BlockTags.field_15497);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (!blockState.method_11591(iWorld, blockPos)) {
			return Blocks.field_10124.method_9564();
		} else {
			if (direction == Direction.UP
				&& blockState2.getBlock() == Blocks.field_10211
				&& (Integer)blockState2.method_11654(field_9914) > (Integer)blockState.method_11654(field_9914)) {
				iWorld.method_8652(blockPos, blockState.method_11572(field_9914), 2);
			}

			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		int i = this.method_9387(blockView, blockPos);
		int j = this.method_9386(blockView, blockPos);
		return i + j + 1 < 16 && (Integer)blockView.method_8320(blockPos.up(i)).method_11654(field_9916) != 1;
	}

	@Override
	public boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState) {
		int i = this.method_9387(world, blockPos);
		int j = this.method_9386(world, blockPos);
		int k = i + j + 1;
		int l = 1 + random.nextInt(2);

		for (int m = 0; m < l; m++) {
			BlockPos blockPos2 = blockPos.up(i);
			BlockState blockState2 = world.method_8320(blockPos2);
			if (k >= 16 || (Integer)blockState2.method_11654(field_9916) == 1 || !world.method_8623(blockPos2.up())) {
				return;
			}

			this.method_9385(blockState2, world, blockPos2, random, k);
			i++;
			k++;
		}
	}

	@Override
	public float method_9594(BlockState blockState, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		return playerEntity.method_6047().getItem() instanceof SwordItem ? 1.0F : super.method_9594(blockState, playerEntity, blockView, blockPos);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	protected void method_9385(BlockState blockState, World world, BlockPos blockPos, Random random, int i) {
		BlockState blockState2 = world.method_8320(blockPos.down());
		BlockPos blockPos2 = blockPos.down(2);
		BlockState blockState3 = world.method_8320(blockPos2);
		BambooLeaves bambooLeaves = BambooLeaves.field_12469;
		if (i >= 1) {
			if (blockState2.getBlock() != Blocks.field_10211 || blockState2.method_11654(field_9917) == BambooLeaves.field_12469) {
				bambooLeaves = BambooLeaves.field_12466;
			} else if (blockState2.getBlock() == Blocks.field_10211 && blockState2.method_11654(field_9917) != BambooLeaves.field_12469) {
				bambooLeaves = BambooLeaves.field_12468;
				if (blockState3.getBlock() == Blocks.field_10211) {
					world.method_8652(blockPos.down(), blockState2.method_11657(field_9917, BambooLeaves.field_12466), 3);
					world.method_8652(blockPos2, blockState3.method_11657(field_9917, BambooLeaves.field_12469), 3);
				}
			}
		}

		int j = blockState.method_11654(field_9914) != 1 && blockState3.getBlock() != Blocks.field_10211 ? 0 : 1;
		int k = (i < 11 || !(random.nextFloat() < 0.25F)) && i != 15 ? 0 : 1;
		world.method_8652(
			blockPos.up(),
			this.method_9564().method_11657(field_9914, Integer.valueOf(j)).method_11657(field_9917, bambooLeaves).method_11657(field_9916, Integer.valueOf(k)),
			3
		);
	}

	protected int method_9387(BlockView blockView, BlockPos blockPos) {
		int i = 0;

		while (i < 16 && blockView.method_8320(blockPos.up(i + 1)).getBlock() == Blocks.field_10211) {
			i++;
		}

		return i;
	}

	protected int method_9386(BlockView blockView, BlockPos blockPos) {
		int i = 0;

		while (i < 16 && blockView.method_8320(blockPos.down(i + 1)).getBlock() == Blocks.field_10211) {
			i++;
		}

		return i;
	}
}
