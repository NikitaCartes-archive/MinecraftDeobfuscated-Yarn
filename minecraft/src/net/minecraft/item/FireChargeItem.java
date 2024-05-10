package net.minecraft.item;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FireChargeItem extends Item implements ProjectileItem {
	public FireChargeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = false;
		if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState)) {
			blockPos = blockPos.offset(context.getSide());
			if (AbstractFireBlock.canPlaceAt(world, blockPos, context.getHorizontalPlayerFacing())) {
				this.playUseSound(world, blockPos);
				world.setBlockState(blockPos, AbstractFireBlock.getState(world, blockPos));
				world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_PLACE, blockPos);
				bl = true;
			}
		} else {
			this.playUseSound(world, blockPos);
			world.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)));
			world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
			bl = true;
		}

		if (bl) {
			context.getStack().decrement(1);
			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.FAIL;
		}
	}

	private void playUseSound(World world, BlockPos pos) {
		Random random = world.getRandom();
		world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
	}

	@Override
	public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
		Random random = world.getRandom();
		double d = random.nextTriangular((double)direction.getOffsetX(), 0.11485000000000001);
		double e = random.nextTriangular((double)direction.getOffsetY(), 0.11485000000000001);
		double f = random.nextTriangular((double)direction.getOffsetZ(), 0.11485000000000001);
		Vec3d vec3d = new Vec3d(d, e, f);
		SmallFireballEntity smallFireballEntity = new SmallFireballEntity(world, pos.getX(), pos.getY(), pos.getZ(), vec3d.normalize());
		smallFireballEntity.setItem(stack);
		return smallFireballEntity;
	}

	@Override
	public void initializeProjectile(ProjectileEntity entity, double x, double y, double z, float power, float uncertainty) {
	}

	@Override
	public ProjectileItem.Settings getProjectileSettings() {
		return ProjectileItem.Settings.builder()
			.positionFunction((pointer, facing) -> DispenserBlock.getOutputLocation(pointer, 1.0, Vec3d.ZERO))
			.uncertainty(6.6666665F)
			.power(1.0F)
			.overrideDispenseEvent(1018)
			.build();
	}
}
