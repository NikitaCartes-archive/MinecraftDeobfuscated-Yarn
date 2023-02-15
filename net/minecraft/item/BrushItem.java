/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SuspiciousSandBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BrushItem
extends Item {
    public static final int field_42682 = 10;
    private static final int field_42683 = 225;

    public BrushItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        if (playerEntity != null) {
            playerEntity.setCurrentHand(context.getHand());
        }
        return ActionResult.CONSUME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 225;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks < 0 || !(user instanceof PlayerEntity)) {
            user.stopUsingItem();
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity)user;
        BlockHitResult blockHitResult = Item.raycast(world, playerEntity, RaycastContext.FluidHandling.NONE);
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            user.stopUsingItem();
            return;
        }
        int i = this.getMaxUseTime(stack) - remainingUseTicks + 1;
        if (i == 1 || i % 10 == 0) {
            SuspiciousSandBlockEntity suspiciousSandBlockEntity;
            boolean bl;
            BlockEntity blockEntity;
            BlockState blockState = world.getBlockState(blockPos);
            this.addDustParticles(world, blockHitResult, blockState, user.getRotationVec(0.0f));
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_BRUSH_BRUSHING, SoundCategory.PLAYERS);
            if (!world.isClient() && blockState.isOf(Blocks.SUSPICIOUS_SAND) && (blockEntity = world.getBlockEntity(blockPos)) instanceof SuspiciousSandBlockEntity && (bl = (suspiciousSandBlockEntity = (SuspiciousSandBlockEntity)blockEntity).brush(world.getTime(), playerEntity, blockHitResult.getSide()))) {
                stack.damage(1, user, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
        }
    }

    public void addDustParticles(World world, BlockHitResult hitResult, BlockState state, Vec3d vec3d) {
        double d = 3.0;
        int i = world.getRandom().nextBetweenExclusive(7, 12);
        BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
        Direction direction = hitResult.getSide();
        class_8163 lv = class_8163.method_49185(vec3d, direction);
        Vec3d vec3d2 = hitResult.getPos();
        for (int j = 0; j < i; ++j) {
            world.addParticle(blockStateParticleEffect, vec3d2.x - (double)(direction == Direction.WEST ? 1.0E-6f : 0.0f), vec3d2.y, vec3d2.z - (double)(direction == Direction.NORTH ? 1.0E-6f : 0.0f), lv.xd() * 3.0 * world.getRandom().nextDouble(), 0.0, lv.zd() * 3.0 * world.getRandom().nextDouble());
        }
    }

    record class_8163(double xd, double yd, double zd) {
        private static final double field_42685 = 1.0;
        private static final double field_42686 = 0.1;

        public static class_8163 method_49185(Vec3d vec3d, Direction direction) {
            double d = 0.0;
            return switch (direction) {
                default -> throw new IncompatibleClassChangeError();
                case Direction.DOWN -> new class_8163(-vec3d.getX(), 0.0, vec3d.getZ());
                case Direction.UP -> new class_8163(vec3d.getZ(), 0.0, -vec3d.getX());
                case Direction.NORTH -> new class_8163(1.0, 0.0, -0.1);
                case Direction.SOUTH -> new class_8163(-1.0, 0.0, 0.1);
                case Direction.WEST -> new class_8163(-0.1, 0.0, -1.0);
                case Direction.EAST -> new class_8163(0.1, 0.0, 1.0);
            };
        }
    }
}

