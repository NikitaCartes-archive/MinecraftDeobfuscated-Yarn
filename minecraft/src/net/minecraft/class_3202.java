package net.minecraft;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.RaidState;
import net.minecraft.util.Profiler;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.WorldVillageManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.UnmodifiableLevelProperties;

public class class_3202 extends ServerWorld {
	public class_3202(MinecraftServer minecraftServer, WorldSaveHandler worldSaveHandler, DimensionType dimensionType, ServerWorld serverWorld, Profiler profiler) {
		super(
			minecraftServer, worldSaveHandler, serverWorld.method_8646(), new UnmodifiableLevelProperties(serverWorld.getLevelProperties()), dimensionType, profiler
		);
		serverWorld.getWorldBorder().addListener(new WorldBorderListener() {
			@Override
			public void onSizeChange(WorldBorder worldBorder, double d) {
				class_3202.this.getWorldBorder().setSize(d);
			}

			@Override
			public void method_11931(WorldBorder worldBorder, double d, double e, long l) {
				class_3202.this.getWorldBorder().method_11957(d, e, l);
			}

			@Override
			public void onCenterChanged(WorldBorder worldBorder, double d, double e) {
				class_3202.this.getWorldBorder().setCenter(d, e);
			}

			@Override
			public void onWarningTimeChanged(WorldBorder worldBorder, int i) {
				class_3202.this.getWorldBorder().setWarningTime(i);
			}

			@Override
			public void onWarningBlocksChanged(WorldBorder worldBorder, int i) {
				class_3202.this.getWorldBorder().setWarningBlocks(i);
			}

			@Override
			public void onDamagePerBlockChanged(WorldBorder worldBorder, double d) {
				class_3202.this.getWorldBorder().setDamagePerBlock(d);
			}

			@Override
			public void onSafeZoneChanged(WorldBorder worldBorder, double d) {
				class_3202.this.getWorldBorder().setSafeZone(d);
			}
		});
	}

	@Override
	protected void method_14188() {
	}

	@Override
	protected void tickTime() {
	}

	public class_3202 method_14033() {
		String string = RaidState.method_16533(this.dimension);
		RaidState raidState = this.method_8648(this.dimension.getType(), RaidState::new, string);
		if (raidState == null) {
			this.raidState = new RaidState(this);
			this.method_8647(this.dimension.getType(), string, this.raidState);
		} else {
			this.raidState = raidState;
			this.raidState.method_16530(this);
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

	public void method_14032() {
		this.dimension.method_12450();
	}
}
