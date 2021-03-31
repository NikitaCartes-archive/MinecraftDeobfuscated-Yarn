package net.minecraft.stat;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;

public class StatHandler {
	protected final Object2IntMap<Stat<?>> statMap = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());

	public StatHandler() {
		this.statMap.defaultReturnValue(0);
	}

	public void increaseStat(PlayerEntity player, Stat<?> stat, int value) {
		int i = (int)Math.min((long)this.getStat(stat) + (long)value, 2147483647L);
		this.setStat(player, stat, i);
	}

	public void setStat(PlayerEntity player, Stat<?> stat, int value) {
		this.statMap.put(stat, value);
	}

	public <T> int getStat(StatType<T> type, T stat) {
		return type.hasStat(stat) ? this.getStat(type.getOrCreateStat(stat)) : 0;
	}

	public int getStat(Stat<?> stat) {
		return this.statMap.getInt(stat);
	}
}
