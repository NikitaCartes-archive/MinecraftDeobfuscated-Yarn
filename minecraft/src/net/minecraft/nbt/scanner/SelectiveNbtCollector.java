package net.minecraft.nbt.scanner;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtType;

/**
 * A selective NBT collector builds an NBT object including only the
 * prescribed queries.
 */
public class SelectiveNbtCollector extends NbtCollector {
	private int queriesLeft;
	private final Set<NbtType<?>> allPossibleTypes;
	private final Deque<SelectiveNbtCollector.Tree> selectionStack = new ArrayDeque();

	public SelectiveNbtCollector(SelectiveNbtCollector.Query... queries) {
		this.queriesLeft = queries.length;
		Builder<NbtType<?>> builder = ImmutableSet.builder();
		SelectiveNbtCollector.Tree tree = new SelectiveNbtCollector.Tree(1);

		for (SelectiveNbtCollector.Query query : queries) {
			tree.add(query);
			builder.add(query.type);
		}

		this.selectionStack.push(tree);
		builder.add(NbtCompound.TYPE);
		this.allPossibleTypes = builder.build();
	}

	@Override
	public NbtScanner.Result start(NbtType<?> rootType) {
		return rootType != NbtCompound.TYPE ? NbtScanner.Result.HALT : super.start(rootType);
	}

	@Override
	public NbtScanner.NestedResult visitSubNbtType(NbtType<?> type) {
		SelectiveNbtCollector.Tree tree = (SelectiveNbtCollector.Tree)this.selectionStack.element();
		if (this.getDepth() > tree.depth()) {
			return super.visitSubNbtType(type);
		} else if (this.queriesLeft <= 0) {
			return NbtScanner.NestedResult.HALT;
		} else {
			return !this.allPossibleTypes.contains(type) ? NbtScanner.NestedResult.SKIP : super.visitSubNbtType(type);
		}
	}

	@Override
	public NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key) {
		SelectiveNbtCollector.Tree tree = (SelectiveNbtCollector.Tree)this.selectionStack.element();
		if (this.getDepth() > tree.depth()) {
			return super.startSubNbt(type, key);
		} else if (tree.fieldsToGet.remove(key, type)) {
			this.queriesLeft--;
			return super.startSubNbt(type, key);
		} else {
			if (type == NbtCompound.TYPE) {
				SelectiveNbtCollector.Tree tree2 = (SelectiveNbtCollector.Tree)tree.fieldsToRecurse.get(key);
				if (tree2 != null) {
					this.selectionStack.push(tree2);
					return super.startSubNbt(type, key);
				}
			}

			return NbtScanner.NestedResult.SKIP;
		}
	}

	@Override
	public NbtScanner.Result endNested() {
		if (this.getDepth() == ((SelectiveNbtCollector.Tree)this.selectionStack.element()).depth()) {
			this.selectionStack.pop();
		}

		return super.endNested();
	}

	public int getQueriesLeft() {
		return this.queriesLeft;
	}

	public static record Query() {
		final List<String> path;
		final NbtType<?> type;
		final String key;

		public Query(NbtType<?> type, String key) {
			this(List.of(), type, key);
		}

		public Query(String path, NbtType<?> type, String key) {
			this(List.of(path), type, key);
		}

		public Query(String path1, String path2, NbtType<?> type, String key) {
			this(List.of(path1, path2), type, key);
		}

		public Query(List<String> list, NbtType<?> nbtType, String string) {
			this.path = list;
			this.type = nbtType;
			this.key = string;
		}
	}

	static record Tree() {
		private final int depth;
		final Map<String, NbtType<?>> fieldsToGet;
		final Map<String, SelectiveNbtCollector.Tree> fieldsToRecurse;

		public Tree(int depth) {
			this(depth, new HashMap(), new HashMap());
		}

		private Tree(int i, Map<String, NbtType<?>> map, Map<String, SelectiveNbtCollector.Tree> map2) {
			this.depth = i;
			this.fieldsToGet = map;
			this.fieldsToRecurse = map2;
		}

		public void add(SelectiveNbtCollector.Query query) {
			if (this.depth <= query.path.size()) {
				((SelectiveNbtCollector.Tree)this.fieldsToRecurse
						.computeIfAbsent((String)query.path.get(this.depth - 1), path -> new SelectiveNbtCollector.Tree(this.depth + 1)))
					.add(query);
			} else {
				this.fieldsToGet.put(query.key, query.type);
			}
		}
	}
}
