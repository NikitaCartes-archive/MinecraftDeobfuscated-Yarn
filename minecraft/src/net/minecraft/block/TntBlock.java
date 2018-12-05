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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntBlock extends Block {
	public static final BooleanProperty field_11621 = Properties.UNSTABLE;

	public TntBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(field_11621, Boolean.valueOf(false)));
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (world.isReceivingRedstonePower(blockPos)) {
				this.method_10738(world, blockPos);
				world.clearBlockState(blockPos);
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (world.isReceivingRedstonePower(blockPos)) {
			this.method_10738(world, blockPos);
			world.clearBlockState(blockPos);
		}
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isRemote() && !playerEntity.isCreative() && (Boolean)blockState.get(field_11621)) {
			this.method_10738(world, blockPos);
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
		if (!world.isRemote) {
			PrimedTNTEntity primedTNTEntity = new PrimedTNTEntity(
				world, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), explosion.getCausingEntity()
			);
			primedTNTEntity.setFuse((short)(world.random.nextInt(primedTNTEntity.getFuseTimer() / 4) + primedTNTEntity.getFuseTimer() / 8));
			world.spawnEntity(primedTNTEntity);
		}
	}

	public void method_10738(World world, BlockPos blockPos) {
		this.method_10737(world, blockPos, null);
	}

	private void method_10737(World world, BlockPos blockPos, @Nullable LivingEntity livingEntity) {
		if (!world.isRemote) {
			PrimedTNTEntity primedTNTEntity = new PrimedTNTEntity(
				world, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), livingEntity
			);
			world.spawnEntity(primedTNTEntity);
			world.playSound(null, primedTNTEntity.x, primedTNTEntity.y, primedTNTEntity.z, SoundEvents.field_15079, SoundCategory.field_15245, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (item != Items.field_8884 && item != Items.field_8814) {
			return super.method_9534(blockState, world, blockPos, playerEntity, hand, direction, f, g, h);
		} else {
			this.method_10737(world, blockPos, playerEntity);
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
		if (!world.isRemote && entity instanceof ProjectileEntity) {
			ProjectileEntity projectileEntity = (ProjectileEntity)entity;
			Entity entity2 = projectileEntity.getOwner();
			if (projectileEntity.isOnFire()) {
				this.method_10737(world, blockPos, entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null);
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
		builder.with(field_11621);
	}
}
