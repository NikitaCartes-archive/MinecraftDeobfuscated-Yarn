package net.minecraft.world;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.dimension.DimensionType;

public interface PersistentStateContainer {
	@Nullable
	PersistentStateManager getPersistentStateManager();

	@Nullable
	default <T extends PersistentState> T method_8648(DimensionType dimensionType, Function<String, T> function, String string) {
		PersistentStateManager persistentStateManager = this.getPersistentStateManager();
		return persistentStateManager == null ? null : persistentStateManager.get(dimensionType, function, string);
	}

	default void method_8647(DimensionType dimensionType, String string, PersistentState persistentState) {
		PersistentStateManager persistentStateManager = this.getPersistentStateManager();
		if (persistentStateManager != null) {
			persistentStateManager.set(dimensionType, string, persistentState);
		}
	}

	default int getNextAvailableId(DimensionType dimensionType, String string) {
		return this.getPersistentStateManager().getNextAvailableId(dimensionType, string);
	}
}
