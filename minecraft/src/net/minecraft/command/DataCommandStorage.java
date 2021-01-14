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

	private DataCommandStorage.PersistentState createStorage(String namespace, String saveKey) {
		DataCommandStorage.PersistentState persistentState = new DataCommandStorage.PersistentState(saveKey);
		this.storages.put(namespace, persistentState);
		return persistentState;
	}

	public NbtCompound get(Identifier id) {
		String string = id.getNamespace();
		String string2 = getSaveKey(string);
		DataCommandStorage.PersistentState persistentState = this.stateManager.get(() -> this.createStorage(string, string2), string2);
		return persistentState != null ? persistentState.get(id.getPath()) : new NbtCompound();
	}

	public void set(Identifier id, NbtCompound nbt) {
		String string = id.getNamespace();
		String string2 = getSaveKey(string);
		this.stateManager.<DataCommandStorage.PersistentState>getOrCreate(() -> this.createStorage(string, string2), string2).set(id.getPath(), nbt);
	}

	public Stream<Identifier> getIds() {
		return this.storages.entrySet().stream().flatMap(entry -> ((DataCommandStorage.PersistentState)entry.getValue()).getIds((String)entry.getKey()));
	}

	private static String getSaveKey(String namespace) {
		return "command_storage_" + namespace;
	}

	static class PersistentState extends net.minecraft.world.PersistentState {
		private final Map<String, NbtCompound> map = Maps.<String, NbtCompound>newHashMap();

		public PersistentState(String string) {
			super(string);
		}

		@Override
		public void fromTag(NbtCompound tag) {
			NbtCompound nbtCompound = tag.getCompound("contents");

			for (String string : nbtCompound.getKeys()) {
				this.map.put(string, nbtCompound.getCompound(string));
			}
		}

		@Override
		public NbtCompound writeNbt(NbtCompound nbt) {
			NbtCompound nbtCompound = new NbtCompound();
			this.map.forEach((string, nbtCompound2) -> nbtCompound.put(string, nbtCompound2.copy()));
			nbt.put("contents", nbtCompound);
			return nbt;
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
