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
		ItemStack itemStack = playerEntity.method_5998(hand);
		HitResult hitResult = method_7872(
			world, playerEntity, this.field_7905 == Fluids.EMPTY ? RayTraceContext.FluidHandling.field_1345 : RayTraceContext.FluidHandling.NONE
		);
		if (hitResult.getType() == HitResult.Type.NONE) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockPos blockPos = blockHitResult.method_17777();
			if (!world.method_8505(playerEntity, blockPos) || !playerEntity.method_7343(blockPos, blockHitResult.method_17780(), itemStack)) {
				return new TypedActionResult<>(ActionResult.field_5814, itemStack);
			} else if (this.field_7905 == Fluids.EMPTY) {
				BlockState blockState = world.method_8320(blockPos);
				if (blockState.getBlock() instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)blockState.getBlock()).method_9700(world, blockPos, blockState);
					if (fluid != Fluids.EMPTY) {
						playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
						playerEntity.method_5783(fluid.method_15791(FluidTags.field_15518) ? SoundEvents.field_15202 : SoundEvents.field_15126, 1.0F, 1.0F);
						ItemStack itemStack2 = this.method_7730(itemStack, playerEntity, fluid.getBucketItem());
						if (!world.isClient) {
							Criterions.FILLED_BUCKET.method_8932((ServerPlayerEntity)playerEntity, new ItemStack(fluid.getBucketItem()));
						}

						return new TypedActionResult<>(ActionResult.field_5812, itemStack2);
					}
				}

				return new TypedActionResult<>(ActionResult.field_5814, itemStack);
			} else {
				BlockState blockState = world.method_8320(blockPos);
				BlockPos blockPos2 = blockState.getBlock() instanceof FluidFillable ? blockPos : blockHitResult.method_17777().method_10093(blockHitResult.method_17780());
				if (this.method_7731(playerEntity, world, blockPos2, blockHitResult)) {
					this.method_7728(world, itemStack, blockPos2);
					if (playerEntity instanceof ServerPlayerEntity) {
						Criterions.PLACED_BLOCK.method_9087((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
					}

					playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
					return new TypedActionResult<>(ActionResult.field_5812, this.method_7732(itemStack, playerEntity));
				} else {
					return new TypedActionResult<>(ActionResult.field_5814, itemStack);
				}
			}
		} else {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		}
	}

	protected ItemStack method_7732(ItemStack itemStack, PlayerEntity playerEntity) {
		return !playerEntity.abilities.creativeMode ? new ItemStack(Items.field_8550) : itemStack;
	}

	public void method_7728(World world, ItemStack itemStack, BlockPos blockPos) {
	}

	private ItemStack method_7730(ItemStack itemStack, PlayerEntity playerEntity, Item item) {
		if (playerEntity.abilities.creativeMode) {
			return itemStack;
		} else {
			itemStack.subtractAmount(1);
			if (itemStack.isEmpty()) {
				return new ItemStack(item);
			} else {
				if (!playerEntity.inventory.method_7394(new ItemStack(item))) {
					playerEntity.method_7328(new ItemStack(item), false);
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
			boolean bl = !material.method_15799();
			boolean bl2 = material.isReplaceable();
			if (world.method_8623(blockPos)
				|| bl
				|| bl2
				|| blockState.getBlock() instanceof FluidFillable && ((FluidFillable)blockState.getBlock()).method_10310(world, blockPos, blockState, this.field_7905)) {
				if (world.field_9247.doesWaterVaporize() && this.field_7905.method_15791(FluidTags.field_15517)) {
					int i = blockPos.getX();
					int j = blockPos.getY();
					int k = blockPos.getZ();
					world.method_8396(
						playerEntity, blockPos, SoundEvents.field_15102, SoundCategory.field_15245, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int l = 0; l < 8; l++) {
						world.method_8406(ParticleTypes.field_11237, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
					}
				} else if (blockState.getBlock() instanceof FluidFillable) {
					if (((FluidFillable)blockState.getBlock()).method_10311(world, blockPos, blockState, ((BaseFluid)this.field_7905).method_15729(false))) {
						this.method_7727(playerEntity, world, blockPos);
					}
				} else {
					if (!world.isClient && (bl || bl2) && !material.isLiquid()) {
						world.method_8651(blockPos, true);
					}

					this.method_7727(playerEntity, world, blockPos);
					world.method_8652(blockPos, this.field_7905.method_15785().getBlockState(), 11);
				}

				return true;
			} else {
				return blockHitResult == null
					? false
					: this.method_7731(playerEntity, world, blockHitResult.method_17777().method_10093(blockHitResult.method_17780()), null);
			}
		}
	}

	protected void method_7727(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
		SoundEvent soundEvent = this.field_7905.method_15791(FluidTags.field_15518) ? SoundEvents.field_15010 : SoundEvents.field_14834;
		iWorld.method_8396(playerEntity, blockPos, soundEvent, SoundCategory.field_15245, 1.0F, 1.0F);
	}
}
