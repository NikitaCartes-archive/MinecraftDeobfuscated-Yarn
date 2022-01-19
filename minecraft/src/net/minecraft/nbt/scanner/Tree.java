package net.minecraft.nbt.scanner;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NbtType;

public record Tree(int depth, Map<String, NbtType<?>> selectedFields, Map<String, Tree> fieldsToRecurse) {
	private Tree(int depth) {
		this(depth, new HashMap(), new HashMap());
	}

	public static Tree method_40060() {
		return new Tree(1);
	}

	public void add(Query query) {
		if (this.depth <= query.path().size()) {
			((Tree)this.fieldsToRecurse.computeIfAbsent((String)query.path().get(this.depth - 1), path -> new Tree(this.depth + 1))).add(query);
		} else {
			this.selectedFields.put(query.key(), query.type());
		}
	}

	public boolean method_40061(NbtType<?> nbtType, String string) {
		return nbtType.equals(this.selectedFields().get(string));
	}
}
