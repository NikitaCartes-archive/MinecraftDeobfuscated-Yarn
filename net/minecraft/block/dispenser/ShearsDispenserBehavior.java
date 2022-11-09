/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.dispenser;

import java.util.List;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.Entity;
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

public class ShearsDispenserBehavior
extends FallibleItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld world = pointer.getWorld();
        if (!world.isClient()) {
            BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            this.setSuccess(ShearsDispenserBehavior.tryShearBlock(world, blockPos) || ShearsDispenserBehavior.tryShearEntity(world, blockPos));
            if (this.isSuccess() && stack.damage(1, world.getRandom(), null)) {
                stack.setCount(0);
            }
        }
        return stack;
    }

    private static boolean tryShearBlock(ServerWorld world, BlockPos pos) {
        int i;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isIn(BlockTags.BEEHIVES, state -> state.contains(BeehiveBlock.HONEY_LEVEL) && state.getBlock() instanceof BeehiveBlock) && (i = blockState.get(BeehiveBlock.HONEY_LEVEL).intValue()) >= 5) {
            world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
            BeehiveBlock.dropHoneycomb(world, pos);
            ((BeehiveBlock)blockState.getBlock()).takeHoney(world, blockState, pos, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
            world.emitGameEvent(null, GameEvent.SHEAR, pos);
            return true;
        }
        return false;
    }

    private static boolean tryShearEntity(ServerWorld world, BlockPos pos) {
        List<Entity> list = world.getEntitiesByClass(LivingEntity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR);
        for (LivingEntity livingEntity : list) {
            Shearable shearable;
            if (!(livingEntity instanceof Shearable) || !(shearable = (Shearable)((Object)livingEntity)).isShearable()) continue;
            shearable.sheared(SoundCategory.BLOCKS);
            world.emitGameEvent(null, GameEvent.SHEAR, pos);
            return true;
        }
        return false;
    }
}

