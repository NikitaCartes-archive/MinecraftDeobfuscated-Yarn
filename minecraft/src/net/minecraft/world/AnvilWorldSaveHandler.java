package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

public class AnvilWorldSaveHandler extends OldWorldSaveHandler {
	public AnvilWorldSaveHandler(File file, String string, @Nullable MinecraftServer minecraftServer, DataFixer dataFixer) {
		super(file, string, minecraftServer, dataFixer);
	}

	@Override
	public ChunkSaveHandler createChunkSaveHandler(Dimension dimension) {
		File file = dimension.getType().getFile(this.getWorldDir());
		file.mkdirs();
		return new ChunkSaveHandlerImpl(file, this.field_148);
	}

	@Override
	public void saveWorld(LevelProperties levelProperties, @Nullable CompoundTag compoundTag) {
		levelProperties.setVersion(19133);
		super.saveWorld(levelProperties, compoundTag);
	}
}
