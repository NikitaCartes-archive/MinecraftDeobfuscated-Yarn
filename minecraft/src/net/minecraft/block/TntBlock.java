package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PrimedTntEntity;
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
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (world.method_8479(blockPos)) {
				method_10738(world, blockPos);
				world.method_8650(blockPos);
			}
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (world.method_8479(blockPos)) {
			method_10738(world, blockPos);
			world.method_8650(blockPos);
		}
	}

	@Override
	public void method_9576(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isClient() && !playerEntity.isCreative() && (Boolean)blockState.method_11654(field_11621)) {
			method_10738(world, blockPos);
		}

		super.method_9576(world, blockPos, blockState, playerEntity);
	}

	@Override
	public void method_9586(World world, BlockPos blockPos, Explosion explosion) {
		if (!world.isClient) {
			PrimedTntEntity primedTntEntity = new PrimedTntEntity(
				world, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), explosion.getCausingEntity()
			);
			primedTntEntity.setFuse((short)(world.random.nextInt(primedTntEntity.getFuseTimer() / 4) + primedTntEntity.getFuseTimer() / 8));
			world.spawnEntity(primedTntEntity);
		}
	}

	public static void method_10738(World world, BlockPos blockPos) {
		method_10737(world, blockPos, null);
	}

	private static void method_10737(World world, BlockPos blockPos, @Nullable LivingEntity livingEntity) {
		if (!world.isClient) {
			PrimedTntEntity primedTntEntity = new PrimedTntEntity(
				world, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), livingEntity
			);
			world.spawnEntity(primedTntEntity);
			world.method_8465(null, primedTntEntity.x, primedTntEntity.y, primedTntEntity.z, SoundEvents.field_15079, SoundCategory.field_15245, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		Item item = itemStack.getItem();
		if (item != Items.field_8884 && item != Items.field_8814) {
			return super.method_9534(blockState, world, blockPos, playerEntity, hand, blockHitResult);
		} else {
			method_10737(world, blockPos, playerEntity);
			world.method_8652(blockPos, Blocks.field_10124.method_9564(), 11);
			if (item == Items.field_8884) {
				itemStack.applyDamage(1, playerEntity);
			} else {
				itemStack.subtractAmount(1);
			}

			return true;
		}
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient && entity instanceof ProjectileEntity) {
			ProjectileEntity projectileEntity = (ProjectileEntity)entity;
			Entity entity2 = projectileEntity.getOwner();
			if (projectileEntity.isOnFire()) {
				method_10737(world, blockPos, entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null);
				world.method_8650(blockPos);
			}
		}
	}

	@Override
	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return false;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11621);
	}
}
