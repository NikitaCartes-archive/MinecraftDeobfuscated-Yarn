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
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		HitResult hitResult = rayTrace(world, user, this.fluid == Fluids.EMPTY ? RayTraceContext.FluidHandling.SOURCE_ONLY : RayTraceContext.FluidHandling.NONE);
		if (hitResult.getType() == HitResult.Type.MISS) {
			return TypedActionResult.pass(itemStack);
		} else if (hitResult.getType() != HitResult.Type.BLOCK) {
			return TypedActionResult.pass(itemStack);
		} else {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockPos blockPos = blockHitResult.getBlockPos();
			Direction direction = blockHitResult.getSide();
			BlockPos blockPos2 = blockPos.offset(direction);
			if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(blockPos2, direction, itemStack)) {
				return TypedActionResult.fail(itemStack);
			} else if (this.fluid == Fluids.EMPTY) {
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, blockPos, blockState);
					if (fluid != Fluids.EMPTY) {
						user.incrementStat(Stats.USED.getOrCreateStat(this));
						user.playSound(fluid.matches(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
						ItemStack itemStack2 = this.getFilledStack(itemStack, user, fluid.getBucketItem());
						if (!world.isClient) {
							Criterions.FILLED_BUCKET.trigger((ServerPlayerEntity)user, new ItemStack(fluid.getBucketItem()));
						}

						return TypedActionResult.success(itemStack2);
					}
				}

				return TypedActionResult.fail(itemStack);
			} else {
				BlockState blockState = world.getBlockState(blockPos);
				BlockPos blockPos3 = blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER ? blockPos : blockPos2;
				if (this.placeFluid(user, world, blockPos3, blockHitResult)) {
					this.onEmptied(world, itemStack, blockPos3);
					if (user instanceof ServerPlayerEntity) {
						Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity)user, blockPos3, itemStack);
					}

					user.incrementStat(Stats.USED.getOrCreateStat(this));
					return TypedActionResult.success(this.getEmptiedStack(itemStack, user));
				} else {
					return TypedActionResult.fail(itemStack);
				}
			}
		}
	}

	protected ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
		return !player.abilities.creativeMode ? new ItemStack(Items.BUCKET) : stack;
	}

	public void onEmptied(World world, ItemStack stack, BlockPos pos) {
	}

	private ItemStack getFilledStack(ItemStack stack, PlayerEntity player, Item filledBucket) {
		if (player.abilities.creativeMode) {
			return stack;
		} else {
			stack.decrement(1);
			if (stack.isEmpty()) {
				return new ItemStack(filledBucket);
			} else {
				if (!player.inventory.insertStack(new ItemStack(filledBucket))) {
					player.dropItem(new ItemStack(filledBucket), false);
				}

				return stack;
			}
		}
	}

	public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
		if (!(this.fluid instanceof BaseFluid)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Material material = blockState.getMaterial();
			boolean bl = blockState.canBucketPlace(this.fluid);
			if (blockState.isAir()
				|| bl
				|| blockState.getBlock() instanceof FluidFillable && ((FluidFillable)blockState.getBlock()).canFillWithFluid(world, pos, blockState, this.fluid)) {
				if (world.dimension.doesWaterVaporize() && this.fluid.matches(FluidTags.WATER)) {
					int i = pos.getX();
					int j = pos.getY();
					int k = pos.getZ();
					world.playSound(
						player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int l = 0; l < 8; l++) {
						world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
					}
				} else if (blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER) {
					if (((FluidFillable)blockState.getBlock()).tryFillWithFluid(world, pos, blockState, ((BaseFluid)this.fluid).getStill(false))) {
						this.playEmptyingSound(player, world, pos);
					}
				} else {
					if (!world.isClient && bl && !material.isLiquid()) {
						world.breakBlock(pos, true);
					}

					this.playEmptyingSound(player, world, pos);
					world.setBlockState(pos, this.fluid.getDefaultState().getBlockState(), 11);
				}

				return true;
			} else {
				return hitResult == null ? false : this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null);
			}
		}
	}

	protected void playEmptyingSound(@Nullable PlayerEntity player, IWorld world, BlockPos pos) {
		SoundEvent soundEvent = this.fluid.matches(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
		world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}
}
