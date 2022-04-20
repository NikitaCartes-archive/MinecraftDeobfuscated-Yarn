package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;

public class EntityTrackerEntry {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_29767 = 1;
	private final ServerWorld world;
	private final Entity entity;
	private final int tickInterval;
	private final boolean alwaysUpdateVelocity;
	private final Consumer<Packet<?>> receiver;
	private int lastYaw;
	private int lastPitch;
	private int lastHeadPitch;
	private Vec3d velocity = Vec3d.ZERO;
	private int trackingTick;
	private int updatesWithoutVehicle;
	private List<Entity> lastPassengers = Collections.emptyList();
	private boolean hadVehicle;
	private boolean lastOnGround;

	public EntityTrackerEntry(ServerWorld world, Entity entity, int tickInterval, boolean alwaysUpdateVelocity, Consumer<Packet<?>> receiver) {
		this.world = world;
		this.receiver = receiver;
		this.entity = entity;
		this.tickInterval = tickInterval;
		this.alwaysUpdateVelocity = alwaysUpdateVelocity;
		entity.getTrackedPosition().setPos(entity.getSyncedPos());
		this.lastYaw = MathHelper.floor(entity.getYaw() * 256.0F / 360.0F);
		this.lastPitch = MathHelper.floor(entity.getPitch() * 256.0F / 360.0F);
		this.lastHeadPitch = MathHelper.floor(entity.getHeadYaw() * 256.0F / 360.0F);
		this.lastOnGround = entity.isOnGround();
	}

	public void tick() {
		List<Entity> list = this.entity.getPassengerList();
		if (!list.equals(this.lastPassengers)) {
			this.lastPassengers = list;
			this.receiver.accept(new EntityPassengersSetS2CPacket(this.entity));
		}

		if (this.entity instanceof ItemFrameEntity itemFrameEntity && this.trackingTick % 10 == 0) {
			ItemStack itemStack = itemFrameEntity.getHeldItemStack();
			if (itemStack.getItem() instanceof FilledMapItem) {
				Integer integer = FilledMapItem.getMapId(itemStack);
				MapState mapState = FilledMapItem.getMapState(integer, this.world);
				if (mapState != null) {
					for (ServerPlayerEntity serverPlayerEntity : this.world.getPlayers()) {
						mapState.update(serverPlayerEntity, itemStack);
						Packet<?> packet = mapState.getPlayerMarkerPacket(integer, serverPlayerEntity);
						if (packet != null) {
							serverPlayerEntity.networkHandler.sendPacket(packet);
						}
					}
				}
			}

			this.syncEntityData();
		}

		if (this.trackingTick % this.tickInterval == 0 || this.entity.velocityDirty || this.entity.getDataTracker().isDirty()) {
			TrackedPosition trackedPosition = this.entity.getTrackedPosition();
			if (this.entity.hasVehicle()) {
				int i = MathHelper.floor(this.entity.getYaw() * 256.0F / 360.0F);
				int j = MathHelper.floor(this.entity.getPitch() * 256.0F / 360.0F);
				boolean bl = Math.abs(i - this.lastYaw) >= 1 || Math.abs(j - this.lastPitch) >= 1;
				if (bl) {
					this.receiver.accept(new EntityS2CPacket.Rotate(this.entity.getId(), (byte)i, (byte)j, this.entity.isOnGround()));
					this.lastYaw = i;
					this.lastPitch = j;
				}

				trackedPosition.setPos(this.entity.getSyncedPos());
				this.syncEntityData();
				this.hadVehicle = true;
			} else {
				this.updatesWithoutVehicle++;
				int i = MathHelper.floor(this.entity.getYaw() * 256.0F / 360.0F);
				int j = MathHelper.floor(this.entity.getPitch() * 256.0F / 360.0F);
				Vec3d vec3d = this.entity.getSyncedPos();
				boolean bl2 = trackedPosition.subtract(vec3d).lengthSquared() >= 7.6293945E-6F;
				Packet<?> packet2 = null;
				boolean bl3 = bl2 || this.trackingTick % 60 == 0;
				boolean bl4 = Math.abs(i - this.lastYaw) >= 1 || Math.abs(j - this.lastPitch) >= 1;
				if (this.trackingTick > 0 || this.entity instanceof PersistentProjectileEntity) {
					long l = trackedPosition.getDeltaX(vec3d);
					long m = trackedPosition.getDeltaY(vec3d);
					long n = trackedPosition.getDeltaZ(vec3d);
					boolean bl5 = l < -32768L || l > 32767L || m < -32768L || m > 32767L || n < -32768L || n > 32767L;
					if (bl5 || this.updatesWithoutVehicle > 400 || this.hadVehicle || this.lastOnGround != this.entity.isOnGround()) {
						this.lastOnGround = this.entity.isOnGround();
						this.updatesWithoutVehicle = 0;
						packet2 = new EntityPositionS2CPacket(this.entity);
					} else if ((!bl3 || !bl4) && !(this.entity instanceof PersistentProjectileEntity)) {
						if (bl3) {
							packet2 = new EntityS2CPacket.MoveRelative(this.entity.getId(), (short)((int)l), (short)((int)m), (short)((int)n), this.entity.isOnGround());
						} else if (bl4) {
							packet2 = new EntityS2CPacket.Rotate(this.entity.getId(), (byte)i, (byte)j, this.entity.isOnGround());
						}
					} else {
						packet2 = new EntityS2CPacket.RotateAndMoveRelative(
							this.entity.getId(), (short)((int)l), (short)((int)m), (short)((int)n), (byte)i, (byte)j, this.entity.isOnGround()
						);
					}
				}

				if ((this.alwaysUpdateVelocity || this.entity.velocityDirty || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying())
					&& this.trackingTick > 0) {
					Vec3d vec3d2 = this.entity.getVelocity();
					double d = vec3d2.squaredDistanceTo(this.velocity);
					if (d > 1.0E-7 || d > 0.0 && vec3d2.lengthSquared() == 0.0) {
						this.velocity = vec3d2;
						this.receiver.accept(new EntityVelocityUpdateS2CPacket(this.entity.getId(), this.velocity));
					}
				}

				if (packet2 != null) {
					this.receiver.accept(packet2);
				}

				this.syncEntityData();
				if (bl3) {
					trackedPosition.setPos(vec3d);
				}

				if (bl4) {
					this.lastYaw = i;
					this.lastPitch = j;
				}

				this.hadVehicle = false;
			}

			int ix = MathHelper.floor(this.entity.getHeadYaw() * 256.0F / 360.0F);
			if (Math.abs(ix - this.lastHeadPitch) >= 1) {
				this.receiver.accept(new EntitySetHeadYawS2CPacket(this.entity, (byte)ix));
				this.lastHeadPitch = ix;
			}

			this.entity.velocityDirty = false;
		}

		this.trackingTick++;
		if (this.entity.velocityModified) {
			this.sendSyncPacket(new EntityVelocityUpdateS2CPacket(this.entity));
			this.entity.velocityModified = false;
		}
	}

