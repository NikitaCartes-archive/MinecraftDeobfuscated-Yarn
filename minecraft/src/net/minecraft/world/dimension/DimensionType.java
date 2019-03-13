package net.minecraft.world.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.class_4213;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class DimensionType implements class_4213 {
	public static final DimensionType field_13072 = register("overworld", new DimensionType(1, "", "", OverworldDimension::new, true));
	public static final DimensionType field_13076 = register("the_nether", new DimensionType(0, "_nether", "DIM-1", TheNetherDimension::new, false));
	public static final DimensionType field_13078 = register("the_end", new DimensionType(2, "_end", "DIM1", TheEndDimension::new, false));
	private final int id;
	private final String suffix;
	private final String saveDir;
	private final BiFunction<World, DimensionType, ? extends Dimension> factory;
	private final boolean hasSkyLight;

	private static DimensionType register(String string, DimensionType dimensionType) {
		return Registry.register(Registry.DIMENSION, dimensionType.id, string, dimensionType);
	}

	protected DimensionType(int i, String string, String string2, BiFunction<World, DimensionType, ? extends Dimension> biFunction, boolean bl) {
		this.id = i;
		this.suffix = string;
		this.saveDir = string2;
		this.factory = biFunction;
		this.hasSkyLight = bl;
	}

	public static DimensionType method_19298(Dynamic<?> dynamic) {
		return Registry.DIMENSION.method_10223(new Identifier(dynamic.asString("")));
	}

	public static Iterable<DimensionType> getAll() {
		return Registry.DIMENSION;
	}

	public int getRawId() {
		return this.id + -1;
	}

	public String getSuffix() {
		return this.suffix;
	}

	public File getFile(File file) {
		return this.saveDir.isEmpty() ? file : new File(file, this.saveDir);
	}

	public Dimension create(World world) {
		return (Dimension)this.factory.apply(world, this);
	}

	public String toString() {
		return method_12485(this).toString();
	}

	@Nullable
	public static DimensionType byRawId(int i) {
		return Registry.DIMENSION.get(i - -1);
	}

	@Nullable
	public static DimensionType method_12483(Identifier identifier) {
		return Registry.DIMENSION.method_10223(identifier);
	}

	@Nullable
	public static Identifier method_12485(DimensionType dimensionType) {
		return Registry.DIMENSION.method_10221(dimensionType);
	}

	public boolean hasSkyLight() {
		return this.hasSkyLight;
	}

	@Override
	public <T> T method_19508(DynamicOps<T> dynamicOps) {
		return dynamicOps.createString(Registry.DIMENSION.method_10221(this).toString());
	}
}
