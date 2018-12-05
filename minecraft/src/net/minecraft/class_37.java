package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.PersistedState;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;

public class class_37 {
	private final Map<DimensionType, class_26> field_218;
	@Nullable
	private final WorldSaveHandler field_217;

	public class_37(@Nullable WorldSaveHandler worldSaveHandler) {
		this.field_217 = worldSaveHandler;
		Builder<DimensionType, class_26> builder = ImmutableMap.builder();

		for (DimensionType dimensionType : DimensionType.getAll()) {
			class_26 lv = new class_26(dimensionType, worldSaveHandler);
			builder.put(dimensionType, lv);
			lv.method_122();
		}

		this.field_218 = builder.build();
	}

	@Nullable
	public <T extends PersistedState> T method_268(DimensionType dimensionType, Function<String, T> function, String string) {
		return ((class_26)this.field_218.get(dimensionType)).method_120(function, string);
	}

	public void method_267(DimensionType dimensionType, String string, PersistedState persistedState) {
		((class_26)this.field_218.get(dimensionType)).method_123(string, persistedState);
	}

	public void method_265() {
		this.field_218.values().forEach(class_26::method_125);
	}

	public int method_266(DimensionType dimensionType, String string) {
		return ((class_26)this.field_218.get(dimensionType)).method_124(string);
	}

	public CompoundTag method_264(String string, int i) throws IOException {
		return class_26.method_119(this.field_217, DimensionType.field_13072, string, i);
	}
}
