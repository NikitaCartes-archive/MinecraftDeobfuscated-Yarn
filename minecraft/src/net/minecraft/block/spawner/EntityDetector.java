package net.minecraft.block.spawner;

import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public interface EntityDetector {
	EntityDetector SURVIVAL_PLAYER = (world, center, radius) -> world.getPlayers(
				player -> player.getBlockPos().isWithinDistance(center, (double)radius) && !player.isCreative() && !player.isSpectator()
			)
			.stream()
			.map(Entity::getUuid)
			.toList();
	EntityDetector SHEEP = (world, center, radius) -> {
		Box box = new Box(center).expand((double)radius);
		return world.getEntitiesByType(EntityType.SHEEP, box, LivingEntity::isAlive).stream().map(Entity::getUuid).toList();
	};

	List<UUID> detect(ServerWorld world, BlockPos center, int radius);
}
