package net.minecraft.world.chunk.storage;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;

public class VersionedRegionFileCache extends RegionFileCache {
	protected final DataFixer dataFixer;
	@Nullable
	private FeatureUpdater field_17654;

	public VersionedRegionFileCache(File file, DataFixer dataFixer) {
		super(file);
		this.dataFixer = dataFixer;
	}

	public CompoundTag method_17907(DimensionType dimensionType, Supplier<PersistentStateManager> supplier, CompoundTag compoundTag) {
		int i = method_17908(compoundTag);
		int j = 1493;
		if (i < 1493) {
			compoundTag = TagHelper.update(this.dataFixer, DataFixTypes.CHUNK, compoundTag, i, 1493);
			if (compoundTag.getCompound("Level").getBoolean("hasLegacyStructureData")) {
				if (this.field_17654 == null) {
					this.field_17654 = FeatureUpdater.method_14745(dimensionType, (PersistentStateManager)supplier.get());
				}

				compoundTag = this.field_17654.method_14735(compoundTag);
			}
		}

		compoundTag = TagHelper.update(this.dataFixer, DataFixTypes.CHUNK, compoundTag, Math.max(1493, i));
		if (i < SharedConstants.getGameVersion().getWorldVersion()) {
			compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		}

		return compoundTag;
	}

	public static int method_17908(CompoundTag compoundTag) {
		return compoundTag.containsKey("DataVersion", 99) ? compoundTag.getInt("DataVersion") : -1;
	}

	@Override
	public void method_17910(ChunkPos chunkPos, CompoundTag compoundTag) throws IOException {
		super.method_17910(chunkPos, compoundTag);
		if (this.field_17654 != null) {
			this.field_17654.markResolved(chunkPos.toLong());
		}
	}
}
