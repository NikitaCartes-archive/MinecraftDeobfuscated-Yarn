package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentStateManager;

public class DataCommandStorage {
	private static final String COMMAND_STORAGE_PREFIX = "command_storage_";
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

	public NbtCompound get(Identifier id) {
		String string = id.getNamespace();
		DataCommandStorage.PersistentState persistentState = this.stateManager.get(data -> this.createStorage(string).readNbt(data), getSaveKey(string));
		return persistentState != null ? persistentState.get(id.getPath()) : new NbtCompound();
	}

	public void set(Identifier id, NbtCompound nbt) {
		String string = id.getNamespace();
		this.stateManager
			.<DataCommandStorage.PersistentState>getOrCreate(data -> this.createStorage(string).readNbt(data), () -> this.createStorage(string), getSaveKey(string))
			.set(id.getPath(), nbt);
	}

	public Stream<Identifier> getIds() {
		return this.storages.entrySet().stream().flatMap(entry -> ((DataCommandStorage.PersistentState)entry.getValue()).getIds((String)entry.getKey()));
	}

	private static String getSaveKey(String namespace) {
		return "command_storage_" + namespace;
	}

	static class PersistentState extends net.minecraft.world.PersistentState {
		private static final String CONTENTS_KEY = "contents";
		private final Map<String, NbtCompound> map = Maps.<String, NbtCompound>newHashMap();

		DataCommandStorage.PersistentState readNbt(NbtCompound nbt) {
			NbtCompound nbtCompound = nbt.getCompound("contents");

			for (String string : nbtCompound.getKeys()) {
				this.map.put(string, nbtCompound.getCompound(string));
			}

			return this;
		}

		@Override
		public NbtCompound writeNbt(NbtCompound nbt) {
			NbtCompound nbtCompound = new NbtCompound();
			this.map.forEach((key, value) -> nbtCompound.put(key, value.copy()));
			nbt.put("contents", nbtCompound);
			return nbt;
		}

		public NbtCompound get(String name) {
			NbtCompound nbtCompound = (NbtCompound)this.map.get(name);
			return nbtCompound != null ? nbtCompound : new NbtCompound();
		}

		public void set(String name, NbtCompound nbt) {
			if (nbt.isEmpty()) {
				this.map.remove(name);
			} else {
				this.map.put(name, nbt);
			}

			this.markDirty();
		}

		public Stream<Identifier> getIds(String namespace) {
			return this.map.keySet().stream().map(key -> new Identifier(namespace, key));
		}
	}
}
