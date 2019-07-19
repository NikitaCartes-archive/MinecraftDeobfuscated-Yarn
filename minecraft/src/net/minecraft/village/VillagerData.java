package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class VillagerData {
	private static final int[] LEVEL_BASE_EXPERIENCE = new int[]{0, 10, 70, 150, 250};
	private final VillagerType type;
	private final VillagerProfession profession;
	private final int level;

	public VillagerData(VillagerType villagerType, VillagerProfession villagerProfession, int level) {
		this.type = villagerType;
		this.profession = villagerProfession;
		this.level = Math.max(1, level);
	}

	public VillagerData(Dynamic<?> dynamic) {
		this(
			Registry.VILLAGER_TYPE.get(Identifier.tryParse(dynamic.get("type").asString(""))),
			Registry.VILLAGER_PROFESSION.get(Identifier.tryParse(dynamic.get("profession").asString(""))),
			dynamic.get("level").asInt(1)
		);
	}

	public VillagerType getType() {
		return this.type;
	}

	public VillagerProfession getProfession() {
		return this.profession;
	}

	public int getLevel() {
		return this.level;
	}

	public VillagerData withType(VillagerType villagerType) {
		return new VillagerData(villagerType, this.profession, this.level);
	}

	public VillagerData withProfession(VillagerProfession villagerProfession) {
		return new VillagerData(this.type, villagerProfession, this.level);
	}

	public VillagerData withLevel(int level) {
		return new VillagerData(this.type, this.profession, level);
	}

	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createMap(
			ImmutableMap.of(
				ops.createString("type"),
				ops.createString(Registry.VILLAGER_TYPE.getId(this.type).toString()),
				ops.createString("profession"),
				ops.createString(Registry.VILLAGER_PROFESSION.getId(this.profession).toString()),
				ops.createString("level"),
				ops.createInt(this.level)
			)
		);
	}

	@Environment(EnvType.CLIENT)
	public static int getLowerLevelExperience(int level) {
		return canLevelUp(level) ? LEVEL_BASE_EXPERIENCE[level - 1] : 0;
	}

	public static int getUpperLevelExperience(int level) {
		return canLevelUp(level) ? LEVEL_BASE_EXPERIENCE[level] : 0;
	}

	public static boolean canLevelUp(int level) {
		return level >= 1 && level < 5;
	}
}
