package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionalPersistentStateManager;

public class PersistentStateManager {
	private final Map<DimensionType, DimensionalPersistentStateManager> dimensionToManager;
	@Nullable
	private final WorldSaveHandler saveHandler;

	public PersistentStateManager(@Nullable WorldSaveHandler worldSaveHandler) {
		this.saveHandler = worldSaveHandler;
		Builder<DimensionType, DimensionalPersistentStateManager> builder = ImmutableMap.builder();

		for (DimensionType dimensionType : DimensionType.getAll()) {
			DimensionalPersistentStateManager dimensionalPersistentStateManager = new DimensionalPersistentStateManager(dimensionType, worldSaveHandler);
			builder.put(dimensionType, dimensionalPersistentStateManager);
			dimensionalPersistentStateManager.readIdCounts();
		}

		this.dimensionToManager = builder.build();
	}

	@Nullable
	public <T extends PersistentState> T get(DimensionType dimensionType, Function<String, T> function, String string) {
		return ((DimensionalPersistentStateManager)this.dimensionToManager.get(dimensionType)).get(function, string);
	}

	public void set(DimensionType dimensionType, String string, PersistentState persistentState) {
		((DimensionalPersistentStateManager)this.dimensionToManager.get(dimensionType)).set(string, persistentState);
	}

	public void save() {
		this.dimensionToManager.values().forEach(DimensionalPersistentStateManager::save);
	}

	public int getNextAvailableId(DimensionType dimensionType, String string) {
		return ((DimensionalPersistentStateManager)this.dimensionToManager.get(dimensionType)).getNextAvailableId(string);
	}

	public CompoundTag update(String string, int i) throws IOException {
		return DimensionalPersistentStateManager.update(this.saveHandler, DimensionType.field_13072, string, i);
	}
}
