package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;

public class EntitySelector {
	private final int count;
	private final boolean includesEntities;
	private final boolean field_10829;
	private final Predicate<Entity> basePredicate;
	private final NumberRange.Float distance;
	private final Function<Vec3d, Vec3d> positionOffset;
	@Nullable
	private final BoundingBox box;
	private final BiConsumer<Vec3d, List<? extends Entity>> sorter;
	private final boolean field_10828;
	@Nullable
	private final String playerName;
	@Nullable
	private final UUID entityId;
	private final Class<? extends Entity> type;
	private final boolean checkPermissions;

	public EntitySelector(
		int i,
		boolean bl,
		boolean bl2,
		Predicate<Entity> predicate,
		NumberRange.Float float_,
		Function<Vec3d, Vec3d> function,
		@Nullable BoundingBox boundingBox,
		BiConsumer<Vec3d, List<? extends Entity>> biConsumer,
		boolean bl3,
		@Nullable String string,
		@Nullable UUID uUID,
		Class<? extends Entity> class_,
		boolean bl4
	) {
		this.count = i;
		this.includesEntities = bl;
		this.field_10829 = bl2;
		this.basePredicate = predicate;
		this.distance = float_;
		this.positionOffset = function;
		this.box = boundingBox;
		this.sorter = biConsumer;
		this.field_10828 = bl3;
		this.playerName = string;
		this.entityId = uUID;
		this.type = class_;
		this.checkPermissions = bl4;
	}

	public int getCount() {
		return this.count;
	}

	public boolean includesEntities() {
		return this.includesEntities;
	}

	public boolean method_9820() {
		return this.field_10828;
	}

	public boolean method_9821() {
		return this.field_10829;
	}

	private void check(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		if (this.checkPermissions && !serverCommandSource.hasPermissionLevel(2)) {
			throw EntityArgumentType.NOT_ALLOWED_EXCEPTION.create();
		}
	}

	public Entity getEntity(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		this.check(serverCommandSource);
		List<? extends Entity> list = this.getEntities(serverCommandSource);
		if (list.isEmpty()) {
			throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
		} else if (list.size() > 1) {
			throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
		} else {
			return (Entity)list.get(0);
		}
	}

	public List<? extends Entity> getEntities(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		this.check(serverCommandSource);
		if (!this.includesEntities) {
			return this.getPlayers(serverCommandSource);
		} else if (this.playerName != null) {
			ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(this.playerName);
			return (List<? extends Entity>)(serverPlayerEntity == null ? Collections.emptyList() : Lists.newArrayList(serverPlayerEntity));
		} else if (this.entityId != null) {
			for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
				Entity entity = serverWorld.getEntityByUuid(this.entityId);
				if (entity != null) {
					return Lists.newArrayList(entity);
				}
			}

			return Collections.emptyList();
		} else {
			Vec3d vec3d = (Vec3d)this.positionOffset.apply(serverCommandSource.getPosition());
			Predicate<Entity> predicate = this.getPositionPredicate(vec3d);
			if (this.field_10828) {
				return (List<? extends Entity>)(serverCommandSource.getEntity() != null && predicate.test(serverCommandSource.getEntity())
					? Lists.newArrayList(serverCommandSource.getEntity())
					: Collections.emptyList());
			} else {
				List<Entity> list = Lists.<Entity>newArrayList();
				if (this.method_9821()) {
					this.method_9823(list, serverCommandSource.getWorld(), vec3d, predicate);
				} else {
					for (ServerWorld serverWorld2 : serverCommandSource.getMinecraftServer().getWorlds()) {
						this.method_9823(list, serverWorld2, vec3d, predicate);
					}
				}

				return this.getEntities(vec3d, list);
			}
		}
	}

	private void method_9823(List<Entity> list, ServerWorld serverWorld, Vec3d vec3d, Predicate<Entity> predicate) {
		if (this.box != null) {
			list.addAll(serverWorld.getEntities(this.type, this.box.offset(vec3d), predicate::test));
		} else {
			list.addAll(serverWorld.getEntities(this.type, predicate::test));
		}
	}

	public ServerPlayerEntity getPlayer(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		this.check(serverCommandSource);
		List<ServerPlayerEntity> list = this.getPlayers(serverCommandSource);
		if (list.size() != 1) {
			throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
		} else {
			return (ServerPlayerEntity)list.get(0);
		}
	}

	public List<ServerPlayerEntity> getPlayers(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		this.check(serverCommandSource);
		if (this.playerName != null) {
			ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(this.playerName);
			return (List<ServerPlayerEntity>)(serverPlayerEntity == null ? Collections.emptyList() : Lists.<ServerPlayerEntity>newArrayList(serverPlayerEntity));
		} else if (this.entityId != null) {
			ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(this.entityId);
			return (List<ServerPlayerEntity>)(serverPlayerEntity == null ? Collections.emptyList() : Lists.<ServerPlayerEntity>newArrayList(serverPlayerEntity));
		} else {
			Vec3d vec3d = (Vec3d)this.positionOffset.apply(serverCommandSource.getPosition());
			Predicate<Entity> predicate = this.getPositionPredicate(vec3d);
			if (this.field_10828) {
				if (serverCommandSource.getEntity() instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)serverCommandSource.getEntity();
					if (predicate.test(serverPlayerEntity2)) {
						return Lists.<ServerPlayerEntity>newArrayList(serverPlayerEntity2);
					}
				}

				return Collections.emptyList();
			} else {
				List<ServerPlayerEntity> list;
				if (this.method_9821()) {
					list = serverCommandSource.getWorld().getPlayers(ServerPlayerEntity.class, predicate::test);
				} else {
					list = Lists.<ServerPlayerEntity>newArrayList();

					for (ServerPlayerEntity serverPlayerEntity3 : serverCommandSource.getMinecraftServer().getPlayerManager().getPlayerList()) {
						if (predicate.test(serverPlayerEntity3)) {
							list.add(serverPlayerEntity3);
						}
					}
				}

				return this.getEntities(vec3d, list);
			}
		}
	}

	private Predicate<Entity> getPositionPredicate(Vec3d vec3d) {
		Predicate<Entity> predicate = this.basePredicate;
		if (this.box != null) {
			BoundingBox boundingBox = this.box.offset(vec3d);
			predicate = predicate.and(entity -> boundingBox.intersects(entity.getBoundingBox()));
		}

		if (!this.distance.isDummy()) {
			predicate = predicate.and(entity -> this.distance.matchesSquared(entity.squaredDistanceTo(vec3d)));
		}

		return predicate;
	}

	private <T extends Entity> List<T> getEntities(Vec3d vec3d, List<T> list) {
		if (list.size() > 1) {
			this.sorter.accept(vec3d, list);
		}

		return list.subList(0, Math.min(this.count, list.size()));
	}

	public static TextComponent getNames(List<? extends Entity> list) {
		return TextFormatter.join(list, Entity::getDisplayName);
	}
}
