package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ProjectilePowerS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;

public class EntityTrackerEntry {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_29767 = 1;
	private static final double field_44988 = 7.6293945E-6F;
	public static final int field_44987 = 60;
	private static final int field_44989 = 400;
	private final ServerWorld world;
	private final Entity entity;
	private final int tickInterval;
	private final boolean alwaysUpdateVelocity;
	private final Consumer<Packet<?>> receiver;
	private final TrackedPosition trackedPos = new TrackedPosition();
	private int lastYaw;
	private int lastPitch;
	private int lastHeadYaw;
	private Vec3d velocity = Vec3d.ZERO;
	private int trackingTick;
	private int updatesWithoutVehicle;
	private List<Entity> lastPassengers = Collections.emptyList();
	private boolean hadVehicle;
	private boolean lastOnGround;
	@Nullable
	private List<DataTracker.SerializedEntry<?>> changedEntries;

	public EntityTrackerEntry(ServerWorld world, Entity entity, int tickInterval, boolean alwaysUpdateVelocity, Consumer<Packet<?>> receiver) {
		this.world = world;
		this.receiver = receiver;
		this.entity = entity;
		this.tickInterval = tickInterval;
		this.alwaysUpdateVelocity = alwaysUpdateVelocity;
		this.trackedPos.setPos(entity.getSyncedPos());
		this.lastYaw = MathHelper.floor(entity.getYaw() * 256.0F / 360.0F);
		this.lastPitch = MathHelper.floor(entity.getPitch() * 256.0F / 360.0F);
		this.lastHeadYaw = MathHelper.floor(entity.getHeadYaw() * 256.0F / 360.0F);
		this.lastOnGround = entity.isOnGround();
		this.changedEntries = entity.getDataTracker().getChangedEntries();
	}

