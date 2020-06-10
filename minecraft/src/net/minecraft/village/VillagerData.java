package net.minecraft.village;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;

public class VillagerData {
	private static final int[] LEVEL_BASE_EXPERIENCE = new int[]{0, 10, 70, 150, 250};
	public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registry.VILLAGER_TYPE
						.fieldOf("type")
						.withDefault((Supplier<? extends VillagerType>)(() -> VillagerType.PLAINS))
						.forGetter(villagerData -> villagerData.type),
					Registry.VILLAGER_PROFESSION
						.fieldOf("profession")
						.withDefault((Supplier<? extends VillagerProfession>)(() -> VillagerProfession.NONE))
						.forGetter(villagerData -> villagerData.profession),
					Codec.INT.fieldOf("level").withDefault(1).forGetter(villagerData -> villagerData.level)
				)
				.apply(instance, VillagerData::new)
	);
	private final VillagerType type;
	private final VillagerProfession profession;
	private final int level;

	public VillagerData(VillagerType villagerType, VillagerProfession villagerProfession, int i) {
		this.type = villagerType;
		this.profession = villagerProfession;
		this.level = Math.max(1, i);
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
