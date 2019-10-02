package net.minecraft.world.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeAccessType;
import net.minecraft.world.biome.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.VoronoiBiomeAccessType;

public class DimensionType implements DynamicSerializable {
	public static final DimensionType OVERWORLD = register(
		"overworld", new DimensionType(1, "", "", OverworldDimension::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE)
	);
	public static final DimensionType THE_NETHER = register(
		"the_nether", new DimensionType(0, "_nether", "DIM-1", TheNetherDimension::new, false, VoronoiBiomeAccessType.INSTANCE)
	);
	public static final DimensionType THE_END = register(
		"the_end", new DimensionType(2, "_end", "DIM1", TheEndDimension::new, false, VoronoiBiomeAccessType.INSTANCE)
	);
	private final int id;
	private final String suffix;
	private final String saveDir;
	private final BiFunction<World, DimensionType, ? extends Dimension> factory;
	private final boolean hasSkyLight;
	private final BiomeAccessType biomeAccessType;

	private static DimensionType register(String string, DimensionType dimensionType) {
		return Registry.register(Registry.DIMENSION_TYPE, dimensionType.id, string, dimensionType);
	}

	protected DimensionType(
		int i, String string, String string2, BiFunction<World, DimensionType, ? extends Dimension> biFunction, boolean bl, BiomeAccessType biomeAccessType
	) {
		this.id = i;
		this.suffix = string;
		this.saveDir = string2;
		this.factory = biFunction;
		this.hasSkyLight = bl;
		this.biomeAccessType = biomeAccessType;
	}

	public static DimensionType deserialize(Dynamic<?> dynamic) {
		return Registry.DIMENSION_TYPE.get(new Identifier(dynamic.asString("")));
	}

	public static Iterable<DimensionType> getAll() {
		return Registry.DIMENSION_TYPE;
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
		return getId(this).toString();
	}

	@Nullable
	public static DimensionType byRawId(int i) {
		return Registry.DIMENSION_TYPE.get(i - -1);
	}

	@Nullable
	public static DimensionType byId(Identifier identifier) {
		return Registry.DIMENSION_TYPE.get(identifier);
	}

	@Nullable
	public static Identifier getId(DimensionType dimensionType) {
		return Registry.DIMENSION_TYPE.getId(dimensionType);
	}

	public boolean hasSkyLight() {
		return this.hasSkyLight;
	}

	public BiomeAccessType getBiomeAccessType() {
		return this.biomeAccessType;
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return dynamicOps.createString(Registry.DIMENSION_TYPE.getId(this).toString());
	}
}
