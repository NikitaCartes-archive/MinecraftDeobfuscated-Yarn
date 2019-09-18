package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentStateManager;

public class DataCommandStorage {
	private final Map<String, DataCommandStorage.PersistentState> storages = Maps.<String, DataCommandStorage.PersistentState>newHashMap();
	private final PersistentStateManager stateManager;

	public DataCommandStorage(PersistentStateManager persistentStateManager) {
		this.stateManager = persistentStateManager;
	}

	private DataCommandStorage.PersistentState createStorage(String string, String string2) {
		DataCommandStorage.PersistentState persistentState = new DataCommandStorage.PersistentState(string2);
		this.storages.put(string, persistentState);
		return persistentState;
	}

	public CompoundTag get(Identifier identifier) {
		String string = identifier.getNamespace();
		String string2 = getSaveKey(string);
		DataCommandStorage.PersistentState persistentState = this.stateManager.get(() -> this.createStorage(string, string2), string2);
		return persistentState != null ? persistentState.get(identifier.getPath()) : new CompoundTag();
	}

	public void set(Identifier identifier, CompoundTag compoundTag) {
		String string = identifier.getNamespace();
		String string2 = getSaveKey(string);
		this.stateManager.<DataCommandStorage.PersistentState>getOrCreate(() -> this.createStorage(string, string2), string2).set(identifier.getPath(), compoundTag);
	}

	public Stream<Identifier> getIds() {
		return this.storages.entrySet().stream().flatMap(entry -> ((DataCommandStorage.PersistentState)entry.getValue()).getIds((String)entry.getKey()));
	}

	private static String getSaveKey(String string) {
		return "command_storage_" + string;
	}

	static class PersistentState extends net.minecraft.world.PersistentState {
		private final Map<String, CompoundTag> map = Maps.<String, CompoundTag>newHashMap();

		public PersistentState(String string) {
			super(string);
		}

		@Override
		public void fromTag(CompoundTag compoundTag) {
			CompoundTag compoundTag2 = compoundTag.getCompound("contents");

			for (String string : compoundTag2.getKeys()) {
				this.map.put(string, compoundTag2.getCompound(string));
			}
		}

		@Override
		public CompoundTag toTag(CompoundTag compoundTag) {
			CompoundTag compoundTag2 = new CompoundTag();
			this.map.forEach((string, compoundTag2x) -> compoundTag2.put(string, compoundTag2x.method_10553()));
			compoundTag.put("contents", compoundTag2);
			return compoundTag;
		}

		public CompoundTag get(String string) {
			CompoundTag compoundTag = (CompoundTag)this.map.get(string);
			return compoundTag != null ? compoundTag : new CompoundTag();
		}

		public void set(String string, CompoundTag compoundTag) {
			if (compoundTag.isEmpty()) {
				this.map.remove(string);
			} else {
				this.map.put(string, compoundTag);
			}

			this.markDirty();
		}

		public Stream<Identifier> getIds(String string) {
			return this.map.keySet().stream().map(string2 -> new Identifier(string, string2));
		}
	}
}
