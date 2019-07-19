package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SweetBerryBushBlock extends PlantBlock implements Fertilizable {
	public static final IntProperty AGE = Properties.AGE_3;
	private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
	private static final VoxelShape LARGE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	public SweetBerryBushBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Items.SWEET_BERRIES);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		if ((Integer)state.get(AGE) == 0) {
			return SMALL_SHAPE;
		} else {
			return state.get(AGE) < 3 ? LARGE_SHAPE : super.getOutlineShape(state, view, pos, context);
		}
	}

	@Override
	public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		super.onScheduledTick(state, world, pos, random);
		int i = (Integer)state.get(AGE);
		if (i < 3 && random.nextInt(5) == 0 && world.getLightLevel(pos.up(), 0) >= 9) {
			world.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), 2);
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity && entity.getType() != EntityType.FOX) {
			entity.slowMovement(state, new Vec3d(0.8F, 0.75, 0.8F));
			if (!world.isClient && (Integer)state.get(AGE) > 0 && (entity.lastRenderX != entity.x || entity.lastRenderZ != entity.z)) {
				double d = Math.abs(entity.x - entity.lastRenderX);
				double e = Math.abs(entity.z - entity.lastRenderZ);
				if (d >= 0.003F || e >= 0.003F) {
					entity.damage(DamageSource.SWEET_BERRY_BUSH, 1.0F);
				}
			}
		}
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		int i = (Integer)state.get(AGE);
		boolean bl = i == 3;
		if (!bl && player.getStackInHand(hand).getItem() == Items.BONE_MEAL) {
			return false;
		} else if (i > 1) {
			int j = 1 + world.random.nextInt(2);
			dropStack(world, pos, new ItemStack(Items.SWEET_BERRIES, j + (bl ? 1 : 0)));
			world.playSound(null, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
			world.setBlockState(pos, state.with(AGE, Integer.valueOf(1)), 2);
			return true;
		} else {
			return super.activate(state, world, pos, player, hand, hit);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return (Integer)state.get(AGE) < 3;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		int i = Math.min(3, (Integer)state.get(AGE) + 1);
		world.setBlockState(pos, state.with(AGE, Integer.valueOf(i)), 2);
	}
}
