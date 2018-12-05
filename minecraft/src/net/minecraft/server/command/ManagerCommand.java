package net.minecraft.server.command;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_37;
import net.minecraft.nbt.PersistedState;
import net.minecraft.world.dimension.DimensionType;

public interface ManagerCommand {
	@Nullable
	class_37 method_8646();

	@Nullable
	default <T extends PersistedState> T method_8648(DimensionType dimensionType, Function<String, T> function, String string) {
		class_37 lv = this.method_8646();
		return lv == null ? null : lv.method_268(dimensionType, function, string);
	}

	default void method_8647(DimensionType dimensionType, String string, PersistedState persistedState) {
		class_37 lv = this.method_8646();
		if (lv != null) {
			lv.method_267(dimensionType, string, persistedState);
		}
	}

	default int method_8645(DimensionType dimensionType, String string) {
		return this.method_8646().method_266(dimensionType, string);
	}
}
