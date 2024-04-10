package net.minecraft.datafixer;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

public enum DataFixTypes {
	LEVEL(TypeReferences.LEVEL),
	PLAYER(TypeReferences.PLAYER),
	CHUNK(TypeReferences.CHUNK),
	HOTBAR(TypeReferences.HOTBAR),
	OPTIONS(TypeReferences.OPTIONS),
	STRUCTURE(TypeReferences.STRUCTURE),
	STATS(TypeReferences.STATS),
	SAVED_DATA_COMMAND_STORAGE(TypeReferences.SAVED_DATA_COMMAND_STORAGE),
	SAVED_DATA_FORCED_CHUNKS(TypeReferences.SAVED_DATA_CHUNKS),
	SAVED_DATA_MAP_DATA(TypeReferences.SAVED_DATA_MAP_DATA),
	SAVED_DATA_MAP_INDEX(TypeReferences.SAVED_DATA_IDCOUNTS),
	SAVED_DATA_RAIDS(TypeReferences.SAVED_DATA_RAIDS),
	SAVED_DATA_RANDOM_SEQUENCES(TypeReferences.SAVED_DATA_RANDOM_SEQUENCES),
	SAVED_DATA_SCOREBOARD(TypeReferences.SAVED_DATA_SCOREBOARD),
	SAVED_DATA_STRUCTURE_FEATURE_INDICES(TypeReferences.SAVED_DATA_STRUCTURE_FEATURE_INDICES),
	ADVANCEMENTS(TypeReferences.ADVANCEMENTS),
	POI_CHUNK(TypeReferences.POI_CHUNK),
	WORLD_GEN_SETTINGS(TypeReferences.WORLD_GEN_SETTINGS),
	ENTITY_CHUNK(TypeReferences.ENTITY_CHUNK);

	public static final Set<TypeReference> REQUIRED_TYPES;
	private final TypeReference typeReference;

	private DataFixTypes(final TypeReference typeReference) {
		this.typeReference = typeReference;
	}

	static int getSaveVersionId() {
		return SharedConstants.getGameVersion().getSaveVersion().getId();
	}

	public <A> Codec<A> createDataFixingCodec(Codec<A> baseCodec, DataFixer dataFixer, int currentDataVersion) {
		return new Codec<A>() {
			@Override
			public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
				return baseCodec.encode(input, ops, prefix)
					.flatMap(encoded -> ops.mergeToMap((T)encoded, ops.createString("DataVersion"), ops.createInt(DataFixTypes.getSaveVersionId())));
			}

			@Override
			public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
				int i = (Integer)ops.get(input, "DataVersion").flatMap(ops::getNumberValue).map(Number::intValue).result().orElse(currentDataVersion);
				Dynamic<T> dynamic = new Dynamic<>(ops, ops.remove(input, "DataVersion"));
				Dynamic<T> dynamic2 = DataFixTypes.this.update(dataFixer, dynamic, i);
				return baseCodec.decode(dynamic2);
			}
		};
	}

	/**
	 * {@return {@code dynamic} updated from {@code oldVersion} to {@code newVersion}}
	 */
	public <T> Dynamic<T> update(DataFixer dataFixer, Dynamic<T> dynamic, int oldVersion, int newVersion) {
		return dataFixer.update(this.typeReference, dynamic, oldVersion, newVersion);
	}

	/**
	 * {@return {@code dynamic} updated from {@code oldVersion} to the current version}
	 */
	public <T> Dynamic<T> update(DataFixer dataFixer, Dynamic<T> dynamic, int oldVersion) {
		return this.update(dataFixer, dynamic, oldVersion, getSaveVersionId());
	}

	/**
	 * {@return {@code nbt} updated from {@code oldVersion} to {@code newVersion}}
	 * 
	 * @see net.minecraft.nbt.NbtHelper#getDataVersion
	 */
	public NbtCompound update(DataFixer dataFixer, NbtCompound nbt, int oldVersion, int newVersion) {
		return (NbtCompound)this.update(dataFixer, new Dynamic<>(NbtOps.INSTANCE, nbt), oldVersion, newVersion).getValue();
	}

	/**
	 * {@return {@code nbt} updated from {@code oldVersion} to the current version}
	 * 
	 * @see net.minecraft.nbt.NbtHelper#getDataVersion
	 */
	public NbtCompound update(DataFixer dataFixer, NbtCompound nbt, int oldVersion) {
		return this.update(dataFixer, nbt, oldVersion, getSaveVersionId());
	}

	static {
		REQUIRED_TYPES = Set.of(LEVEL.typeReference);
	}
}
