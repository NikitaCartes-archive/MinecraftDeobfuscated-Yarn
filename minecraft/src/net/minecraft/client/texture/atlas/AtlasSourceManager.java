package net.minecraft.client.texture.atlas;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AtlasSourceManager {
	private static final BiMap<Identifier, AtlasSourceType> SOURCE_TYPE_BY_ID = HashBiMap.create();
	public static final AtlasSourceType SINGLE = register("single", SingleAtlasSource.CODEC);
	public static final AtlasSourceType DIRECTORY = register("directory", DirectoryAtlasSource.CODEC);
	public static final AtlasSourceType FILTER = register("filter", FilterAtlasSource.CODEC);
	public static final AtlasSourceType UNSTITCH = register("unstitch", UnstitchAtlasSource.CODEC);
	public static final AtlasSourceType PALETTED_PERMUTATIONS = register("paletted_permutations", PalettedPermutationsAtlasSource.CODEC);
	public static Codec<AtlasSourceType> CODEC = Identifier.CODEC.flatXmap(id -> {
		AtlasSourceType atlasSourceType = (AtlasSourceType)SOURCE_TYPE_BY_ID.get(id);
		return atlasSourceType != null ? DataResult.success(atlasSourceType) : DataResult.error(() -> "Unknown type " + id);
	}, type -> {
		Identifier identifier = (Identifier)SOURCE_TYPE_BY_ID.inverse().get(type);
		return type != null ? DataResult.success(identifier) : DataResult.error(() -> "Unknown type " + identifier);
	});
	public static Codec<AtlasSource> TYPE_CODEC = CODEC.dispatch(AtlasSource::getType, AtlasSourceType::codec);
	public static Codec<List<AtlasSource>> LIST_CODEC = TYPE_CODEC.listOf().fieldOf("sources").codec();

	private static AtlasSourceType register(String id, MapCodec<? extends AtlasSource> codec) {
		AtlasSourceType atlasSourceType = new AtlasSourceType(codec);
		Identifier identifier = Identifier.ofVanilla(id);
		AtlasSourceType atlasSourceType2 = (AtlasSourceType)SOURCE_TYPE_BY_ID.putIfAbsent(identifier, atlasSourceType);
		if (atlasSourceType2 != null) {
			throw new IllegalStateException("Duplicate registration " + identifier);
		} else {
			return atlasSourceType;
		}
	}
}
