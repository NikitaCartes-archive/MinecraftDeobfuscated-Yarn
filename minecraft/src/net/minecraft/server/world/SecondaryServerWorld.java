package net.minecraft.server.world;

import java.util.concurrent.Executor;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.WorldVillageManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.UnmodifiableLevelProperties;

public class SecondaryServerWorld extends ServerWorld {
	public SecondaryServerWorld(
		MinecraftServer minecraftServer,
		Executor executor,
		WorldSaveHandler worldSaveHandler,
		DimensionType dimensionType,
		ServerWorld serverWorld,
		Profiler profiler
	) {
		super(
			minecraftServer,
			executor,
			worldSaveHandler,
			serverWorld.getPersistentStateManager(),
			new UnmodifiableLevelProperties(serverWorld.getLevelProperties()),
			dimensionType,
			profiler
		);
		serverWorld.getWorldBorder().addListener(new WorldBorderListener() {
			@Override
			public void onSizeChange(WorldBorder worldBorder, double d) {
				SecondaryServerWorld.this.getWorldBorder().setSize(d);
			}

			@Override
			public void method_11931(WorldBorder worldBorder, double d, double e, long l) {
				SecondaryServerWorld.this.getWorldBorder().method_11957(d, e, l);
			}

			@Override
			public void onCenterChanged(WorldBorder worldBorder, double d, double e) {
				SecondaryServerWorld.this.getWorldBorder().setCenter(d, e);
			}

			@Override
			public void onWarningTimeChanged(WorldBorder worldBorder, int i) {
				SecondaryServerWorld.this.getWorldBorder().setWarningTime(i);
			}

			@Override
			public void onWarningBlocksChanged(WorldBorder worldBorder, int i) {
				SecondaryServerWorld.this.getWorldBorder().setWarningBlocks(i);
			}

			@Override
			public void onDamagePerBlockChanged(WorldBorder worldBorder, double d) {
				SecondaryServerWorld.this.getWorldBorder().setDamagePerBlock(d);
			}

			@Override
			public void onSafeZoneChanged(WorldBorder worldBorder, double d) {
				SecondaryServerWorld.this.getWorldBorder().setSafeZone(d);
			}
		});
	}

	@Override
	protected void saveLevel() {
	}

	@Override
	protected void tickTime() {
	}

	public SecondaryServerWorld initializeAsSecondaryWorld() {
		String string = RaidManager.nameFor(this.dimension);
		RaidManager raidManager = this.method_8648(this.dimension.getType(), RaidManager::new, string);
		if (raidManager == null) {
			this.raidManager = new RaidManager(this);
			this.method_8647(this.dimension.getType(), string, this.raidManager);
		} else {
			this.raidManager = raidManager;
			this.raidManager.setWorld(this);
		}

		String string2 = WorldVillageManager.getBaseTag(this.dimension);
		WorldVillageManager worldVillageManager = this.method_8648(DimensionType.field_13072, WorldVillageManager::new, string2);
		if (worldVillageManager == null) {
			this.villageManager = new WorldVillageManager(this);
			this.method_8647(DimensionType.field_13072, string2, this.villageManager);
		} else {
			this.villageManager = worldVillageManager;
			this.villageManager.setWorld(this);
		}

		this.villageManager.method_16471();
		return this;
	}

	public void saveWorldData() {
		this.dimension.saveWorldData();
	}
}
