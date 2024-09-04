package net.minecraft.entity.conversion;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.scoreboard.Team;

public record EntityConversionContext(EntityConversionType type, boolean keepEquipment, boolean preserveCanPickUpLoot, @Nullable Team team) {
	public static EntityConversionContext create(MobEntity entity, boolean keepEquipment, boolean preserveCanPickUpLoot) {
		return new EntityConversionContext(EntityConversionType.SINGLE, keepEquipment, preserveCanPickUpLoot, entity.getScoreboardTeam());
	}

	@FunctionalInterface
	public interface Finalizer<T extends MobEntity> {
		void finalizeConversion(T convertedEntity);
	}
}
