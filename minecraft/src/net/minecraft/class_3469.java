package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;

public class class_3469 {
	protected final Object2IntMap<Stat<?>> field_15431 = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());

	public class_3469() {
		this.field_15431.defaultReturnValue(0);
	}

	public void method_15022(PlayerEntity playerEntity, Stat<?> stat, int i) {
		this.method_15023(playerEntity, stat, this.method_15025(stat) + i);
	}

	public void method_15023(PlayerEntity playerEntity, Stat<?> stat, int i) {
		this.field_15431.put(stat, i);
	}

	@Environment(EnvType.CLIENT)
	public <T> int method_15024(StatType<T> statType, T object) {
		return statType.method_14958(object) ? this.method_15025(statType.method_14956(object)) : 0;
	}

	public int method_15025(Stat<?> stat) {
		return this.field_15431.getInt(stat);
	}
}
