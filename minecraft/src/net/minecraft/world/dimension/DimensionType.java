package net.minecraft.world.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;

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

	private static DimensionType register(String id, DimensionType dimension) {
		return Registry.register(Registry.DIMENSION_TYPE, dimension.id, id, dimension);
	}

	public DimensionType(
		int dimensionId,
		String suffix,
		String saveDir,
		BiFunction<World, DimensionType, ? extends Dimension> factory,
		boolean hasSkylight,
		BiomeAccessType biomeAccessType
	) {
		this.id = dimensionId;
		this.suffix = suffix;
		this.saveDir = saveDir;
		this.factory = factory;
		this.hasSkyLight = hasSkylight;
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

	public File getSaveDirectory(File root) {
		return this.saveDir.isEmpty() ? root : new File(root, this.saveDir);
	}

	public Dimension create(World world) {
		return (Dimension)this.factory.apply(world, this);
	}

	public String toString() {
		return getId(this).toString();
	}

	@Nullable
	public static DimensionType byRawId(int id) {
		return Registry.DIMENSION_TYPE.get(id - -1);
	}

	@Nullable
	public static DimensionType byId(Identifier identifier) {
		return Registry.DIMENSION_TYPE.get(identifier);
	}

	@Nullable
	public static Identifier getId(DimensionType type) {
		return Registry.DIMENSION_TYPE.getId(type);
	}

	public boolean hasSkyLight() {
		return this.hasSkyLight;
	}

	public BiomeAccessType getBiomeAccessType() {
		return this.biomeAccessType;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createString(Registry.DIMENSION_TYPE.getId(this).toString());
	}

	public boolean method_26523() {
		return false;
	}
}
