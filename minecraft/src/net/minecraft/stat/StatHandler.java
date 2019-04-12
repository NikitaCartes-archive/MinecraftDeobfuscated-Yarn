package net.minecraft.stat;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;

public class StatHandler {
	protected final Object2IntMap<Stat<?>> statMap = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());

	public StatHandler() {
		this.statMap.defaultReturnValue(0);
	}

	public void increaseStat(PlayerEntity playerEntity, Stat<?> stat, int i) {
		this.setStat(playerEntity, stat, this.getStat(stat) + i);
	}

	public void setStat(PlayerEntity playerEntity, Stat<?> stat, int i) {
		this.statMap.put(stat, i);
	}

	@Environment(EnvType.CLIENT)
	public <T> int getStat(StatType<T> statType, T object) {
		return statType.hasStat(object) ? this.getStat(statType.getOrCreateStat(object)) : 0;
	}

	public int getStat(Stat<?> stat) {
		return this.statMap.getInt(stat);
	}
}
