package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
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
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class BucketItem extends Item {
	private final Fluid fluid;

	public BucketItem(Fluid fluid, Item.Settings settings) {
		super(settings);
		this.fluid = fluid;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		HitResult hitResult = rayTrace(
			world, user, this.fluid == Fluids.field_15906 ? RayTraceContext.FluidHandling.field_1345 : RayTraceContext.FluidHandling.field_1348
		);
		if (hitResult.getType() == HitResult.Type.field_1333) {
			return TypedActionResult.pass(itemStack);
		} else if (hitResult.getType() != HitResult.Type.field_1332) {
			return TypedActionResult.pass(itemStack);
		} else {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockPos blockPos = blockHitResult.getBlockPos();
			Direction direction = blockHitResult.getSide();
			BlockPos blockPos2 = blockPos.offset(direction);
			if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(blockPos2, direction, itemStack)) {
				return TypedActionResult.fail(itemStack);
			} else if (this.fluid == Fluids.field_15906) {
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, blockPos, blockState);
					if (fluid != Fluids.field_15906) {
						user.incrementStat(Stats.field_15372.getOrCreateStat(this));
						user.playSound(fluid.isIn(FluidTags.field_15518) ? SoundEvents.field_15202 : SoundEvents.field_15126, 1.0F, 1.0F);
						ItemStack itemStack2 = ItemUsage.method_30012(itemStack, user, new ItemStack(fluid.getBucketItem()));
						if (!world.isClient) {
							Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)user, new ItemStack(fluid.getBucketItem()));
						}

						return TypedActionResult.method_29237(itemStack2, world.isClient());
					}
				}

				return TypedActionResult.fail(itemStack);
			} else {
				BlockState blockState = world.getBlockState(blockPos);
				BlockPos blockPos3 = blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER ? blockPos : blockPos2;
				if (this.placeFluid(user, world, blockPos3, blockHitResult)) {
					this.onEmptied(world, itemStack, blockPos3);
					if (user instanceof ServerPlayerEntity) {
						Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)user, blockPos3, itemStack);
					}

					user.incrementStat(Stats.field_15372.getOrCreateStat(this));
					return TypedActionResult.method_29237(this.getEmptiedStack(itemStack, user), world.isClient());
				} else {
					return TypedActionResult.fail(itemStack);
				}
			}
		}
	}

	protected ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
		return !player.abilities.creativeMode ? new ItemStack(Items.field_8550) : stack;
	}

	public void onEmptied(World world, ItemStack stack, BlockPos pos) {
	}

	public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult blockHitResult) {
		if (!(this.fluid instanceof FlowableFluid)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			Material material = blockState.getMaterial();
			boolean bl = blockState.canBucketPlace(this.fluid);
			boolean bl2 = blockState.isAir() || bl || block instanceof FluidFillable && ((FluidFillable)block).canFillWithFluid(world, pos, blockState, this.fluid);
			if (!bl2) {
				return blockHitResult != null && this.placeFluid(player, world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), null);
			} else if (world.getDimension().isUltrawarm() && this.fluid.isIn(FluidTags.field_15517)) {
				int i = pos.getX();
				int j = pos.getY();
				int k = pos.getZ();
				world.playSound(player, pos, SoundEvents.field_15102, SoundCategory.field_15245, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

				for (int l = 0; l < 8; l++) {
					world.addParticle(ParticleTypes.field_11237, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
				}

				return true;
			} else if (block instanceof FluidFillable && this.fluid == Fluids.WATER) {
				((FluidFillable)block).tryFillWithFluid(world, pos, blockState, ((FlowableFluid)this.fluid).getStill(false));
				this.playEmptyingSound(player, world, pos);
				return true;
			} else {
				if (!world.isClient && bl && !material.isLiquid()) {
					world.breakBlock(pos, true);
				}

				if (!world.setBlockState(pos, this.fluid.getDefaultState().getBlockState(), 11) && !blockState.getFluidState().isStill()) {
					return false;
				} else {
					this.playEmptyingSound(player, world, pos);
					return true;
				}
			}
		}
	}

	protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
		SoundEvent soundEvent = this.fluid.isIn(FluidTags.field_15518) ? SoundEvents.field_15010 : SoundEvents.field_14834;
		world.playSound(player, pos, soundEvent, SoundCategory.field_15245, 1.0F, 1.0F);
	}
}
