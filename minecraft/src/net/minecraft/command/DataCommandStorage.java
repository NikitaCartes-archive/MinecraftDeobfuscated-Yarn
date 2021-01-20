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

	public DataCommandStorage(PersistentStateManager stateManager) {
		this.stateManager = stateManager;
	}

	private DataCommandStorage.PersistentState createStorage(String namespace) {
		DataCommandStorage.PersistentState persistentState = new DataCommandStorage.PersistentState();
		this.storages.put(namespace, persistentState);
		return persistentState;
	}

	public CompoundTag get(Identifier id) {
		String string = id.getNamespace();
		DataCommandStorage.PersistentState persistentState = this.stateManager
			.get(compoundTag -> this.createStorage(string).method_32383(compoundTag), getSaveKey(string));
		return persistentState != null ? persistentState.get(id.getPath()) : new CompoundTag();
	}

	public void set(Identifier id, CompoundTag tag) {
		String string = id.getNamespace();
		this.stateManager
			.<DataCommandStorage.PersistentState>getOrCreate(
				compoundTag -> this.createStorage(string).method_32383(compoundTag), () -> this.createStorage(string), getSaveKey(string)
			)
			.set(id.getPath(), tag);
	}

	public Stream<Identifier> getIds() {
		return this.storages.entrySet().stream().flatMap(entry -> ((DataCommandStorage.PersistentState)entry.getValue()).getIds((String)entry.getKey()));
	}

	private static String getSaveKey(String namespace) {
		return "command_storage_" + namespace;
	}

	static class PersistentState extends net.minecraft.world.PersistentState {
		private final Map<String, CompoundTag> map = Maps.<String, CompoundTag>newHashMap();

		private PersistentState() {
		}

		private DataCommandStorage.PersistentState method_32383(CompoundTag compoundTag) {
			CompoundTag compoundTag2 = compoundTag.getCompound("contents");

			for (String string : compoundTag2.getKeys()) {
				this.map.put(string, compoundTag2.getCompound(string));
			}

			return this;
		}

		@Override
		public CompoundTag toNbt(CompoundTag tag) {
			CompoundTag compoundTag = new CompoundTag();
			this.map.forEach((string, compoundTag2) -> compoundTag.put(string, compoundTag2.copy()));
			tag.put("contents", compoundTag);
			return tag;
		}

		public CompoundTag get(String name) {
			CompoundTag compoundTag = (CompoundTag)this.map.get(name);
			return compoundTag != null ? compoundTag : new CompoundTag();
		}

		public void set(String name, CompoundTag tag) {
			if (tag.isEmpty()) {
				this.map.remove(name);
			} else {
				this.map.put(name, tag);
			}

			this.markDirty();
		}

		public Stream<Identifier> getIds(String namespace) {
			return this.map.keySet().stream().map(string2 -> new Identifier(namespace, string2));
		}
	}
}
