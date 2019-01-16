package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

public interface WorldSaveHandler {
	@Nullable
	LevelProperties readProperties();

	void checkSessionLock() throws SessionLockException;

	ChunkSaveHandler createChunkSaveHandler(Dimension dimension);

	void saveWorld(LevelProperties levelProperties, CompoundTag compoundTag);

	void saveWorld(LevelProperties levelProperties);

	PlayerSaveHandler getPlayerSaveHandler();

	File getWorldDir();

	@Nullable
	File getDataFile(DimensionType dimensionType, String string);

	StructureManager getStructureManager();

	DataFixer getDataFixer();
}
