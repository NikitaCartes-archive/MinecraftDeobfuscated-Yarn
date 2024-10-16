package net.minecraft.entity.projectile.thrown;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class EnderPearlEntity extends ThrownItemEntity {
	private long chunkTicketExpiryTicks = 0L;

	public EnderPearlEntity(EntityType<? extends EnderPearlEntity> entityType, World world) {
		super(entityType, world);
	}

	public EnderPearlEntity(World world, LivingEntity owner, ItemStack stack) {
		super(EntityType.ENDER_PEARL, owner, world, stack);
	}

	@Override
	protected Item getDefaultItem() {
		return Items.ENDER_PEARL;
	}

	@Override
	protected void setOwner(UUID uuid) {
		this.removeFromOwner();
		super.setOwner(uuid);
		this.addToOwner();
	}

	@Override
	public void setOwner(@Nullable Entity entity) {
		this.removeFromOwner();
		super.setOwner(entity);
		this.addToOwner();
	}

	private void removeFromOwner() {
		if (this.getOwner() instanceof ServerPlayerEntity serverPlayerEntity) {
			serverPlayerEntity.removeEnderPearl(this);
		}
	}

	private void addToOwner() {
		if (this.getOwner() instanceof ServerPlayerEntity serverPlayerEntity) {
			serverPlayerEntity.addEnderPearl(this);
		}
	}

	@Nullable
	@Override
	protected Entity getEntity(UUID uuid) {
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			Entity entity = super.getEntity(uuid);
			if (entity != null) {
				return entity;
			} else {
				for (ServerWorld serverWorld2 : serverWorld.getServer().getWorlds()) {
					if (serverWorld2 != serverWorld) {
						entity = serverWorld2.getEntity(uuid);
						if (entity != null) {
							return entity;
						}
					}
				}

				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		entityHitResult.getEntity().serverDamage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);

		for (int i = 0; i < 32; i++) {
			this.getWorld()
				.addParticle(
					ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian()
				);
		}

		if (this.getWorld() instanceof ServerWorld serverWorld && !this.isRemoved()) {
			Entity entity = this.getOwner();
			if (entity != null && canTeleportEntityTo(entity, serverWorld)) {
				if (entity.hasVehicle()) {
					entity.detach();
				}

				Vec3d vec3d = this.getLastRenderPos();
				if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
					if (serverPlayerEntity.networkHandler.isConnectionOpen()) {
						if (this.random.nextFloat() < 0.05F && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
							EndermiteEntity endermiteEntity = EntityType.ENDERMITE.create(serverWorld, SpawnReason.TRIGGERED);
							if (endermiteEntity != null) {
								endermiteEntity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
								serverWorld.spawnEntity(endermiteEntity);
							}
						}

						if (this.hasPortalCooldown()) {
							entity.resetPortalCooldown();
						}

						ServerPlayerEntity serverPlayerEntity2 = serverPlayerEntity.teleportTo(
							new TeleportTarget(serverWorld, vec3d, Vec3d.ZERO, 0.0F, 0.0F, PositionFlag.combine(PositionFlag.ROT, PositionFlag.DELTA), TeleportTarget.NO_OP)
						);
						if (serverPlayerEntity2 != null) {
							serverPlayerEntity2.onLanding();
							serverPlayerEntity2.clearCurrentExplosion();
							serverPlayerEntity2.damage(serverPlayerEntity.getServerWorld(), this.getDamageSources().enderPearl(), 5.0F);
						}

						this.playTeleportSound(serverWorld, vec3d);
					}
				} else {
					Entity entity2 = entity.teleportTo(new TeleportTarget(serverWorld, vec3d, entity.getVelocity(), entity.getYaw(), entity.getPitch(), TeleportTarget.NO_OP));
					if (entity2 != null) {
						entity2.onLanding();
					}

					this.playTeleportSound(serverWorld, vec3d);
				}

				this.discard();
				return;
			}

			this.discard();
			return;
		}
	}

	private static boolean canTeleportEntityTo(Entity entity, World world) {
		if (entity.getWorld().getRegistryKey() == world.getRegistryKey()) {
			return !(entity instanceof LivingEntity livingEntity) ? entity.isAlive() : livingEntity.isAlive() && !livingEntity.isSleeping();
		} else {
			return entity.canUsePortals(true);
		}
	}

	@Override
	public void tick() {
		int i;
		int j;
		Entity entity;
		label30: {
			i = ChunkSectionPos.getSectionCoordFloored(this.getPos().getX());
			j = ChunkSectionPos.getSectionCoordFloored(this.getPos().getZ());
			entity = this.getOwner();
			if (entity instanceof ServerPlayerEntity serverPlayerEntity
				&& !entity.isAlive()
				&& serverPlayerEntity.getServerWorld().getGameRules().getBoolean(GameRules.ENDER_PEARLS_VANISH_ON_DEATH)) {
				this.discard();
				break label30;
			}

			super.tick();
		}

		if (this.isAlive()) {
			BlockPos blockPos = BlockPos.ofFloored(this.getPos());
			if ((--this.chunkTicketExpiryTicks <= 0L || i != ChunkSectionPos.getSectionCoord(blockPos.getX()) || j != ChunkSectionPos.getSectionCoord(blockPos.getZ()))
				&& entity instanceof ServerPlayerEntity serverPlayerEntity2) {
				this.chunkTicketExpiryTicks = serverPlayerEntity2.handleThrownEnderPearl(this);
			}
		}
	}

	private void playTeleportSound(World world, Vec3d pos) {
		world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);
	}

	@Nullable
	@Override
	public Entity teleportTo(TeleportTarget teleportTarget) {
		Entity entity = super.teleportTo(teleportTarget);
		if (entity != null) {
			entity.addPortalChunkTicketAt(BlockPos.ofFloored(entity.getPos()));
		}

		return entity;
	}

	@Override
	public boolean canTeleportBetween(World from, World to) {
		return from.getRegistryKey() == World.END && to.getRegistryKey() == World.OVERWORLD && this.getOwner() instanceof ServerPlayerEntity serverPlayerEntity
			? super.canTeleportBetween(from, to) && serverPlayerEntity.seenCredits
			: super.canTeleportBetween(from, to);
	}

	@Override
	protected void onBlockCollision(BlockState state) {
		super.onBlockCollision(state);
		if (state.isOf(Blocks.END_GATEWAY) && this.getOwner() instanceof ServerPlayerEntity serverPlayerEntity) {
			serverPlayerEntity.onBlockCollision(state);
		}
	}

	@Override
	public void onRemove(Entity.RemovalReason reason) {
		if (reason != Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
			this.removeFromOwner();
		}

		super.onRemove(reason);
	}
}
