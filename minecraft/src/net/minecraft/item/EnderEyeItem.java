package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class EnderEyeItem extends Item {
	public EnderEyeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (!blockState.isOf(Blocks.END_PORTAL_FRAME) || (Boolean)blockState.get(EndPortalFrameBlock.EYE)) {
			return ActionResult.PASS;
		} else if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockState blockState2 = blockState.with(EndPortalFrameBlock.EYE, Boolean.valueOf(true));
			Block.pushEntitiesUpBeforeBlockChange(blockState, blockState2, world, blockPos);
			world.setBlockState(blockPos, blockState2, Block.NOTIFY_LISTENERS);
			world.updateComparators(blockPos, Blocks.END_PORTAL_FRAME);
			context.getStack().decrement(1);
			world.syncWorldEvent(WorldEvents.END_PORTAL_FRAME_FILLED, blockPos, 0);
			BlockPattern.Result result = EndPortalFrameBlock.getCompletedFramePattern().searchAround(world, blockPos);
			if (result != null) {
				BlockPos blockPos2 = result.getFrontTopLeft().add(-3, 0, -3);

				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						world.setBlockState(blockPos2.add(i, 0, j), Blocks.END_PORTAL.getDefaultState(), Block.NOTIFY_LISTENERS);
					}
				}

				world.syncGlobalEvent(WorldEvents.END_PORTAL_OPENED, blockPos2.add(1, 0, 1), 0);
			}

			return ActionResult.CONSUME;
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 0;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);
		if (blockHitResult.getType() == HitResult.Type.BLOCK && world.getBlockState(blockHitResult.getBlockPos()).isOf(Blocks.END_PORTAL_FRAME)) {
			return TypedActionResult.pass(itemStack);
		} else {
			user.setCurrentHand(hand);
			if (world instanceof ServerWorld serverWorld) {
				BlockPos blockPos = serverWorld.locateStructure(StructureTags.EYE_OF_ENDER_LOCATED, user.getBlockPos(), 100, false);
				if (blockPos != null) {
					EyeOfEnderEntity eyeOfEnderEntity = new EyeOfEnderEntity(world, user.getX(), user.getBodyY(0.5), user.getZ());
					eyeOfEnderEntity.setItem(itemStack);
					eyeOfEnderEntity.initTargetPos(blockPos);
					world.emitGameEvent(GameEvent.PROJECTILE_SHOOT, eyeOfEnderEntity.getPos(), GameEvent.Emitter.of(user));
					world.spawnEntity(eyeOfEnderEntity);
					if (user instanceof ServerPlayerEntity serverPlayerEntity) {
						Criteria.USED_ENDER_EYE.trigger(serverPlayerEntity, blockPos);
					}

					float f = MathHelper.lerp(world.random.nextFloat(), 0.33F, 0.5F);
					world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, f);
					itemStack.decrementUnlessCreative(1, user);
					user.incrementStat(Stats.USED.getOrCreateStat(this));
					user.swingHand(hand, true);
					return TypedActionResult.success(itemStack);
				}
			}

			return TypedActionResult.consume(itemStack);
		}
	}
}
