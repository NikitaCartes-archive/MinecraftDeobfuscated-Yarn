package net.minecraft.block.dispenser;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.event.GameEvent;

public class ShearsDispenserBehavior extends FallibleItemDispenserBehavior {
	@Override
	protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		ServerWorld serverWorld = pointer.getWorld();
		if (!serverWorld.isClient()) {
			BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
			this.setSuccess(tryShearBlock(serverWorld, blockPos) || tryShearEntity(serverWorld, blockPos));
			if (this.isSuccess() && stack.damage(1, serverWorld.getRandom(), null)) {
				stack.setCount(0);
			}
		}

		return stack;
	}

	private static boolean tryShearBlock(ServerWorld world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.isIn(BlockTags.BEEHIVES, state -> state.contains(BeehiveBlock.HONEY_LEVEL) && state.getBlock() instanceof BeehiveBlock)) {
			int i = (Integer)blockState.get(BeehiveBlock.HONEY_LEVEL);
			if (i >= 5) {
				world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
				BeehiveBlock.dropHoneycomb(world, pos);
				((BeehiveBlock)blockState.getBlock()).takeHoney(world, blockState, pos, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
				world.emitGameEvent(null, GameEvent.SHEAR, pos);
				return true;
			}
		}

		return false;
	}

	private static boolean tryShearEntity(ServerWorld world, BlockPos pos) {
		for (LivingEntity livingEntity : world.getEntitiesByClass(LivingEntity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR)) {
			if (livingEntity instanceof Shearable shearable && shearable.isShearable()) {
				shearable.sheared(SoundCategory.BLOCKS);
				world.emitGameEvent(null, GameEvent.SHEAR, pos);
				return true;
			}
		}

		return false;
	}
}
