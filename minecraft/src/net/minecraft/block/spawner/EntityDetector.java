package net.minecraft.block.spawner;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public interface EntityDetector {
	EntityDetector SURVIVAL_PLAYERS = (world, selector, center, radius, spawner) -> selector.getPlayers(
				world, player -> player.getBlockPos().isWithinDistance(center, radius) && !player.isCreative() && !player.isSpectator()
			)
			.stream()
			.filter(entity -> !spawner || hasLineOfSight(world, center.toCenterPos(), entity.getEyePos()))
			.map(Entity::getUuid)
			.toList();
	EntityDetector NON_SPECTATOR_PLAYERS = (world, selector, center, radius, spawner) -> selector.getPlayers(
				world, player -> player.getBlockPos().isWithinDistance(center, radius) && !player.isSpectator()
			)
			.stream()
			.filter(entity -> !spawner || hasLineOfSight(world, center.toCenterPos(), entity.getEyePos()))
			.map(Entity::getUuid)
			.toList();
	EntityDetector SHEEP = (world, selector, center, radius, spawner) -> {
		Box box = new Box(center).expand(radius);
		return selector.getEntities(world, EntityType.SHEEP, box, LivingEntity::isAlive)
			.stream()
			.filter(entity -> !spawner || hasLineOfSight(world, center.toCenterPos(), entity.getEyePos()))
			.map(Entity::getUuid)
			.toList();
	};

	List<UUID> detect(ServerWorld world, EntityDetector.Selector selector, BlockPos center, double radius, boolean spawner);

	private static boolean hasLineOfSight(World world, Vec3d pos, Vec3d entityEyePos) {
		BlockHitResult blockHitResult = world.raycast(
			new RaycastContext(entityEyePos, pos, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent())
		);
		return blockHitResult.getBlockPos().equals(BlockPos.ofFloored(pos)) || blockHitResult.getType() == HitResult.Type.MISS;
	}

	public interface Selector {
		EntityDetector.Selector IN_WORLD = new EntityDetector.Selector() {
			@Override
			public List<ServerPlayerEntity> getPlayers(ServerWorld world, Predicate<? super PlayerEntity> predicate) {
				return world.getPlayers(predicate);
			}

			@Override
			public <T extends Entity> List<T> getEntities(ServerWorld world, TypeFilter<Entity, T> typeFilter, Box box, Predicate<? super T> predicate) {
				return world.getEntitiesByType(typeFilter, box, predicate);
			}
		};

		List<? extends PlayerEntity> getPlayers(ServerWorld world, Predicate<? super PlayerEntity> predicate);

		<T extends Entity> List<T> getEntities(ServerWorld world, TypeFilter<Entity, T> typeFilter, Box box, Predicate<? super T> predicate);

		static EntityDetector.Selector ofPlayer(PlayerEntity player) {
			return ofPlayers(List.of(player));
		}

		static EntityDetector.Selector ofPlayers(List<PlayerEntity> players) {
			return new EntityDetector.Selector() {
				@Override
				public List<PlayerEntity> getPlayers(ServerWorld world, Predicate<? super PlayerEntity> predicate) {
					return players.stream().filter(predicate).toList();
				}

				@Override
				public <T extends Entity> List<T> getEntities(ServerWorld world, TypeFilter<Entity, T> typeFilter, Box box, Predicate<? super T> predicate) {
					return players.stream().map(typeFilter::downcast).filter(Objects::nonNull).filter(predicate).toList();
				}
			};
		}
	}
}
