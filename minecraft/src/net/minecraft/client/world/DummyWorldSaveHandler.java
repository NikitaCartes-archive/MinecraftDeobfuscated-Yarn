package net.minecraft.client.world;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.world.ChunkSaveHandler;
import net.minecraft.world.PlayerSaveHandler;
import net.minecraft.world.SessionLockException;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

@Environment(EnvType.CLIENT)
public class DummyWorldSaveHandler implements WorldSaveHandler {
	@Override
	public LevelProperties readProperties() {
		return null;
	}

	@Override
	public void checkSessionLock() throws SessionLockException {
	}

	@Override
	public ChunkSaveHandler createChunkSaveHandler(Dimension dimension) {
		return null;
	}

	@Override
	public void saveWorld(LevelProperties levelProperties, CompoundTag compoundTag) {
	}

	@Override
	public void saveWorld(LevelProperties levelProperties) {
	}

	@Override
	public PlayerSaveHandler getPlayerSaveHandler() {
		return null;
	}

	@Nullable
	@Override
	public File getDataFile(DimensionType dimensionType, String string) {
		return null;
	}

	@Override
	public File getWorldDir() {
		return null;
	}

	@Override
	public StructureManager getStructureManager() {
		return null;
	}

	@Override
	public DataFixer getDataFixer() {
		return null;
	}
}