	public void stopTracking(ServerPlayerEntity player) {
		this.entity.onStoppedTrackingBy(player);
		player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(this.entity.getId()));
	}

	public void startTracking(ServerPlayerEntity player) {
		this.sendPackets(player.networkHandler::sendPacket);
		this.entity.onStartedTrackingBy(player);
	}

	public void sendPackets(Consumer<Packet<?>> sender) {
		if (this.entity.isRemoved()) {
			LOGGER.warn("Fetching packet for removed entity {}", this.entity);
		}

		Packet<?> packet = this.entity.createSpawnPacket();
		this.lastHeadPitch = MathHelper.floor(this.entity.getHeadYaw() * 256.0F / 360.0F);
		sender.accept(packet);
		if (!this.entity.getDataTracker().isEmpty()) {
			sender.accept(new EntityTrackerUpdateS2CPacket(this.entity.getId(), this.entity.getDataTracker(), true));
		}

		boolean bl = this.alwaysUpdateVelocity;
		if (this.entity instanceof LivingEntity) {
			Collection<EntityAttributeInstance> collection = ((LivingEntity)this.entity).getAttributes().getAttributesToSend();
			if (!collection.isEmpty()) {
				sender.accept(new EntityAttributesS2CPacket(this.entity.getId(), collection));
			}

			if (((LivingEntity)this.entity).isFallFlying()) {
				bl = true;
			}
		}

		this.velocity = this.entity.getVelocity();
		if (bl && !(this.entity instanceof LivingEntity)) {
			sender.accept(new EntityVelocityUpdateS2CPacket(this.entity.getId(), this.velocity));
		}

		if (this.entity instanceof LivingEntity) {
			List<Pair<EquipmentSlot, ItemStack>> list = Lists.<Pair<EquipmentSlot, ItemStack>>newArrayList();

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				ItemStack itemStack = ((LivingEntity)this.entity).getEquippedStack(equipmentSlot);
				if (!itemStack.isEmpty()) {
					list.add(Pair.of(equipmentSlot, itemStack.copy()));
				}
			}

			if (!list.isEmpty()) {
				sender.accept(new EntityEquipmentUpdateS2CPacket(this.entity.getId(), list));
			}
		}

		if (this.entity instanceof LivingEntity livingEntity) {
			for (StatusEffectInstance statusEffectInstance : livingEntity.getStatusEffects()) {
				sender.accept(new EntityStatusEffectS2CPacket(this.entity.getId(), statusEffectInstance));
			}
		}

		if (!this.entity.getPassengerList().isEmpty()) {
			sender.accept(new EntityPassengersSetS2CPacket(this.entity));
		}

		if (this.entity.hasVehicle()) {
			sender.accept(new EntityPassengersSetS2CPacket(this.entity.getVehicle()));
		}

		if (this.entity instanceof MobEntity mobEntity && mobEntity.isLeashed()) {
			sender.accept(new EntityAttachS2CPacket(mobEntity, mobEntity.getHoldingEntity()));
		}
	}

	/**
	 * Synchronizes tracked data and attributes
	 */
	private void syncEntityData() {
		DataTracker dataTracker = this.entity.getDataTracker();
		if (dataTracker.isDirty()) {
			this.sendSyncPacket(new EntityTrackerUpdateS2CPacket(this.entity.getId(), dataTracker, false));
		}

		if (this.entity instanceof LivingEntity) {
			Set<EntityAttributeInstance> set = ((LivingEntity)this.entity).getAttributes().getTracked();
			if (!set.isEmpty()) {
				this.sendSyncPacket(new EntityAttributesS2CPacket(this.entity.getId(), set));
			}

			set.clear();
		}
	}

	/**
	 * Sends a packet for synchronization with watcher and tracked player (if applicable)
	 */
	private void sendSyncPacket(Packet<?> packet) {
		this.receiver.accept(packet);
		if (this.entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
		}
	}
}
