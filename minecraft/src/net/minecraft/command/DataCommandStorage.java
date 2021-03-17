package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
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

	public NbtCompound get(Identifier id) {
		String string = id.getNamespace();
		DataCommandStorage.PersistentState persistentState = this.stateManager
			.get(nbtCompound -> this.createStorage(string).method_32383(nbtCompound), getSaveKey(string));
		return persistentState != null ? persistentState.get(id.getPath()) : new NbtCompound();
	}

	public void set(Identifier id, NbtCompound tag) {
		String string = id.getNamespace();
		this.stateManager
			.<DataCommandStorage.PersistentState>getOrCreate(
				nbtCompound -> this.createStorage(string).method_32383(nbtCompound), () -> this.createStorage(string), getSaveKey(string)
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
		private final Map<String, NbtCompound> map = Maps.<String, NbtCompound>newHashMap();

		private PersistentState() {
		}

		private DataCommandStorage.PersistentState method_32383(NbtCompound nbtCompound) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("contents");

			for (String string : nbtCompound2.getKeys()) {
				this.map.put(string, nbtCompound2.getCompound(string));
			}

			return this;
		}

		@Override
		public NbtCompound writeNbt(NbtCompound tag) {
			NbtCompound nbtCompound = new NbtCompound();
			this.map.forEach((string, nbtCompound2) -> nbtCompound.put(string, nbtCompound2.copy()));
			tag.put("contents", nbtCompound);
			return tag;
		}

		public NbtCompound get(String name) {
			NbtCompound nbtCompound = (NbtCompound)this.map.get(name);
			return nbtCompound != null ? nbtCompound : new NbtCompound();
		}

		public void set(String name, NbtCompound tag) {
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
