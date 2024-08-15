package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;

public class CelebrateRaidWinTask extends MultiTickTask<VillagerEntity> {
	@Nullable
	private Raid raid;

	public CelebrateRaidWinTask(int minRunTime, int maxRunTime) {
		super(ImmutableMap.of(), minRunTime, maxRunTime);
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		BlockPos blockPos = villagerEntity.getBlockPos();
		this.raid = serverWorld.getRaidAt(blockPos);
		return this.raid != null && this.raid.hasWon() && SeekSkyTask.isSkyVisible(serverWorld, villagerEntity, blockPos);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.raid != null && !this.raid.hasStopped();
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.raid = null;
		villagerEntity.getBrain().refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Random random = villagerEntity.getRandom();
		if (random.nextInt(100) == 0) {
			villagerEntity.playCelebrateSound();
		}

		if (random.nextInt(200) == 0 && SeekSkyTask.isSkyVisible(serverWorld, villagerEntity, villagerEntity.getBlockPos())) {
			DyeColor dyeColor = Util.getRandom(DyeColor.values(), random);
			int i = random.nextInt(3);
			ItemStack itemStack = this.createFirework(dyeColor, i);
			ProjectileEntity.spawn(
				new FireworkRocketEntity(villagerEntity.getWorld(), villagerEntity, villagerEntity.getX(), villagerEntity.getEyeY(), villagerEntity.getZ(), itemStack),
				serverWorld,
				itemStack
			);
		}
	}

	private ItemStack createFirework(DyeColor color, int flight) {
		ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
		itemStack.set(
			DataComponentTypes.FIREWORKS,
			new FireworksComponent(
				(byte)flight,
				List.of(new FireworkExplosionComponent(FireworkExplosionComponent.Type.BURST, IntList.of(color.getFireworkColor()), IntList.of(), false, false))
			)
		);
		return itemStack;
	}
}
