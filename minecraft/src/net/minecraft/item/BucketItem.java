package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.class_2263;
import net.minecraft.class_2402;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.HitResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BucketItem extends Item {
	private final Fluid field_7905;

	public BucketItem(Fluid fluid, Item.Settings settings) {
		super(settings);
		this.field_7905 = fluid;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = this.getHitResult(world, playerEntity, this.field_7905 == Fluids.field_15906);
		if (hitResult == null) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else if (hitResult.type == HitResult.Type.BLOCK) {
			BlockPos blockPos = hitResult.getBlockPos();
			if (!world.canPlayerModifyAt(playerEntity, blockPos) || !playerEntity.method_7343(blockPos, hitResult.field_1327, itemStack)) {
				return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
			} else if (this.field_7905 == Fluids.field_15906) {
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() instanceof class_2263) {
					Fluid fluid = ((class_2263)blockState.getBlock()).method_9700(world, blockPos, blockState);
					if (fluid != Fluids.field_15906) {
						playerEntity.incrementStat(Stats.field_15372.method_14956(this));
						playerEntity.playSoundAtEntity(fluid.matches(FluidTags.field_15518) ? SoundEvents.field_15202 : SoundEvents.field_15126, 1.0F, 1.0F);
						ItemStack itemStack2 = this.method_7730(itemStack, playerEntity, fluid.getBucketItem());
						if (!world.isRemote) {
							CriterionCriterions.FILLED_BUCKET.method_8932((ServerPlayerEntity)playerEntity, new ItemStack(fluid.getBucketItem()));
						}

						return new TypedActionResult<>(ActionResult.SUCCESS, itemStack2);
					}
				}

				return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
			} else {
				BlockState blockState = world.getBlockState(blockPos);
				BlockPos blockPos2 = this.method_7729(blockState, blockPos, hitResult);
				if (this.method_7731(playerEntity, world, blockPos2, hitResult)) {
					this.method_7728(world, itemStack, blockPos2);
					if (playerEntity instanceof ServerPlayerEntity) {
						CriterionCriterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
					}

					playerEntity.incrementStat(Stats.field_15372.method_14956(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, this.method_7732(itemStack, playerEntity));
				} else {
					return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
				}
			}
		} else {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		}
	}

	private BlockPos method_7729(BlockState blockState, BlockPos blockPos, HitResult hitResult) {
		return blockState.getBlock() instanceof class_2402 ? blockPos : hitResult.getBlockPos().method_10093(hitResult.field_1327);
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
				if (!playerEntity.inventory.insertStack(new ItemStack(item))) {
					playerEntity.dropItem(new ItemStack(item), false);
				}

				return itemStack;
			}
		}
	}

	public boolean method_7731(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, @Nullable HitResult hitResult) {
		if (!(this.field_7905 instanceof BaseFluid)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			Material material = blockState.getMaterial();
			boolean bl = !material.method_15799();
			boolean bl2 = material.method_15800();
			if (world.isAir(blockPos)
				|| bl
				|| bl2
				|| blockState.getBlock() instanceof class_2402 && ((class_2402)blockState.getBlock()).method_10310(world, blockPos, blockState, this.field_7905)) {
				if (world.dimension.method_12465() && this.field_7905.matches(FluidTags.field_15517)) {
					int i = blockPos.getX();
					int j = blockPos.getY();
					int k = blockPos.getZ();
					world.playSound(
						playerEntity, blockPos, SoundEvents.field_15102, SoundCategory.field_15245, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int l = 0; l < 8; l++) {
						world.method_8406(ParticleTypes.field_11237, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
					}
				} else if (blockState.getBlock() instanceof class_2402) {
					if (((class_2402)blockState.getBlock()).method_10311(world, blockPos, blockState, ((BaseFluid)this.field_7905).getState(false))) {
						this.method_7727(playerEntity, world, blockPos);
					}
				} else {
					if (!world.isRemote && (bl || bl2) && !material.method_15797()) {
						world.breakBlock(blockPos, true);
					}

					this.method_7727(playerEntity, world, blockPos);
					world.setBlockState(blockPos, this.field_7905.getDefaultState().getBlockState(), 11);
				}

				return true;
			} else {
				return hitResult == null ? false : this.method_7731(playerEntity, world, hitResult.getBlockPos().method_10093(hitResult.field_1327), null);
			}
		}
	}

	protected void method_7727(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
		SoundEvent soundEvent = this.field_7905.matches(FluidTags.field_15518) ? SoundEvents.field_15010 : SoundEvents.field_14834;
		iWorld.playSound(playerEntity, blockPos, soundEvent, SoundCategory.field_15245, 1.0F, 1.0F);
	}
}
