package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class VillagerData {
	private static final int[] field_18540 = new int[]{0, 10, 50, 100, 150};
	private final VillagerType field_17048;
	private final VillagerProfession field_17049;
	private final int level;

	public VillagerData(VillagerType villagerType, VillagerProfession villagerProfession, int i) {
		this.field_17048 = villagerType;
		this.field_17049 = villagerProfession;
		this.level = Math.max(1, i);
	}

	public VillagerData(Dynamic<?> dynamic) {
		this(
			Registry.VILLAGER_TYPE.method_10223(Identifier.create(dynamic.get("type").asString(""))),
			Registry.VILLAGER_PROFESSION.method_10223(Identifier.create(dynamic.get("profession").asString(""))),
			dynamic.get("level").asInt(1)
		);
	}

	public VillagerType method_16919() {
		return this.field_17048;
	}

	public VillagerProfession method_16924() {
		return this.field_17049;
	}

	public int getLevel() {
		return this.level;
	}

	public VillagerData method_16922(VillagerType villagerType) {
		return new VillagerData(villagerType, this.field_17049, this.level);
	}

	public VillagerData method_16921(VillagerProfession villagerProfession) {
		return new VillagerData(this.field_17048, villagerProfession, this.level);
	}

	public VillagerData withLevel(int i) {
		return new VillagerData(this.field_17048, this.field_17049, i);
	}

	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("type"),
				dynamicOps.createString(Registry.VILLAGER_TYPE.method_10221(this.field_17048).toString()),
				dynamicOps.createString("profession"),
				dynamicOps.createString(Registry.VILLAGER_PROFESSION.method_10221(this.field_17049).toString()),
				dynamicOps.createString("level"),
				dynamicOps.createInt(this.level)
			)
		);
	}

	public static int method_19194(int i) {
		return method_19196(i) ? field_18540[i - 1] : 0;
	}

	public static int method_19195(int i) {
		return method_19196(i) ? field_18540[i] : 0;
	}

	public static boolean method_19196(int i) {
		return i >= 1 && i < 5;
	}
}
