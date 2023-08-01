package net.minecraft.datafixer;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.serialization.Dynamic;
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

	private DataFixTypes(TypeReference typeReference) {
		this.typeReference = typeReference;
	}

	private static int getSaveVersionId() {
		return SharedConstants.getGameVersion().getSaveVersion().getId();
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
