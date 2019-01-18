package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PrimedTNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntBlock extends Block {
	public static final BooleanProperty UNSTABLE = Properties.UNSTABLE;

	public TntBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(UNSTABLE, Boolean.valueOf(false)));
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (world.isReceivingRedstonePower(blockPos)) {
				primeTnt(world, blockPos);
				world.clearBlockState(blockPos);
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (world.isReceivingRedstonePower(blockPos)) {
			primeTnt(world, blockPos);
			world.clearBlockState(blockPos);
		}
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isClient() && !playerEntity.isCreative() && (Boolean)blockState.get(UNSTABLE)) {
			primeTnt(world, blockPos);
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
		if (!world.isClient) {
			PrimedTNTEntity primedTNTEntity = new PrimedTNTEntity(
				world, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), explosion.getCausingEntity()
			);
			primedTNTEntity.setFuse((short)(world.random.nextInt(primedTNTEntity.getFuseTimer() / 4) + primedTNTEntity.getFuseTimer() / 8));
			world.spawnEntity(primedTNTEntity);
		}
	}

	public static void primeTnt(World world, BlockPos blockPos) {
		primeTnt(world, blockPos, null);
	}

	private static void primeTnt(World world, BlockPos blockPos, @Nullable LivingEntity livingEntity) {
		if (!world.isClient) {
			PrimedTNTEntity primedTNTEntity = new PrimedTNTEntity(
				world, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), livingEntity
			);
			world.spawnEntity(primedTNTEntity);
			world.playSound(null, primedTNTEntity.x, primedTNTEntity.y, primedTNTEntity.z, SoundEvents.field_15079, SoundCategory.field_15245, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (item != Items.field_8884 && item != Items.field_8814) {
			return super.method_9534(blockState, world, blockPos, playerEntity, hand, blockHitResult);
		} else {
			primeTnt(world, blockPos, playerEntity);
			world.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 11);
			if (item == Items.field_8884) {
				itemStack.applyDamage(1, playerEntity);
			} else {
				itemStack.subtractAmount(1);
			}

			return true;
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient && entity instanceof ProjectileEntity) {
			ProjectileEntity projectileEntity = (ProjectileEntity)entity;
			Entity entity2 = projectileEntity.getOwner();
			if (projectileEntity.isOnFire()) {
				primeTnt(world, blockPos, entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null);
				world.clearBlockState(blockPos);
			}
		}
	}

	@Override
	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return false;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(UNSTABLE);
	}
}
