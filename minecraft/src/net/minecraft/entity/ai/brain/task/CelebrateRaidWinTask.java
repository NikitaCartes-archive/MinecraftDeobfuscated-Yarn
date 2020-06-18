package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;

public class CelebrateRaidWinTask extends Task<VillagerEntity> {
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
			FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(
				villagerEntity.world, villagerEntity, villagerEntity.getX(), villagerEntity.getEyeY(), villagerEntity.getZ(), itemStack
			);
			villagerEntity.world.spawnEntity(fireworkRocketEntity);
		}
	}

	private ItemStack createFirework(DyeColor color, int flight) {
		ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
		ItemStack itemStack2 = new ItemStack(Items.FIREWORK_STAR);
		CompoundTag compoundTag = itemStack2.getOrCreateSubTag("Explosion");
		List<Integer> list = Lists.newArrayList();
		list.add(color.getFireworkColor());
		compoundTag.putIntArray("Colors", list);
		compoundTag.putByte("Type", (byte)FireworkItem.Type.BURST.getId());
		CompoundTag compoundTag2 = itemStack.getOrCreateSubTag("Fireworks");
		ListTag listTag = new ListTag();
		CompoundTag compoundTag3 = itemStack2.getSubTag("Explosion");
		if (compoundTag3 != null) {
			listTag.add(compoundTag3);
		}

		compoundTag2.putByte("Flight", (byte)flight);
		if (!listTag.isEmpty()) {
			compoundTag2.put("Explosions", listTag);
		}

		return itemStack;
	}
}
