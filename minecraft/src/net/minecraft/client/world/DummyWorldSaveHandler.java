package net.minecraft.client.world;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3485;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.MinecraftException;
import net.minecraft.world.ChunkSaveHandler;
import net.minecraft.world.PlayerSaveHandler;
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
	public void checkSessionLock() throws MinecraftException {
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
	public class_3485 method_134() {
		return null;
	}

	@Override
	public DataFixer method_130() {
		return null;
	}
}
