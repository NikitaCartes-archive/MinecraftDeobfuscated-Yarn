package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
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
	public static final BooleanProperty field_11621 = Properties.field_12539;

	public TntBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.method_9564().method_11657(field_11621, Boolean.valueOf(false)));
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (world.isReceivingRedstonePower(blockPos)) {
				primeTnt(world, blockPos);
				world.clearBlockState(blockPos, false);
			}
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (world.isReceivingRedstonePower(blockPos)) {
			primeTnt(world, blockPos);
			world.clearBlockState(blockPos, false);
		}
	}

	@Override
	public void method_9576(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isClient() && !playerEntity.isCreative() && (Boolean)blockState.method_11654(field_11621)) {
			primeTnt(world, blockPos);
		}

		super.method_9576(world, blockPos, blockState, playerEntity);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
		if (!world.isClient) {
			TntEntity tntEntity = new TntEntity(
				world, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), explosion.getCausingEntity()
			);
			tntEntity.setFuse((short)(world.random.nextInt(tntEntity.getFuseTimer() / 4) + tntEntity.getFuseTimer() / 8));
			world.spawnEntity(tntEntity);
		}
	}

	public static void primeTnt(World world, BlockPos blockPos) {
		primeTnt(world, blockPos, null);
	}

	private static void primeTnt(World world, BlockPos blockPos, @Nullable LivingEntity livingEntity) {
		if (!world.isClient) {
			TntEntity tntEntity = new TntEntity(
				world, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), livingEntity
			);
			world.spawnEntity(tntEntity);
			world.playSound(null, tntEntity.x, tntEntity.y, tntEntity.z, SoundEvents.field_15079, SoundCategory.field_15245, 1.0F, 1.0F);
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
			world.method_8652(blockPos, Blocks.field_10124.method_9564(), 11);
			if (item == Items.field_8884) {
				itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(hand));
			} else {
				itemStack.decrement(1);
			}

			return true;
		}
	}

	@Override
	public void method_19286(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		if (!world.isClient && entity instanceof ProjectileEntity) {
			ProjectileEntity projectileEntity = (ProjectileEntity)entity;
			Entity entity2 = projectileEntity.getOwner();
			if (projectileEntity.isOnFire()) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				primeTnt(world, blockPos, entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null);
				world.clearBlockState(blockPos, false);
			}
		}
	}

	@Override
	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return false;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11621);
	}
}
