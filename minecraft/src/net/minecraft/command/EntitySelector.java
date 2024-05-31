package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class EntitySelector {
	public static final int MAX_VALUE = Integer.MAX_VALUE;
	public static final BiConsumer<Vec3d, List<? extends Entity>> ARBITRARY = (pos, entities) -> {
	};
	private static final TypeFilter<Entity, ?> PASSTHROUGH_FILTER = new TypeFilter<Entity, Entity>() {
		public Entity downcast(Entity entity) {
			return entity;
		}

		@Override
		public Class<? extends Entity> getBaseClass() {
			return Entity.class;
		}
	};
	private final int limit;
	private final boolean includesNonPlayers;
	private final boolean localWorldOnly;
	private final List<Predicate<Entity>> predicates;
	private final NumberRange.DoubleRange distance;
	private final Function<Vec3d, Vec3d> positionOffset;
	@Nullable
	private final Box box;
	private final BiConsumer<Vec3d, List<? extends Entity>> sorter;
	private final boolean senderOnly;
	@Nullable
	private final String playerName;
	@Nullable
	private final UUID uuid;
	private final TypeFilter<Entity, ?> entityFilter;
	private final boolean usesAt;

	public EntitySelector(
		int count,
		boolean includesNonPlayers,
		boolean localWorldOnly,
		List<Predicate<Entity>> predicates,
		NumberRange.DoubleRange distance,
		Function<Vec3d, Vec3d> positionOffset,
		@Nullable Box box,
		BiConsumer<Vec3d, List<? extends Entity>> sorter,
		boolean senderOnly,
		@Nullable String playerName,
		@Nullable UUID uuid,
		@Nullable EntityType<?> type,
		boolean usesAt
	) {
		this.limit = count;
		this.includesNonPlayers = includesNonPlayers;
		this.localWorldOnly = localWorldOnly;
		this.predicates = predicates;
		this.distance = distance;
		this.positionOffset = positionOffset;
		this.box = box;
		this.sorter = sorter;
		this.senderOnly = senderOnly;
		this.playerName = playerName;
		this.uuid = uuid;
		this.entityFilter = (TypeFilter<Entity, ?>)(type == null ? PASSTHROUGH_FILTER : type);
		this.usesAt = usesAt;
	}

	public int getLimit() {
		return this.limit;
	}

	public boolean includesNonPlayers() {
		return this.includesNonPlayers;
	}

	public boolean isSenderOnly() {
		return this.senderOnly;
	}

	public boolean isLocalWorldOnly() {
		return this.localWorldOnly;
	}

	public boolean usesAt() {
		return this.usesAt;
	}

	private void checkSourcePermission(ServerCommandSource source) throws CommandSyntaxException {
		if (this.usesAt && !source.hasPermissionLevel(2)) {
			throw EntityArgumentType.NOT_ALLOWED_EXCEPTION.create();
		}
	}

	public Entity getEntity(ServerCommandSource source) throws CommandSyntaxException {
		this.checkSourcePermission(source);
		List<? extends Entity> list = this.getEntities(source);
		if (list.isEmpty()) {
			throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
		} else if (list.size() > 1) {
			throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
		} else {
			return (Entity)list.get(0);
		}
	}

	public List<? extends Entity> getEntities(ServerCommandSource source) throws CommandSyntaxException {
		this.checkSourcePermission(source);
		if (!this.includesNonPlayers) {
			return this.getPlayers(source);
		} else if (this.playerName != null) {
			ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(this.playerName);
			return serverPlayerEntity == null ? List.of() : List.of(serverPlayerEntity);
		} else if (this.uuid != null) {
			for (ServerWorld serverWorld : source.getServer().getWorlds()) {
				Entity entity = serverWorld.getEntity(this.uuid);
				if (entity != null) {
					if (entity.getType().isEnabled(source.getEnabledFeatures())) {
						return List.of(entity);
					}
					break;
				}
			}

			return List.of();
		} else {
			Vec3d vec3d = (Vec3d)this.positionOffset.apply(source.getPosition());
			Box box = this.getOffsetBox(vec3d);
			if (this.senderOnly) {
				Predicate<Entity> predicate = this.getPositionPredicate(vec3d, box, null);
				return source.getEntity() != null && predicate.test(source.getEntity()) ? List.of(source.getEntity()) : List.of();
			} else {
				Predicate<Entity> predicate = this.getPositionPredicate(vec3d, box, source.getEnabledFeatures());
				List<Entity> list = new ObjectArrayList<>();
				if (this.isLocalWorldOnly()) {
					this.appendEntitiesFromWorld(list, source.getWorld(), box, predicate);
				} else {
					for (ServerWorld serverWorld2 : source.getServer().getWorlds()) {
						this.appendEntitiesFromWorld(list, serverWorld2, box, predicate);
					}
				}

				return this.getEntities(vec3d, list);
			}
		}
	}

	private void appendEntitiesFromWorld(List<Entity> entities, ServerWorld world, @Nullable Box box, Predicate<Entity> predicate) {
		int i = this.getAppendLimit();
		if (entities.size() < i) {
			if (box != null) {
				world.collectEntitiesByType(this.entityFilter, box, predicate, entities, i);
			} else {
				world.collectEntitiesByType(this.entityFilter, predicate, entities, i);
			}
		}
	}

	private int getAppendLimit() {
		return this.sorter == ARBITRARY ? this.limit : Integer.MAX_VALUE;
	}

	public ServerPlayerEntity getPlayer(ServerCommandSource source) throws CommandSyntaxException {
		this.checkSourcePermission(source);
		List<ServerPlayerEntity> list = this.getPlayers(source);
		if (list.size() != 1) {
			throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
		} else {
			return (ServerPlayerEntity)list.get(0);
		}
	}

	public List<ServerPlayerEntity> getPlayers(ServerCommandSource source) throws CommandSyntaxException {
		this.checkSourcePermission(source);
		if (this.playerName != null) {
			ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(this.playerName);
			return serverPlayerEntity == null ? List.of() : List.of(serverPlayerEntity);
		} else if (this.uuid != null) {
			ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(this.uuid);
			return serverPlayerEntity == null ? List.of() : List.of(serverPlayerEntity);
		} else {
			Vec3d vec3d = (Vec3d)this.positionOffset.apply(source.getPosition());
			Box box = this.getOffsetBox(vec3d);
			Predicate<Entity> predicate = this.getPositionPredicate(vec3d, box, null);
			if (this.senderOnly) {
				if (source.getEntity() instanceof ServerPlayerEntity serverPlayerEntity2 && predicate.test(serverPlayerEntity2)) {
					return List.of(serverPlayerEntity2);
				}

				return List.of();
			} else {
				int i = this.getAppendLimit();
				List<ServerPlayerEntity> list;
				if (this.isLocalWorldOnly()) {
					list = source.getWorld().getPlayers(predicate, i);
				} else {
					list = new ObjectArrayList<>();

					for (ServerPlayerEntity serverPlayerEntity3 : source.getServer().getPlayerManager().getPlayerList()) {
						if (predicate.test(serverPlayerEntity3)) {
							list.add(serverPlayerEntity3);
							if (list.size() >= i) {
								return list;
							}
						}
					}
				}

				return this.getEntities(vec3d, list);
			}
		}
	}

	@Nullable
	private Box getOffsetBox(Vec3d offset) {
		return this.box != null ? this.box.offset(offset) : null;
	}

	private Predicate<Entity> getPositionPredicate(Vec3d pos, @Nullable Box box, @Nullable FeatureSet enabledFeatures) {
		boolean bl = enabledFeatures != null;
		boolean bl2 = box != null;
		boolean bl3 = !this.distance.isDummy();
		int i = (bl ? 1 : 0) + (bl2 ? 1 : 0) + (bl3 ? 1 : 0);
		List<Predicate<Entity>> list;
		if (i == 0) {
			list = this.predicates;
		} else {
			List<Predicate<Entity>> list2 = new ObjectArrayList<>(this.predicates.size() + i);
			list2.addAll(this.predicates);
			if (bl) {
				list2.add((Predicate)entity -> entity.getType().isEnabled(enabledFeatures));
			}

			if (bl2) {
				list2.add((Predicate)entity -> box.intersects(entity.getBoundingBox()));
			}

			if (bl3) {
				list2.add((Predicate)entity -> this.distance.testSqrt(entity.squaredDistanceTo(pos)));
			}

			list = list2;
		}

		return Util.allOf(list);
	}

	private <T extends Entity> List<T> getEntities(Vec3d pos, List<T> entities) {
		if (entities.size() > 1) {
			this.sorter.accept(pos, entities);
		}

		return entities.subList(0, Math.min(this.limit, entities.size()));
	}

	public static Text getNames(List<? extends Entity> entities) {
		return Texts.join(entities, Entity::getDisplayName);
	}
}
