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
import net.minecraft.state.StateFactory;
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
	public static final IntProperty field_17000 = Properties.field_12497;
	private static final VoxelShape field_17001 = Block.method_9541(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
	private static final VoxelShape field_17002 = Block.method_9541(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	public SweetBerryBushBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_17000, Integer.valueOf(0)));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.field_16998);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		if ((Integer)blockState.method_11654(field_17000) == 0) {
			return field_17001;
		} else {
			return blockState.method_11654(field_17000) < 3 ? field_17002 : super.method_9530(blockState, blockView, blockPos, entityContext);
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.method_9588(blockState, world, blockPos, random);
		int i = (Integer)blockState.method_11654(field_17000);
		if (i < 3 && random.nextInt(5) == 0 && world.getLightLevel(blockPos.up(), 0) >= 9) {
			world.method_8652(blockPos, blockState.method_11657(field_17000, Integer.valueOf(i + 1)), 2);
		}
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (entity instanceof LivingEntity && entity.getType() != EntityType.field_17943) {
			entity.method_5844(blockState, new Vec3d(0.8F, 0.75, 0.8F));
			if (!world.isClient && (Integer)blockState.method_11654(field_17000) > 0 && (entity.prevRenderX != entity.x || entity.prevRenderZ != entity.z)) {
				double d = Math.abs(entity.x - entity.prevRenderX);
				double e = Math.abs(entity.z - entity.prevRenderZ);
				if (d >= 0.003F || e >= 0.003F) {
					entity.damage(DamageSource.SWEET_BERRY_BUSH, 1.0F);
				}
			}
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		int i = (Integer)blockState.method_11654(field_17000);
		boolean bl = i == 3;
		if (!bl && playerEntity.getStackInHand(hand).getItem() == Items.field_8324) {
			return false;
		} else if (i > 1) {
			int j = 1 + world.random.nextInt(2);
			dropStack(world, blockPos, new ItemStack(Items.field_16998, j + (bl ? 1 : 0)));
			world.playSound(null, blockPos, SoundEvents.field_17617, SoundCategory.field_15245, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
			world.method_8652(blockPos, blockState.method_11657(field_17000, Integer.valueOf(1)), 2);
			return true;
		} else {
			return super.method_9534(blockState, world, blockPos, playerEntity, hand, blockHitResult);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_17000);
	}

	@Override
	public boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return (Integer)blockState.method_11654(field_17000) < 3;
	}

	@Override
	public boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState) {
		int i = Math.min(3, (Integer)blockState.method_11654(field_17000) + 1);
		world.method_8652(blockPos, blockState.method_11657(field_17000, Integer.valueOf(i)), 2);
	}
}
