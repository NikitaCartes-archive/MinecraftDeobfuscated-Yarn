package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.client.network.packet.EntityPassengersSetS2CPacket;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {
	private static final Logger LOGGER = LogManager.getLogger();
	private final World world;
	private final Set<EntityTrackerEntry> trackedEntities = Sets.<EntityTrackerEntry>newHashSet();
	private final IntHashMap<EntityTrackerEntry> trackedEntitiesById = new IntHashMap<>();
	private int field_13906;

	public EntityTracker(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.setViewDistance(serverWorld.getServer().getPlayerManager().getViewDistance());
	}

	public void add(Entity entity) {
		if (entity instanceof ServerPlayerEntity) {
			this.add(entity, 512, 2);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;

			for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
				if (entityTrackerEntry.getEntity() != serverPlayerEntity) {
					entityTrackerEntry.method_14303(serverPlayerEntity);
				}
			}
		} else if (entity instanceof FishHookEntity) {
			this.add(entity, 64, 5, true);
		} else if (entity instanceof ProjectileEntity) {
			this.add(entity, 64, 20, true);
		} else if (entity instanceof SmallFireballEntity) {
			this.add(entity, 64, 10, true);
		} else if (entity instanceof ExplosiveProjectileEntity) {
			this.add(entity, 64, 10, true);
		} else if (entity instanceof SnowballEntity) {
			this.add(entity, 64, 10, true);
		} else if (entity instanceof LlamaSpitEntity) {
			this.add(entity, 64, 10, false);
		} else if (entity instanceof ThrownEnderpearlEntity) {
			this.add(entity, 64, 10, true);
		} else if (entity instanceof EnderEyeEntity) {
			this.add(entity, 64, 4, true);
		} else if (entity instanceof ThrownEggEntity) {
			this.add(entity, 64, 10, true);
		} else if (entity instanceof ThrownPotionEntity) {
			this.add(entity, 64, 10, true);
		} else if (entity instanceof ThrownExperienceBottleEntity) {
			this.add(entity, 64, 10, true);
		} else if (entity instanceof FireworkEntity) {
			this.add(entity, 64, 10, true);
		} else if (entity instanceof ItemEntity) {
			this.add(entity, 64, 20, true);
		} else if (entity instanceof AbstractMinecartEntity) {
			this.add(entity, 80, 3, true);
		} else if (entity instanceof BoatEntity) {
			this.add(entity, 80, 3, true);
		} else if (entity instanceof SquidEntity) {
			this.add(entity, 64, 3, true);
		} else if (entity instanceof WitherEntity) {
			this.add(entity, 80, 3, false);
		} else if (entity instanceof ShulkerBulletEntity) {
			this.add(entity, 80, 3, true);
		} else if (entity instanceof BatEntity) {
			this.add(entity, 80, 3, false);
		} else if (entity instanceof EnderDragonEntity) {
			this.add(entity, 160, 3, true);
		} else if (entity instanceof MobEntity) {
			this.add(entity, 80, 3, true);
		} else if (entity instanceof PrimedTntEntity) {
			this.add(entity, 160, 10, true);
		} else if (entity instanceof FallingBlockEntity) {
			this.add(entity, 160, 20, true);
		} else if (entity instanceof AbstractDecorationEntity) {
			this.add(entity, 160, Integer.MAX_VALUE, false);
		} else if (entity instanceof ArmorStandEntity) {
			this.add(entity, 160, 3, true);
		} else if (entity instanceof ExperienceOrbEntity) {
			this.add(entity, 160, 20, true);
		} else if (entity instanceof AreaEffectCloudEntity) {
			this.add(entity, 160, Integer.MAX_VALUE, true);
		} else if (entity instanceof EnderCrystalEntity) {
			this.add(entity, 256, Integer.MAX_VALUE, false);
		} else if (entity instanceof EvokerFangsEntity) {
			this.add(entity, 160, 2, false);
		}
	}

	public void add(Entity entity, int i, int j) {
		this.add(entity, i, j, false);
	}

	public void add(Entity entity, int i, int j, boolean bl) {
		try {
			if (this.trackedEntitiesById.containsKey(entity.getEntityId())) {
				throw new IllegalStateException("Entity is already tracked!");
			}

			EntityTrackerEntry entityTrackerEntry = new EntityTrackerEntry(entity, i, this.field_13906, j, bl);
			this.trackedEntities.add(entityTrackerEntry);
			this.trackedEntitiesById.put(entity.getEntityId(), entityTrackerEntry);
			entityTrackerEntry.method_14300(this.world.players);
		} catch (Throwable var10) {
			CrashReport crashReport = CrashReport.create(var10, "Adding entity to track");
			CrashReportSection crashReportSection = crashReport.addElement("Entity To Track");
			crashReportSection.add("Tracking range", i + " blocks");
			crashReportSection.add("Update interval", (ICrashCallable<String>)(() -> {
				String string = "Once per " + j + " ticks";
				if (j == Integer.MAX_VALUE) {
					string = "Maximum (" + string + ")";
				}

				return string;
			}));
			entity.populateCrashReport(crashReportSection);
			this.trackedEntitiesById.get(entity.getEntityId()).getEntity().populateCrashReport(crashReport.addElement("Entity That Is Already Tracked"));

			try {
				throw new CrashException(crashReport);
			} catch (CrashException var9) {
				LOGGER.error("\"Silently\" catching entity tracking error.", (Throwable)var9);
			}
		}
	}

	public void remove(Entity entity) {
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;

			for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
				entityTrackerEntry.method_14302(serverPlayerEntity);
			}
		}

		EntityTrackerEntry entityTrackerEntry2 = this.trackedEntitiesById.remove(entity.getEntityId());
		if (entityTrackerEntry2 != null) {
			this.trackedEntities.remove(entityTrackerEntry2);
			entityTrackerEntry2.method_14304();
		}
	}

	public void method_14072(ServerPlayerEntity serverPlayerEntity) {
		for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
			entityTrackerEntry.method_14302(serverPlayerEntity);
		}

		this.remove(serverPlayerEntity);
	}

	public void method_14078() {
		List<ServerPlayerEntity> list = Lists.<ServerPlayerEntity>newArrayList();

		for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
			entityTrackerEntry.method_14297(this.world.players);
			if (entityTrackerEntry.field_14058) {
				Entity entity = entityTrackerEntry.getEntity();
				if (entity instanceof ServerPlayerEntity) {
					list.add((ServerPlayerEntity)entity);
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(i);

			for (EntityTrackerEntry entityTrackerEntry2 : this.trackedEntities) {
				if (entityTrackerEntry2.getEntity() != serverPlayerEntity) {
					entityTrackerEntry2.method_14303(serverPlayerEntity);
				}
			}
		}
	}

	public void method_14071(ServerPlayerEntity serverPlayerEntity) {
		for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
			if (entityTrackerEntry.getEntity() == serverPlayerEntity) {
				entityTrackerEntry.method_14300(this.world.players);
			} else {
				entityTrackerEntry.method_14303(serverPlayerEntity);
			}
		}
	}

	public void sendToTrackingPlayers(Entity entity, Packet<?> packet) {
		EntityTrackerEntry entityTrackerEntry = this.trackedEntitiesById.get(entity.getEntityId());
		if (entityTrackerEntry != null) {
			entityTrackerEntry.sendToTrackingPlayers(packet);
		}
	}

	public void sendToTrackingPlayersAndSelf(Entity entity, Packet<?> packet) {
		EntityTrackerEntry entityTrackerEntry = this.trackedEntitiesById.get(entity.getEntityId());
		if (entityTrackerEntry != null) {
			entityTrackerEntry.sendToTrackingPlayersAndSelf(packet);
		}
	}

	public void sendEntitiesInChunk(ServerPlayerEntity serverPlayerEntity, int i, int j) {
		List<Entity> list = Lists.<Entity>newArrayList();
		List<Entity> list2 = Lists.<Entity>newArrayList();

		for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
			Entity entity = entityTrackerEntry.getEntity();
			if (entity != serverPlayerEntity && entity.chunkX == i && entity.chunkZ == j) {
				entityTrackerEntry.method_14303(serverPlayerEntity);
				if (entity instanceof MobEntity && ((MobEntity)entity).getHoldingEntity() != null) {
					list.add(entity);
				}

				if (!entity.getPassengerList().isEmpty()) {
					list2.add(entity);
				}
			}
		}

		if (!list.isEmpty()) {
			for (Entity entity2 : list) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityAttachS2CPacket(entity2, ((MobEntity)entity2).getHoldingEntity()));
			}
		}

		if (!list2.isEmpty()) {
			for (Entity entity2 : list2) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity2));
			}
		}
	}

	public void setViewDistance(int i) {
		this.field_13906 = (i - 1) * 16;

		for (EntityTrackerEntry entityTrackerEntry : this.trackedEntities) {
			entityTrackerEntry.method_14296(this.field_13906);
		}
	}
}
