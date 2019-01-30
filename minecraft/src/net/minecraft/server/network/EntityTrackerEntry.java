package net.minecraft.server.network;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.client.network.packet.EntityAttributesClientPacket;
import net.minecraft.client.network.packet.EntityClientPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateClientPacket;
import net.minecraft.client.network.packet.EntityPassengersSetClientPacket;
import net.minecraft.client.network.packet.EntityPositionClientPacket;
import net.minecraft.client.network.packet.EntityPotionEffectClientPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawClientPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateClientPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateClientPacket;
import net.minecraft.client.network.packet.MobSpawnClientPacket;
import net.minecraft.client.network.packet.PlayerUseBedClientPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
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
		this.lastX = EntityClientPacket.method_18047(entity.x);
		this.lastY = EntityClientPacket.method_18047(entity.y);
		this.lastZ = EntityClientPacket.method_18047(entity.z);
		this.lastYaw = MathHelper.floor(entity.yaw * 256.0F / 360.0F);
		this.lastPitch = MathHelper.floor(entity.pitch * 256.0F / 360.0F);
		this.lastHeadPitch = MathHelper.floor(entity.getHeadYaw() * 256.0F / 360.0F);
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

				this.lastX = EntityClientPacket.method_18047(this.entity.x);
				this.lastY = EntityClientPacket.method_18047(this.entity.y);
				this.lastZ = EntityClientPacket.method_18047(this.entity.z);
				this.method_14306();
				this.field_14051 = true;
			} else {
				this.field_14043++;
				long l = EntityClientPacket.method_18047(this.entity.x);
				long m = EntityClientPacket.method_18047(this.entity.y);
				long n = EntityClientPacket.method_18047(this.entity.z);
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

			int i = MathHelper.floor(this.entity.getHeadYaw() * 256.0F / 360.0F);
			if (Math.abs(i - this.lastHeadPitch) >= 1) {
				this.sendToTrackingPlayers(new EntitySetHeadYawClientPacket(this.entity, (byte)i));
				this.lastHeadPitch = i;
			}

			this.entity.velocityDirty = false;
		}

		this.field_14040++;
		if (this.entity.velocityModified) {
			this.sendToTrackingPlayersAndSelf(new EntityVelocityUpdateClientPacket(this.entity));
			this.entity.velocityModified = false;
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
				if (!this.trackingPlayers.contains(serverPlayerEntity) && (this.method_14298(serverPlayerEntity) || this.entity.teleporting)) {
					this.trackingPlayers.add(serverPlayerEntity);
					if (this.entity.invalid) {
						LOGGER.warn("Fetching addPacket for removed entity");
					}

					Packet<?> packet = this.entity.createSpawnPacket();
					this.lastHeadPitch = MathHelper.floor(this.entity.getHeadYaw() * 256.0F / 360.0F);
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
