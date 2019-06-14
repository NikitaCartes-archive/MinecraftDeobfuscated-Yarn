package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.RegionBasedStorage;

public class VersionedChunkStorage extends RegionBasedStorage {
	protected final DataFixer dataFixer;
	@Nullable
	private FeatureUpdater field_17654;

	public VersionedChunkStorage(File file, DataFixer dataFixer) {
		super(file);
		this.dataFixer = dataFixer;
	}

	public CompoundTag method_17907(DimensionType dimensionType, Supplier<PersistentStateManager> supplier, CompoundTag compoundTag) {
		int i = getDataVersion(compoundTag);
		int j = 1493;
		if (i < 1493) {
			compoundTag = TagHelper.update(this.dataFixer, DataFixTypes.field_19214, compoundTag, i, 1493);
			if (compoundTag.getCompound("Level").getBoolean("hasLegacyStructureData")) {
				if (this.field_17654 == null) {
					this.field_17654 = FeatureUpdater.method_14745(dimensionType, (PersistentStateManager)supplier.get());
				}

				compoundTag = this.field_17654.getUpdatedReferences(compoundTag);
			}
		}

		compoundTag = TagHelper.update(this.dataFixer, DataFixTypes.field_19214, compoundTag, Math.max(1493, i));
		if (i < SharedConstants.getGameVersion().getWorldVersion()) {
			compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		}

		return compoundTag;
	}

	public static int getDataVersion(CompoundTag compoundTag) {
		return compoundTag.containsKey("DataVersion", 99) ? compoundTag.getInt("DataVersion") : -1;
	}

	@Override
	public void setTagAt(ChunkPos chunkPos, CompoundTag compoundTag) throws IOException {
		super.setTagAt(chunkPos, compoundTag);
		if (this.field_17654 != null) {
			this.field_17654.markResolved(chunkPos.toLong());
		}
	}
}
