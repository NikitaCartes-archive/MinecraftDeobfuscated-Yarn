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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class EnderPearlEntity extends ThrownItemEntity {
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
		entityHitResult.getEntity().damage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
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

				if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
					if (serverPlayerEntity.networkHandler.isConnectionOpen()) {
						if (this.random.nextFloat() < 0.05F && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
							EndermiteEntity endermiteEntity = EntityType.ENDERMITE.create(serverWorld, SpawnReason.TRIGGERED);
							if (endermiteEntity != null) {
								endermiteEntity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
								serverWorld.spawnEntity(endermiteEntity);
							}
						}

						PlayerEntity playerEntity = serverPlayerEntity.teleportTo(
							new TeleportTarget(serverWorld, this.getPos(), entity.getVelocity(), entity.getYaw(), entity.getPitch(), TeleportTarget.NO_OP)
						);
						if (playerEntity != null) {
							playerEntity.onLanding();
							playerEntity.clearCurrentExplosion();
							playerEntity.damage(this.getDamageSources().enderPearl(), 5.0F);
						}

						this.playTeleportSound(serverWorld, this.getPos());
					}
				} else {
					Entity entity2 = entity.teleportTo(
						new TeleportTarget(serverWorld, this.getPos(), entity.getVelocity(), entity.getYaw(), entity.getPitch(), TeleportTarget.NO_OP)
					);
					if (entity2 != null) {
						entity2.onLanding();
					}

					this.playTeleportSound(serverWorld, this.getPos());
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
		Entity entity = this.getOwner();
		if (entity instanceof ServerPlayerEntity && !entity.isAlive() && this.getWorld().getGameRules().getBoolean(GameRules.ENDER_PEARLS_VANISH_ON_DEATH)) {
			this.discard();
		} else {
			super.tick();
		}
	}

	private void playTeleportSound(World world, Vec3d pos) {
		world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);
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
}
