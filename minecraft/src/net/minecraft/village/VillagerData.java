package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class VillagerData {
	private final VillagerType type;
	private final VillagerProfession profession;
	private final int level;

	public VillagerData(VillagerType villagerType, VillagerProfession villagerProfession, int i) {
		this.type = villagerType;
		this.profession = villagerProfession;
		this.level = Math.max(1, i);
	}

	public VillagerData(Dynamic<?> dynamic) {
		this(
			Registry.VILLAGER_TYPE.get(Identifier.create(dynamic.getString("type"))),
			Registry.VILLAGER_PROFESSION.get(Identifier.create(dynamic.getString("profession"))),
			dynamic.getInt("level")
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

	public VillagerData withLevel(int i) {
		return new VillagerData(this.type, this.profession, i);
	}

	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("type"),
				dynamicOps.createString(Registry.VILLAGER_TYPE.getId(this.type).toString()),
				dynamicOps.createString("profession"),
				dynamicOps.createString(Registry.VILLAGER_PROFESSION.getId(this.profession).toString()),
				dynamicOps.createString("level"),
				dynamicOps.createInt(this.level)
			)
		);
	}
}
