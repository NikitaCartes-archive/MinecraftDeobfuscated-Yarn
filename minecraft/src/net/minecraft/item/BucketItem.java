package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class BucketItem extends Item {
	private final Fluid field_7905;

	public BucketItem(Fluid fluid, Item.Settings settings) {
		super(settings);
		this.field_7905 = fluid;
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = method_7872(
			world, playerEntity, this.field_7905 == Fluids.field_15906 ? RayTraceContext.FluidHandling.field_1345 : RayTraceContext.FluidHandling.field_1348
		);
		if (hitResult.getType() == HitResult.Type.field_1333) {
			return new TypedActionResult<>(ActionResult.field_5811, itemStack);
		} else if (hitResult.getType() != HitResult.Type.field_1332) {
			return new TypedActionResult<>(ActionResult.field_5811, itemStack);
		} else {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockPos blockPos = blockHitResult.getBlockPos();
			if (!world.canPlayerModifyAt(playerEntity, blockPos) || !playerEntity.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
				return new TypedActionResult<>(ActionResult.field_5814, itemStack);
			} else if (this.field_7905 == Fluids.field_15906) {
				BlockState blockState = world.method_8320(blockPos);
				if (blockState.getBlock() instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)blockState.getBlock()).method_9700(world, blockPos, blockState);
					if (fluid != Fluids.field_15906) {
						playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
						playerEntity.playSound(fluid.matches(FluidTags.field_15518) ? SoundEvents.field_15202 : SoundEvents.field_15126, 1.0F, 1.0F);
						ItemStack itemStack2 = this.getFilledStack(itemStack, playerEntity, fluid.getBucketItem());
						if (!world.isClient) {
							Criterions.FILLED_BUCKET.handle((ServerPlayerEntity)playerEntity, new ItemStack(fluid.getBucketItem()));
						}

						return new TypedActionResult<>(ActionResult.field_5812, itemStack2);
					}
				}

				return new TypedActionResult<>(ActionResult.field_5814, itemStack);
			} else {
				BlockState blockState = world.method_8320(blockPos);
				BlockPos blockPos2 = blockState.getBlock() instanceof FluidFillable && this.field_7905 == Fluids.WATER
					? blockPos
					: blockHitResult.getBlockPos().offset(blockHitResult.getSide());
				if (this.method_7731(playerEntity, world, blockPos2, blockHitResult)) {
					this.method_7728(world, itemStack, blockPos2);
					if (playerEntity instanceof ServerPlayerEntity) {
						Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
					}

					playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
					return new TypedActionResult<>(ActionResult.field_5812, this.getEmptiedStack(itemStack, playerEntity));
				} else {
					return new TypedActionResult<>(ActionResult.field_5814, itemStack);
				}
			}
		}
	}

	protected ItemStack getEmptiedStack(ItemStack itemStack, PlayerEntity playerEntity) {
		return !playerEntity.abilities.creativeMode ? new ItemStack(Items.field_8550) : itemStack;
	}

	public void method_7728(World world, ItemStack itemStack, BlockPos blockPos) {
	}

	private ItemStack getFilledStack(ItemStack itemStack, PlayerEntity playerEntity, Item item) {
		if (playerEntity.abilities.creativeMode) {
			return itemStack;
		} else {
			itemStack.decrement(1);
			if (itemStack.isEmpty()) {
				return new ItemStack(item);
			} else {
				if (!playerEntity.inventory.insertStack(new ItemStack(item))) {
					playerEntity.dropItem(new ItemStack(item), false);
				}

				return itemStack;
			}
		}
	}

	public boolean method_7731(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
		if (!(this.field_7905 instanceof BaseFluid)) {
			return false;
		} else {
			BlockState blockState = world.method_8320(blockPos);
			Material material = blockState.method_11620();
			boolean bl = !material.isSolid();
			boolean bl2 = material.isReplaceable();
			if (world.isAir(blockPos)
				|| bl
				|| bl2
				|| blockState.getBlock() instanceof FluidFillable && ((FluidFillable)blockState.getBlock()).method_10310(world, blockPos, blockState, this.field_7905)) {
				if (world.field_9247.doesWaterVaporize() && this.field_7905.matches(FluidTags.field_15517)) {
					int i = blockPos.getX();
					int j = blockPos.getY();
					int k = blockPos.getZ();
					world.playSound(
						playerEntity, blockPos, SoundEvents.field_15102, SoundCategory.field_15245, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int l = 0; l < 8; l++) {
						world.addParticle(ParticleTypes.field_11237, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
					}
				} else if (blockState.getBlock() instanceof FluidFillable && this.field_7905 == Fluids.WATER) {
					if (((FluidFillable)blockState.getBlock()).method_10311(world, blockPos, blockState, ((BaseFluid)this.field_7905).method_15729(false))) {
						this.method_7727(playerEntity, world, blockPos);
					}
				} else {
					if (!world.isClient && (bl || bl2) && !material.isLiquid()) {
						world.breakBlock(blockPos, true);
					}

					this.method_7727(playerEntity, world, blockPos);
					world.method_8652(blockPos, this.field_7905.method_15785().getBlockState(), 11);
				}

				return true;
			} else {
				return blockHitResult == null ? false : this.method_7731(playerEntity, world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), null);
			}
		}
	}

	protected void method_7727(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
		SoundEvent soundEvent = this.field_7905.matches(FluidTags.field_15518) ? SoundEvents.field_15010 : SoundEvents.field_14834;
		iWorld.playSound(playerEntity, blockPos, soundEvent, SoundCategory.field_15245, 1.0F, 1.0F);
	}
}
