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
	private final Fluid fluid;

	public BucketItem(Fluid fluid, Item.Settings settings) {
		super(settings);
		this.fluid = fluid;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = method_7872(
			world, playerEntity, this.fluid == Fluids.EMPTY ? RayTraceContext.FluidHandling.field_1345 : RayTraceContext.FluidHandling.NONE
		);
		if (hitResult.getType() == HitResult.Type.NONE) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockPos blockPos = blockHitResult.getBlockPos();
			if (!world.canPlayerModifyAt(playerEntity, blockPos) || !playerEntity.canPlaceBlock(blockPos, blockHitResult.getSide(), itemStack)) {
				return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
			} else if (this.fluid == Fluids.EMPTY) {
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, blockPos, blockState);
					if (fluid != Fluids.EMPTY) {
						playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
						playerEntity.playSound(fluid.matches(FluidTags.field_15518) ? SoundEvents.field_15202 : SoundEvents.field_15126, 1.0F, 1.0F);
						ItemStack itemStack2 = this.getFilledStack(itemStack, playerEntity, fluid.getBucketItem());
						if (!world.isClient) {
							Criterions.FILLED_BUCKET.method_8932((ServerPlayerEntity)playerEntity, new ItemStack(fluid.getBucketItem()));
						}

						return new TypedActionResult<>(ActionResult.SUCCESS, itemStack2);
					}
				}

				return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
			} else {
				BlockState blockState = world.getBlockState(blockPos);
				BlockPos blockPos2 = blockState.getBlock() instanceof FluidFillable ? blockPos : blockHitResult.getBlockPos().offset(blockHitResult.getSide());
				if (this.method_7731(playerEntity, world, blockPos2, blockHitResult)) {
					this.onEmptied(world, itemStack, blockPos2);
					if (playerEntity instanceof ServerPlayerEntity) {
						Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
					}

					playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, this.getEmptiedStack(itemStack, playerEntity));
				} else {
					return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
				}
			}
		} else {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		}
	}

	protected ItemStack getEmptiedStack(ItemStack itemStack, PlayerEntity playerEntity) {
		return !playerEntity.abilities.creativeMode ? new ItemStack(Items.field_8550) : itemStack;
	}

	public void onEmptied(World world, ItemStack itemStack, BlockPos blockPos) {
	}

	private ItemStack getFilledStack(ItemStack itemStack, PlayerEntity playerEntity, Item item) {
		if (playerEntity.abilities.creativeMode) {
			return itemStack;
		} else {
			itemStack.subtractAmount(1);
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
		if (!(this.fluid instanceof BaseFluid)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			Material material = blockState.getMaterial();
			boolean bl = !material.method_15799();
			boolean bl2 = material.isReplaceable();
			if (world.isAir(blockPos)
				|| bl
				|| bl2
				|| blockState.getBlock() instanceof FluidFillable && ((FluidFillable)blockState.getBlock()).canFillWithFluid(world, blockPos, blockState, this.fluid)) {
				if (world.dimension.doesWaterVaporize() && this.fluid.matches(FluidTags.field_15517)) {
					int i = blockPos.getX();
					int j = blockPos.getY();
					int k = blockPos.getZ();
					world.playSound(
						playerEntity, blockPos, SoundEvents.field_15102, SoundCategory.field_15245, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int l = 0; l < 8; l++) {
						world.addParticle(ParticleTypes.field_11237, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
					}
				} else if (blockState.getBlock() instanceof FluidFillable) {
					if (((FluidFillable)blockState.getBlock()).tryFillWithFluid(world, blockPos, blockState, ((BaseFluid)this.fluid).getState(false))) {
						this.playEmptyingSound(playerEntity, world, blockPos);
					}
				} else {
					if (!world.isClient && (bl || bl2) && !material.isLiquid()) {
						world.breakBlock(blockPos, true);
					}

					this.playEmptyingSound(playerEntity, world, blockPos);
					world.setBlockState(blockPos, this.fluid.getDefaultState().getBlockState(), 11);
				}

				return true;
			} else {
				return blockHitResult == null ? false : this.method_7731(playerEntity, world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), null);
			}
		}
	}

	protected void playEmptyingSound(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
		SoundEvent soundEvent = this.fluid.matches(FluidTags.field_15518) ? SoundEvents.field_15010 : SoundEvents.field_14834;
		iWorld.playSound(playerEntity, blockPos, soundEvent, SoundCategory.field_15245, 1.0F, 1.0F);
	}
}
