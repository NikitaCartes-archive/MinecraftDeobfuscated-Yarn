package net.minecraft.server.network;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.network.packet.EntityAttributesClientPacket;
import net.minecraft.client.network.packet.EntityClientPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateClientPacket;
import net.minecraft.client.network.packet.EntityPassengersSetClientPacket;
import net.minecraft.client.network.packet.EntityPositionClientPacket;
import net.minecraft.client.network.packet.EntityPotionEffectClientPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawClientPacket;
import net.minecraft.client.network.packet.EntitySpawnClientPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateClientPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateClientPacket;
import net.minecraft.client.network.packet.ExperienceOrbSpawnClientPacket;
import net.minecraft.client.network.packet.MobSpawnClientPacket;
import net.minecraft.client.network.packet.PaintingSpawnClientPacket;
import net.minecraft.client.network.packet.PlayerSpawnClientPacket;
import net.minecraft.client.network.packet.PlayerUseBedClientPacket;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PrimedTNTEntity;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.sortme.Living;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Entity entity;
	private final int trackingRange;
	private int field_14052;
	private final int tickInterval;
	private long lastX;
	private long lastY;
	private long lastZ;
	private int lastYaw;
	private int lastPitch;
	private int lastHeadPitch;
	private double lastVelocityX;
	private double lastVelocityY;
	private double lastVelocityZ;
	public int field_14040;
	private double field_14056;
	private double field_14042;
	private double field_14055;
	private boolean field_14054;
	private final boolean alwaysUpdateVelocity;
	private int field_14043;
	private List<Entity> lastPassengers = Collections.emptyList();
	private boolean field_14051;
	private boolean lastOnGround;
	public boolean field_14058;
	private final Set<ServerPlayerEntity> trackingPlayers = Sets.<ServerPlayerEntity>newHashSet();

	public EntityTrackerEntry(Entity entity, int i, int j, int k, boolean bl) {
		this.entity = entity;
		this.trackingRange = i;
		this.field_14052 = j;
		this.tickInterval = k;
		this.alwaysUpdateVelocity = bl;
		this.lastX = EntityTracker.toFixedPoint(entity.x);
		this.lastY = EntityTracker.toFixedPoint(entity.y);
		this.lastZ = EntityTracker.toFixedPoint(entity.z);
		this.lastYaw = MathHelper.floor(entity.yaw * 256.0F / 360.0F);
		this.lastPitch = MathHelper.floor(entity.pitch * 256.0F / 360.0F);
		this.lastHeadPitch = MathHelper.floor(entity.getHeadPitch() * 256.0F / 360.0F);
		this.lastOnGround = entity.onGround;
	}

	public boolean equals(Object object) {
		return object instanceof EntityTrackerEntry ? ((EntityTrackerEntry)object).entity.getEntityId() == this.entity.getEntityId() : false;
	}

	public int hashCode() {
		return this.entity.getEntityId();
	}

	public void method_14297(List<PlayerEntity> list) {
		this.field_14058 = false;
		if (!this.field_14054 || this.entity.squaredDistanceTo(this.field_14056, this.field_14042, this.field_14055) > 16.0) {
			this.field_14056 = this.entity.x;
			this.field_14042 = this.entity.y;
			this.field_14055 = this.entity.z;
			this.field_14054 = true;
			this.field_14058 = true;
			this.method_14300(list);
		}

		List<Entity> list2 = this.entity.getPassengerList();
		if (!list2.equals(this.lastPassengers)) {
			this.lastPassengers = list2;
			this.sendToTrackingPlayers(new EntityPassengersSetClientPacket(this.entity));
		}

		if (this.entity instanceof ItemFrameEntity && this.field_14040 % 10 == 0) {
			ItemFrameEntity itemFrameEntity = (ItemFrameEntity)this.entity;
			ItemStack itemStack = itemFrameEntity.getHeldItemStack();
			if (itemStack.getItem() instanceof FilledMapItem) {
				MapState mapState = FilledMapItem.method_8001(itemStack, this.entity.world);

				for (PlayerEntity playerEntity : list) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
					mapState.method_102(serverPlayerEntity, itemStack);
					Packet<?> packet = ((FilledMapItem)itemStack.getItem()).createMapPacket(itemStack, this.entity.world, serverPlayerEntity);
					if (packet != null) {
						serverPlayerEntity.networkHandler.sendPacket(packet);
					}
				}
			}

			this.method_14306();
		}

		if (this.field_14040 % this.tickInterval == 0 || this.entity.velocityDirty || this.entity.getDataTracker().isDirty()) {
			if (this.entity.hasVehicle()) {
				int i = MathHelper.floor(this.entity.yaw * 256.0F / 360.0F);
				int j = MathHelper.floor(this.entity.pitch * 256.0F / 360.0F);
				boolean bl = Math.abs(i - this.lastYaw) >= 1 || Math.abs(j - this.lastPitch) >= 1;
				if (bl) {
					this.sendToTrackingPlayers(new EntityClientPacket.Rotate(this.entity.getEntityId(), (byte)i, (byte)j, this.entity.onGround));
					this.lastYaw = i;
					this.lastPitch = j;
				}

				this.lastX = EntityTracker.toFixedPoint(this.entity.x);
				this.lastY = EntityTracker.toFixedPoint(this.entity.y);
				this.lastZ = EntityTracker.toFixedPoint(this.entity.z);
				this.method_14306();
				this.field_14051 = true;
			} else {
				this.field_14043++;
				long l = EntityTracker.toFixedPoint(this.entity.x);
				long m = EntityTracker.toFixedPoint(this.entity.y);
				long n = EntityTracker.toFixedPoint(this.entity.z);
				int k = MathHelper.floor(this.entity.yaw * 256.0F / 360.0F);
				int o = MathHelper.floor(this.entity.pitch * 256.0F / 360.0F);
				long p = l - this.lastX;
				long q = m - this.lastY;
				long r = n - this.lastZ;
				Packet<?> packet2 = null;
				boolean bl2 = p * p + q * q + r * r >= 128L || this.field_14040 % 60 == 0;
				boolean bl3 = Math.abs(k - this.lastYaw) >= 1 || Math.abs(o - this.lastPitch) >= 1;
				if (this.field_14040 > 0 || this.entity instanceof ProjectileEntity) {
					if (p >= -32768L
						&& p < 32768L
						&& q >= -32768L
						&& q < 32768L
						&& r >= -32768L
						&& r < 32768L
						&& this.field_14043 <= 400
						&& !this.field_14051
						&& this.lastOnGround == this.entity.onGround) {
						if ((!bl2 || !bl3) && !(this.entity instanceof ProjectileEntity)) {
							if (bl2) {
								packet2 = new EntityClientPacket.MoveRelative(this.entity.getEntityId(), p, q, r, this.entity.onGround);
							} else if (bl3) {
								packet2 = new EntityClientPacket.Rotate(this.entity.getEntityId(), (byte)k, (byte)o, this.entity.onGround);
							}
						} else {
							packet2 = new EntityClientPacket.RotateAndMoveRelative(this.entity.getEntityId(), p, q, r, (byte)k, (byte)o, this.entity.onGround);
						}
					} else {
						this.lastOnGround = this.entity.onGround;
						this.field_14043 = 0;
						this.method_14307();
						packet2 = new EntityPositionClientPacket(this.entity);
					}
				}

				boolean bl4 = this.alwaysUpdateVelocity || this.entity.velocityDirty;
				if (this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) {
					bl4 = true;
				}

				if (bl4 && this.field_14040 > 0) {
					double d = this.entity.velocityX - this.lastVelocityX;
					double e = this.entity.velocityY - this.lastVelocityY;
					double f = this.entity.velocityZ - this.lastVelocityZ;
					double g = 0.02;
					double h = d * d + e * e + f * f;
					if (h > 4.0E-4 || h > 0.0 && this.entity.velocityX == 0.0 && this.entity.velocityY == 0.0 && this.entity.velocityZ == 0.0) {
						this.lastVelocityX = this.entity.velocityX;
						this.lastVelocityY = this.entity.velocityY;
						this.lastVelocityZ = this.entity.velocityZ;
						this.sendToTrackingPlayers(new EntityVelocityUpdateClientPacket(this.entity.getEntityId(), this.lastVelocityX, this.lastVelocityY, this.lastVelocityZ));
					}
				}

				if (packet2 != null) {
					this.sendToTrackingPlayers(packet2);
				}

				this.method_14306();
				if (bl2) {
					this.lastX = l;
					this.lastY = m;
					this.lastZ = n;
				}

				if (bl3) {
					this.lastYaw = k;
					this.lastPitch = o;
				}

				this.field_14051 = false;
			}

			int i = MathHelper.floor(this.entity.getHeadPitch() * 256.0F / 360.0F);
			if (Math.abs(i - this.lastHeadPitch) >= 1) {
				this.sendToTrackingPlayers(new EntitySetHeadYawClientPacket(this.entity, (byte)i));
				this.lastHeadPitch = i;
			}

			this.entity.velocityDirty = false;
		}

		this.field_14040++;
		if (this.entity.field_6037) {
			this.sendToTrackingPlayersAndSelf(new EntityVelocityUpdateClientPacket(this.entity));
			this.entity.field_6037 = false;
		}
	}

	private void method_14306() {
		DataTracker dataTracker = this.entity.getDataTracker();
		if (dataTracker.isDirty()) {
			this.sendToTrackingPlayersAndSelf(new EntityTrackerUpdateClientPacket(this.entity.getEntityId(), dataTracker, false));
		}

		if (this.entity instanceof LivingEntity) {
			EntityAttributeContainer entityAttributeContainer = (EntityAttributeContainer)((LivingEntity)this.entity).getAttributeContainer();
			Set<EntityAttributeInstance> set = entityAttributeContainer.method_6215();
			if (!set.isEmpty()) {
				this.sendToTrackingPlayersAndSelf(new EntityAttributesClientPacket(this.entity.getEntityId(), set));
			}

			set.clear();
		}
	}

	public void sendToTrackingPlayers(Packet<?> packet) {
		for (ServerPlayerEntity serverPlayerEntity : this.trackingPlayers) {
			serverPlayerEntity.networkHandler.sendPacket(packet);
		}
	}

	public void sendToTrackingPlayersAndSelf(Packet<?> packet) {
		this.sendToTrackingPlayers(packet);
		if (this.entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
		}
	}

	public void method_14304() {
		for (ServerPlayerEntity serverPlayerEntity : this.trackingPlayers) {
			this.entity.onStoppedTrackingBy(serverPlayerEntity);
			serverPlayerEntity.method_14249(this.entity);
		}
	}

	public void method_14302(ServerPlayerEntity serverPlayerEntity) {
		if (this.trackingPlayers.contains(serverPlayerEntity)) {
			this.entity.onStoppedTrackingBy(serverPlayerEntity);
			serverPlayerEntity.method_14249(this.entity);
			this.trackingPlayers.remove(serverPlayerEntity);
		}
	}

	public void method_14303(ServerPlayerEntity serverPlayerEntity) {
		if (serverPlayerEntity != this.entity) {
			if (this.method_14294(serverPlayerEntity)) {
				if (!this.trackingPlayers.contains(serverPlayerEntity) && (this.method_14298(serverPlayerEntity) || this.entity.field_5983)) {
					this.trackingPlayers.add(serverPlayerEntity);
					Packet<?> packet = this.createSpawnPacket();
					serverPlayerEntity.networkHandler.sendPacket(packet);
					if (!this.entity.getDataTracker().method_12790()) {
						serverPlayerEntity.networkHandler.sendPacket(new EntityTrackerUpdateClientPacket(this.entity.getEntityId(), this.entity.getDataTracker(), true));
					}

					boolean bl = this.alwaysUpdateVelocity;
					if (this.entity instanceof LivingEntity) {
						EntityAttributeContainer entityAttributeContainer = (EntityAttributeContainer)((LivingEntity)this.entity).getAttributeContainer();
						Collection<EntityAttributeInstance> collection = entityAttributeContainer.method_6213();
						if (!collection.isEmpty()) {
							serverPlayerEntity.networkHandler.sendPacket(new EntityAttributesClientPacket(this.entity.getEntityId(), collection));
						}

						if (((LivingEntity)this.entity).isFallFlying()) {
							bl = true;
						}
					}

					this.lastVelocityX = this.entity.velocityX;
					this.lastVelocityY = this.entity.velocityY;
					this.lastVelocityZ = this.entity.velocityZ;
					if (bl && !(packet instanceof MobSpawnClientPacket)) {
						serverPlayerEntity.networkHandler
							.sendPacket(new EntityVelocityUpdateClientPacket(this.entity.getEntityId(), this.entity.velocityX, this.entity.velocityY, this.entity.velocityZ));
					}

					if (this.entity instanceof LivingEntity) {
						for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
							ItemStack itemStack = ((LivingEntity)this.entity).getEquippedStack(equipmentSlot);
							if (!itemStack.isEmpty()) {
								serverPlayerEntity.networkHandler.sendPacket(new EntityEquipmentUpdateClientPacket(this.entity.getEntityId(), equipmentSlot, itemStack));
							}
						}
					}

					if (this.entity instanceof PlayerEntity) {
						PlayerEntity playerEntity = (PlayerEntity)this.entity;
						if (playerEntity.isSleeping()) {
							serverPlayerEntity.networkHandler.sendPacket(new PlayerUseBedClientPacket(playerEntity, new BlockPos(this.entity)));
						}
					}

					if (this.entity instanceof LivingEntity) {
						LivingEntity livingEntity = (LivingEntity)this.entity;

						for (StatusEffectInstance statusEffectInstance : livingEntity.getPotionEffects()) {
							serverPlayerEntity.networkHandler.sendPacket(new EntityPotionEffectClientPacket(this.entity.getEntityId(), statusEffectInstance));
						}
					}

					if (!this.entity.getPassengerList().isEmpty()) {
						serverPlayerEntity.networkHandler.sendPacket(new EntityPassengersSetClientPacket(this.entity));
					}

					if (this.entity.hasVehicle()) {
						serverPlayerEntity.networkHandler.sendPacket(new EntityPassengersSetClientPacket(this.entity.getRiddenEntity()));
					}

					this.entity.onStartedTrackingBy(serverPlayerEntity);
					serverPlayerEntity.onStartedTracking(this.entity);
				}
			} else if (this.trackingPlayers.contains(serverPlayerEntity)) {
				this.trackingPlayers.remove(serverPlayerEntity);
				this.entity.onStoppedTrackingBy(serverPlayerEntity);
				serverPlayerEntity.method_14249(this.entity);
			}
		}
	}

	public boolean method_14294(ServerPlayerEntity serverPlayerEntity) {
		double d = serverPlayerEntity.x - (double)this.lastX / 4096.0;
		double e = serverPlayerEntity.z - (double)this.lastZ / 4096.0;
		int i = Math.min(this.trackingRange, this.field_14052);
		return d >= (double)(-i) && d <= (double)i && e >= (double)(-i) && e <= (double)i && this.entity.method_5680(serverPlayerEntity);
	}

	private boolean method_14298(ServerPlayerEntity serverPlayerEntity) {
		return serverPlayerEntity.getServerWorld().getChunkManager().method_14154(serverPlayerEntity, this.entity.chunkX, this.entity.chunkZ);
	}

	public void method_14300(List<PlayerEntity> list) {
		for (int i = 0; i < list.size(); i++) {
			this.method_14303((ServerPlayerEntity)list.get(i));
		}
	}

	private Packet<?> createSpawnPacket() {
		if (this.entity.invalid) {
			LOGGER.warn("Fetching addPacket for removed entity");
		}

		if (this.entity instanceof ServerPlayerEntity) {
			return new PlayerSpawnClientPacket((PlayerEntity)this.entity);
		} else if (this.entity instanceof Living) {
			this.lastHeadPitch = MathHelper.floor(this.entity.getHeadPitch() * 256.0F / 360.0F);
			return new MobSpawnClientPacket((LivingEntity)this.entity);
		} else if (this.entity instanceof PaintingEntity) {
			return new PaintingSpawnClientPacket((PaintingEntity)this.entity);
		} else if (this.entity instanceof ItemEntity) {
			return new EntitySpawnClientPacket(this.entity, 2, 1);
		} else if (this.entity instanceof AbstractMinecartEntity) {
			AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)this.entity;
			return new EntitySpawnClientPacket(this.entity, 10, abstractMinecartEntity.getMinecartType().getId());
		} else if (this.entity instanceof BoatEntity) {
			return new EntitySpawnClientPacket(this.entity, 1);
		} else if (this.entity instanceof ExperienceOrbEntity) {
			return new ExperienceOrbSpawnClientPacket((ExperienceOrbEntity)this.entity);
		} else if (this.entity instanceof FishHookEntity) {
			Entity entity = ((FishHookEntity)this.entity).getOwner();
			return new EntitySpawnClientPacket(this.entity, 90, entity == null ? this.entity.getEntityId() : entity.getEntityId());
		} else if (this.entity instanceof SpectralArrowEntity) {
			Entity entity = ((SpectralArrowEntity)this.entity).getOwner();
			return new EntitySpawnClientPacket(this.entity, 91, 1 + (entity == null ? this.entity.getEntityId() : entity.getEntityId()));
		} else if (this.entity instanceof ArrowEntity) {
			Entity entity = ((ProjectileEntity)this.entity).getOwner();
			return new EntitySpawnClientPacket(this.entity, 60, 1 + (entity == null ? this.entity.getEntityId() : entity.getEntityId()));
		} else if (this.entity instanceof SnowballEntity) {
			return new EntitySpawnClientPacket(this.entity, 61);
		} else if (this.entity instanceof TridentEntity) {
			Entity entity = ((ProjectileEntity)this.entity).getOwner();
			return new EntitySpawnClientPacket(this.entity, 94, 1 + (entity == null ? this.entity.getEntityId() : entity.getEntityId()));
		} else if (this.entity instanceof LlamaSpitEntity) {
			return new EntitySpawnClientPacket(this.entity, 68);
		} else if (this.entity instanceof ThrownPotionEntity) {
			return new EntitySpawnClientPacket(this.entity, 73);
		} else if (this.entity instanceof ThrownExperienceBottleEntity) {
			return new EntitySpawnClientPacket(this.entity, 75);
		} else if (this.entity instanceof ThrownEnderpearlEntity) {
			return new EntitySpawnClientPacket(this.entity, 65);
		} else if (this.entity instanceof EnderEyeEntity) {
			return new EntitySpawnClientPacket(this.entity, 72);
		} else if (this.entity instanceof FireworkEntity) {
			return new EntitySpawnClientPacket(this.entity, 76);
		} else if (this.entity instanceof ExplosiveProjectileEntity) {
			ExplosiveProjectileEntity explosiveProjectileEntity = (ExplosiveProjectileEntity)this.entity;
			int i = 63;
			if (this.entity instanceof SmallFireballEntity) {
				i = 64;
			} else if (this.entity instanceof DragonFireballEntity) {
				i = 93;
			} else if (this.entity instanceof ExplodingWitherSkullEntity) {
				i = 66;
			}

			EntitySpawnClientPacket entitySpawnClientPacket;
			if (explosiveProjectileEntity.owner == null) {
				entitySpawnClientPacket = new EntitySpawnClientPacket(this.entity, i, 0);
			} else {
				entitySpawnClientPacket = new EntitySpawnClientPacket(this.entity, i, ((ExplosiveProjectileEntity)this.entity).owner.getEntityId());
			}

			entitySpawnClientPacket.setVelocityX((int)(explosiveProjectileEntity.field_7601 * 8000.0));
			entitySpawnClientPacket.setVelocityY((int)(explosiveProjectileEntity.field_7600 * 8000.0));
			entitySpawnClientPacket.setVelocityZ((int)(explosiveProjectileEntity.field_7599 * 8000.0));
			return entitySpawnClientPacket;
		} else if (this.entity instanceof ShulkerBulletEntity) {
			EntitySpawnClientPacket entitySpawnClientPacket2 = new EntitySpawnClientPacket(this.entity, 67, 0);
			entitySpawnClientPacket2.setVelocityX((int)(this.entity.velocityX * 8000.0));
			entitySpawnClientPacket2.setVelocityY((int)(this.entity.velocityY * 8000.0));
			entitySpawnClientPacket2.setVelocityZ((int)(this.entity.velocityZ * 8000.0));
			return entitySpawnClientPacket2;
		} else if (this.entity instanceof ThrownEggEntity) {
			return new EntitySpawnClientPacket(this.entity, 62);
		} else if (this.entity instanceof EvokerFangsEntity) {
			return new EntitySpawnClientPacket(this.entity, 79);
		} else if (this.entity instanceof PrimedTNTEntity) {
			return new EntitySpawnClientPacket(this.entity, 50);
		} else if (this.entity instanceof EnderCrystalEntity) {
			return new EntitySpawnClientPacket(this.entity, 51);
		} else if (this.entity instanceof FallingBlockEntity) {
			FallingBlockEntity fallingBlockEntity = (FallingBlockEntity)this.entity;
			return new EntitySpawnClientPacket(this.entity, 70, Block.getRawIdFromState(fallingBlockEntity.getBlockState()));
		} else if (this.entity instanceof ArmorStandEntity) {
			return new EntitySpawnClientPacket(this.entity, 78);
		} else if (this.entity instanceof ItemFrameEntity) {
			ItemFrameEntity itemFrameEntity = (ItemFrameEntity)this.entity;
			return new EntitySpawnClientPacket(this.entity, 71, itemFrameEntity.field_7099.getId(), itemFrameEntity.getDecorationBlockPos());
		} else if (this.entity instanceof LeadKnotEntity) {
			LeadKnotEntity leadKnotEntity = (LeadKnotEntity)this.entity;
			return new EntitySpawnClientPacket(this.entity, 77, 0, leadKnotEntity.getDecorationBlockPos());
		} else if (this.entity instanceof AreaEffectCloudEntity) {
			return new EntitySpawnClientPacket(this.entity, 3);
		} else {
			throw new IllegalArgumentException("Don't know how to add " + this.entity.getClass() + "!");
		}
	}

	public void method_14301(ServerPlayerEntity serverPlayerEntity) {
		if (this.trackingPlayers.contains(serverPlayerEntity)) {
			this.trackingPlayers.remove(serverPlayerEntity);
			this.entity.onStoppedTrackingBy(serverPlayerEntity);
			serverPlayerEntity.method_14249(this.entity);
		}
	}

	public Entity getEntity() {
		return this.entity;
	}

	public void method_14296(int i) {
		this.field_14052 = i;
	}

	public void method_14307() {
		this.field_14054 = false;
	}
}
