/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.SeekSkyTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import org.jetbrains.annotations.Nullable;

public class CelebrateRaidWinTask
extends Task<VillagerEntity> {
    @Nullable
    private Raid raid;

    public CelebrateRaidWinTask(int minRunTime, int maxRunTime) {
        super(ImmutableMap.of(), minRunTime, maxRunTime);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        BlockPos blockPos = villagerEntity.getBlockPos();
        this.raid = serverWorld.getRaidAt(blockPos);
        return this.raid != null && this.raid.hasWon() && SeekSkyTask.isSkyVisible(serverWorld, villagerEntity, blockPos);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.raid != null && !this.raid.hasStopped();
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        this.raid = null;
        villagerEntity.getBrain().refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Random random = villagerEntity.getRandom();
        if (random.nextInt(100) == 0) {
            villagerEntity.playCelebrateSound();
        }
        if (random.nextInt(200) == 0 && SeekSkyTask.isSkyVisible(serverWorld, villagerEntity, villagerEntity.getBlockPos())) {
            DyeColor dyeColor = Util.getRandom(DyeColor.values(), random);
            int i = random.nextInt(3);
            ItemStack itemStack = this.createFirework(dyeColor, i);
            FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(villagerEntity.world, villagerEntity, villagerEntity.getX(), villagerEntity.getEyeY(), villagerEntity.getZ(), itemStack);
            villagerEntity.world.spawnEntity(fireworkRocketEntity);
        }
    }

    private ItemStack createFirework(DyeColor color, int flight) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack itemStack2 = new ItemStack(Items.FIREWORK_STAR);
        NbtCompound nbtCompound = itemStack2.getOrCreateSubTag("Explosion");
        ArrayList<Integer> list = Lists.newArrayList();
        list.add(color.getFireworkColor());
        nbtCompound.putIntArray("Colors", list);
        nbtCompound.putByte("Type", (byte)FireworkItem.Type.BURST.getId());
        NbtCompound nbtCompound2 = itemStack.getOrCreateSubTag("Fireworks");
        NbtList nbtList = new NbtList();
        NbtCompound nbtCompound3 = itemStack2.getSubTag("Explosion");
        if (nbtCompound3 != null) {
            nbtList.add(nbtCompound3);
        }
        nbtCompound2.putByte("Flight", (byte)flight);
        if (!nbtList.isEmpty()) {
            nbtCompound2.put("Explosions", nbtList);
        }
        return itemStack;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }
}