	public void tick() {
		List<Entity> list = this.entity.getPassengerList();
		if (!list.equals(this.lastPassengers)) {
			this.receiver.accept(new EntityPassengersSetS2CPacket(this.entity));
			streamChangedPassengers(list, this.lastPassengers)
				.forEach(
					passenger -> {
						if (passenger instanceof ServerPlayerEntity serverPlayerEntity) {
							serverPlayerEntity.networkHandler
								.requestTeleport(
									serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch()
								);
						}
					}
				);
			this.lastPassengers = list;
		}

		if (this.entity instanceof ItemFrameEntity itemFrameEntity && this.trackingTick % 10 == 0) {
			ItemStack itemStack = itemFrameEntity.getHeldItemStack();
			if (itemStack.getItem() instanceof FilledMapItem) {
				MapIdComponent mapIdComponent = itemStack.get(DataComponentTypes.MAP_ID);
				MapState mapState = FilledMapItem.getMapState(mapIdComponent, this.world);
				if (mapState != null) {
					for (ServerPlayerEntity serverPlayerEntity : this.world.getPlayers()) {
						mapState.update(serverPlayerEntity, itemStack);
						Packet<?> packet = mapState.getPlayerMarkerPacket(mapIdComponent, serverPlayerEntity);
						if (packet != null) {
							serverPlayerEntity.networkHandler.sendPacket(packet);
						}
					}
				}
			}

			this.syncEntityData();
		}

		if (this.trackingTick % this.tickInterval == 0 || this.entity.velocityDirty || this.entity.getDataTracker().isDirty()) {
			if (this.entity.hasVehicle()) {
				int i = MathHelper.floor(this.entity.getYaw() * 256.0F / 360.0F);
				int j = MathHelper.floor(this.entity.getPitch() * 256.0F / 360.0F);
				boolean bl = Math.abs(i - this.lastYaw) >= 1 || Math.abs(j - this.lastPitch) >= 1;
				if (bl) {
					this.receiver.accept(new EntityS2CPacket.Rotate(this.entity.getId(), (byte)i, (byte)j, this.entity.isOnGround()));
					this.lastYaw = i;
					this.lastPitch = j;
				}

				this.trackedPos.setPos(this.entity.getSyncedPos());
				this.syncEntityData();
				this.hadVehicle = true;
			} else {
				this.updatesWithoutVehicle++;
				int i = MathHelper.floor(this.entity.getYaw() * 256.0F / 360.0F);
				int j = MathHelper.floor(this.entity.getPitch() * 256.0F / 360.0F);
				Vec3d vec3d = this.entity.getSyncedPos();
				boolean bl2 = this.trackedPos.subtract(vec3d).lengthSquared() >= 7.6293945E-6F;
				Packet<?> packet2 = null;
				boolean bl3 = bl2 || this.trackingTick % 60 == 0;
				boolean bl4 = Math.abs(i - this.lastYaw) >= 1 || Math.abs(j - this.lastPitch) >= 1;
				boolean bl5 = false;
				boolean bl6 = false;
				if (this.trackingTick > 0 || this.entity instanceof PersistentProjectileEntity) {
					long l = this.trackedPos.getDeltaX(vec3d);
					long m = this.trackedPos.getDeltaY(vec3d);
					long n = this.trackedPos.getDeltaZ(vec3d);
					boolean bl7 = l < -32768L || l > 32767L || m < -32768L || m > 32767L || n < -32768L || n > 32767L;
					if (bl7 || this.updatesWithoutVehicle > 400 || this.hadVehicle || this.lastOnGround != this.entity.isOnGround()) {
						this.lastOnGround = this.entity.isOnGround();
						this.updatesWithoutVehicle = 0;
						packet2 = new EntityPositionS2CPacket(this.entity);
						bl5 = true;
						bl6 = true;
					} else if ((!bl3 || !bl4) && !(this.entity instanceof PersistentProjectileEntity)) {
						if (bl3) {
							packet2 = new EntityS2CPacket.MoveRelative(this.entity.getId(), (short)((int)l), (short)((int)m), (short)((int)n), this.entity.isOnGround());
							bl5 = true;
						} else if (bl4) {
							packet2 = new EntityS2CPacket.Rotate(this.entity.getId(), (byte)i, (byte)j, this.entity.isOnGround());
							bl6 = true;
						}
					} else {
						packet2 = new EntityS2CPacket.RotateAndMoveRelative(
							this.entity.getId(), (short)((int)l), (short)((int)m), (short)((int)n), (byte)i, (byte)j, this.entity.isOnGround()
						);
						bl5 = true;
						bl6 = true;
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
				if (bl5) {
					this.trackedPos.setPos(vec3d);
				}

				if (bl6) {
					this.lastYaw = i;
					this.lastPitch = j;
				}

				this.hadVehicle = false;
			}

			int ix = MathHelper.floor(this.entity.getHeadYaw() * 256.0F / 360.0F);
			if (Math.abs(ix - this.lastHeadYaw) >= 1) {
				this.receiver.accept(new EntitySetHeadYawS2CPacket(this.entity, (byte)ix));
				this.lastHeadYaw = ix;
			}

			this.entity.velocityDirty = false;
		}

		this.trackingTick++;
		if (this.entity.velocityModified) {
			this.sendSyncPacket(new EntityVelocityUpdateS2CPacket(this.entity));
			if (this.entity instanceof ExplosiveProjectileEntity explosiveProjectileEntity) {
				this.sendSyncPacket(
					new ProjectilePowerS2CPacket(
						explosiveProjectileEntity.getId(), explosiveProjectileEntity.powerX, explosiveProjectileEntity.powerY, explosiveProjectileEntity.powerZ
					)
				);
			}

			this.entity.velocityModified = false;
		}
	}

	private static Stream<Entity> streamChangedPassengers(List<Entity> passengers, List<Entity> lastPassengers) {
		return lastPassengers.stream().filter(passenger -> !passengers.contains(passenger));
	}

	public void stopTracking(ServerPlayerEntity player) {
		this.entity.onStoppedTrackingBy(player);
		player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(this.entity.getId()));
	}

	public void startTracking(ServerPlayerEntity player) {
		List<Packet<? super ClientPlayPacketListener>> list = new ArrayList();
		this.sendPackets(player, list::add);
		player.networkHandler.sendPacket(new BundleS2CPacket(list));
		this.entity.onStartedTrackingBy(player);
	}

	public void sendPackets(ServerPlayerEntity player, Consumer<Packet<ClientPlayPacketListener>> sender) {
		if (this.entity.isRemoved()) {
			LOGGER.warn("Fetching packet for removed entity {}", this.entity);
		}

		Packet<ClientPlayPacketListener> packet = this.entity.createSpawnPacket();
		this.lastHeadYaw = MathHelper.floor(this.entity.getHeadYaw() * 256.0F / 360.0F);
		sender.accept(packet);
		if (this.changedEntries != null) {
			sender.accept(new EntityTrackerUpdateS2CPacket(this.entity.getId(), this.changedEntries));
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
		List<DataTracker.SerializedEntry<?>> list = dataTracker.getDirtyEntries();
		if (list != null) {
			this.changedEntries = dataTracker.getChangedEntries();
			this.sendSyncPacket(new EntityTrackerUpdateS2CPacket(this.entity.getId(), list));
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
