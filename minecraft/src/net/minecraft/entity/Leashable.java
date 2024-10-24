package net.minecraft.entity;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;

public interface Leashable {
	String LEASH_NBT_KEY = "leash";
	double MAX_LEASH_LENGTH = 10.0;
	double SHORT_LEASH_LENGTH = 6.0;

	@Nullable
	Leashable.LeashData getLeashData();

	void setLeashData(@Nullable Leashable.LeashData leashData);

	default boolean isLeashed() {
		return this.getLeashData() != null && this.getLeashData().leashHolder != null;
	}

	default boolean mightBeLeashed() {
		return this.getLeashData() != null;
	}

	default boolean canLeashAttachTo() {
		return this.canBeLeashed() && !this.isLeashed();
	}

	default boolean canBeLeashed() {
		return true;
	}

	default void setUnresolvedLeashHolderId(int unresolvedLeashHolderId) {
		this.setLeashData(new Leashable.LeashData(unresolvedLeashHolderId));
		detachLeash((Entity)this, false, false);
	}

	@Nullable
	default Leashable.LeashData readLeashDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("leash", NbtElement.COMPOUND_TYPE)) {
			return new Leashable.LeashData(Either.left(nbt.getCompound("leash").getUuid("UUID")));
		} else {
			if (nbt.contains("leash", NbtElement.INT_ARRAY_TYPE)) {
				Either<UUID, BlockPos> either = (Either<UUID, BlockPos>)NbtHelper.toBlockPos(nbt, "leash").map(Either::right).orElse(null);
				if (either != null) {
					return new Leashable.LeashData(either);
				}
			}

			return null;
		}
	}

	default void writeLeashDataToNbt(NbtCompound nbt, @Nullable Leashable.LeashData leashData) {
		if (leashData != null) {
			Either<UUID, BlockPos> either = leashData.unresolvedLeashData;
			if (leashData.leashHolder instanceof LeashKnotEntity leashKnotEntity) {
				either = Either.right(leashKnotEntity.getAttachedBlockPos());
			} else if (leashData.leashHolder != null) {
				either = Either.left(leashData.leashHolder.getUuid());
			}

			if (either != null) {
				nbt.put("leash", either.map(uuid -> {
					NbtCompound nbtCompound = new NbtCompound();
					nbtCompound.putUuid("UUID", uuid);
					return nbtCompound;
				}, NbtHelper::fromBlockPos));
			}
		}
	}

	private static <E extends Entity & Leashable> void resolveLeashData(E entity, Leashable.LeashData leashData) {
		if (leashData.unresolvedLeashData != null && entity.getWorld() instanceof ServerWorld serverWorld) {
			Optional<UUID> optional = leashData.unresolvedLeashData.left();
			Optional<BlockPos> optional2 = leashData.unresolvedLeashData.right();
			if (optional.isPresent()) {
				Entity entity2 = serverWorld.getEntity((UUID)optional.get());
				if (entity2 != null) {
					attachLeash(entity, entity2, true);
					return;
				}
			} else if (optional2.isPresent()) {
				attachLeash(entity, LeashKnotEntity.getOrCreate(serverWorld, (BlockPos)optional2.get()), true);
				return;
			}

			if (entity.age > 100) {
				entity.dropItem(serverWorld, Items.LEAD);
				entity.setLeashData(null);
			}
		}
	}

	default void detachLeash(boolean sendPacket, boolean dropItem) {
		detachLeash((Entity)this, sendPacket, dropItem);
	}

	private static <E extends Entity & Leashable> void detachLeash(E entity, boolean sendPacket, boolean dropItem) {
		Leashable.LeashData leashData = entity.getLeashData();
		if (leashData != null && leashData.leashHolder != null) {
			entity.setLeashData(null);
			if (entity.getWorld() instanceof ServerWorld serverWorld) {
				if (dropItem) {
					entity.dropItem(serverWorld, Items.LEAD);
				}

				if (sendPacket) {
					serverWorld.getChunkManager().sendToOtherNearbyPlayers(entity, new EntityAttachS2CPacket(entity, null));
				}
			}
		}
	}

	static <E extends Entity & Leashable> void tickLeash(ServerWorld world, E entity) {
		Leashable.LeashData leashData = entity.getLeashData();
		if (leashData != null && leashData.unresolvedLeashData != null) {
			resolveLeashData(entity, leashData);
		}

		if (leashData != null && leashData.leashHolder != null) {
			if (!entity.isAlive() || !leashData.leashHolder.isAlive()) {
				detachLeash(entity, true, world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS));
			}

			Entity entity2 = entity.getLeashHolder();
			if (entity2 != null && entity2.getWorld() == entity.getWorld()) {
				float f = entity.distanceTo(entity2);
				if (!entity.beforeLeashTick(entity2, f)) {
					return;
				}

				if ((double)f > 10.0) {
					entity.detachLeash();
				} else if ((double)f > 6.0) {
					entity.applyLeashElasticity(entity2, f);
					entity.limitFallDistance();
				} else {
					entity.onShortLeashTick(entity2);
				}
			}
		}
	}

	/**
	 * Called before the default leash-ticking logic.
	 * Subclasses can override this to add their own logic to it.
	 * <p>
	 * {@return whether the default logic should run after this.}
	 * 
	 * @see Leashable#tickLeash
	 */
	default boolean beforeLeashTick(Entity leashHolder, float distance) {
		return true;
	}

	default void detachLeash() {
		this.detachLeash(true, true);
	}

	default void onShortLeashTick(Entity entity) {
	}

	default void applyLeashElasticity(Entity leashHolder, float distance) {
		applyLeashElasticity((Entity)this, leashHolder, distance);
	}

	private static <E extends Entity & Leashable> void applyLeashElasticity(E entity, Entity leashHolder, float distance) {
		double d = (leashHolder.getX() - entity.getX()) / (double)distance;
		double e = (leashHolder.getY() - entity.getY()) / (double)distance;
		double f = (leashHolder.getZ() - entity.getZ()) / (double)distance;
		entity.setVelocity(entity.getVelocity().add(Math.copySign(d * d * 0.4, d), Math.copySign(e * e * 0.4, e), Math.copySign(f * f * 0.4, f)));
	}

	default void attachLeash(Entity leashHolder, boolean sendPacket) {
		attachLeash((Entity)this, leashHolder, sendPacket);
	}

	private static <E extends Entity & Leashable> void attachLeash(E entity, Entity leashHolder, boolean sendPacket) {
		Leashable.LeashData leashData = entity.getLeashData();
		if (leashData == null) {
			leashData = new Leashable.LeashData(leashHolder);
			entity.setLeashData(leashData);
		} else {
			leashData.setLeashHolder(leashHolder);
		}

		if (sendPacket && entity.getWorld() instanceof ServerWorld serverWorld) {
			serverWorld.getChunkManager().sendToOtherNearbyPlayers(entity, new EntityAttachS2CPacket(entity, leashHolder));
		}

		if (entity.hasVehicle()) {
			entity.stopRiding();
		}
	}

	@Nullable
	default Entity getLeashHolder() {
		return getLeashHolder((Entity)this);
	}

	@Nullable
	private static <E extends Entity & Leashable> Entity getLeashHolder(E entity) {
		Leashable.LeashData leashData = entity.getLeashData();
		if (leashData == null) {
			return null;
		} else {
			if (leashData.unresolvedLeashHolderId != 0 && entity.getWorld().isClient) {
				Entity var3 = entity.getWorld().getEntityById(leashData.unresolvedLeashHolderId);
				if (var3 instanceof Entity) {
					leashData.setLeashHolder(var3);
				}
			}

			return leashData.leashHolder;
		}
	}

	public static final class LeashData {
		int unresolvedLeashHolderId;
		@Nullable
		public Entity leashHolder;
		@Nullable
		public Either<UUID, BlockPos> unresolvedLeashData;

		LeashData(Either<UUID, BlockPos> unresolvedLeashData) {
			this.unresolvedLeashData = unresolvedLeashData;
		}

		LeashData(Entity leashHolder) {
			this.leashHolder = leashHolder;
		}

		LeashData(int unresolvedLeashHolderId) {
			this.unresolvedLeashHolderId = unresolvedLeashHolderId;
		}

		public void setLeashHolder(Entity leashHolder) {
			this.leashHolder = leashHolder;
			this.unresolvedLeashData = null;
			this.unresolvedLeashHolderId = 0;
		}
	}
}
