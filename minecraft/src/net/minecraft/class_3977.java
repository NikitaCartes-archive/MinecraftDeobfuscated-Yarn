package net.minecraft;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionalPersistentStateManager;

public abstract class class_3977 extends RegionFileCache {
	protected final DataFixer dataFixer;
	@Nullable
	private FeatureUpdater featureUpdater;

	public class_3977(DataFixer dataFixer) {
		this.dataFixer = dataFixer;
	}

	public CompoundTag updateChunkTag(DimensionType dimensionType, Supplier<DimensionalPersistentStateManager> supplier, CompoundTag compoundTag) {
		int i = getDataVersion(compoundTag);
		int j = 1493;
		if (i < 1493) {
			compoundTag = TagHelper.update(this.dataFixer, DataFixTypes.CHUNK, compoundTag, i, 1493);
			if (compoundTag.getCompound("Level").getBoolean("hasLegacyStructureData")) {
				if (this.featureUpdater == null) {
					this.featureUpdater = FeatureUpdater.create(dimensionType, (DimensionalPersistentStateManager)supplier.get());
				}

				compoundTag = this.featureUpdater.getUpdatedReferences(compoundTag);
			}
		}

		compoundTag = TagHelper.update(this.dataFixer, DataFixTypes.CHUNK, compoundTag, Math.max(1493, i));
		if (i < SharedConstants.getGameVersion().getWorldVersion()) {
			compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		}

		return compoundTag;
	}

	public static int getDataVersion(CompoundTag compoundTag) {
		return compoundTag.containsKey("DataVersion", 99) ? compoundTag.getInt("DataVersion") : -1;
	}

	@Override
	public void setChunkTag(ChunkPos chunkPos, CompoundTag compoundTag) throws IOException {
		super.setChunkTag(chunkPos, compoundTag);
		if (this.featureUpdater != null) {
			this.featureUpdater.markResolved(chunkPos.toLong());
		}
	}
}
