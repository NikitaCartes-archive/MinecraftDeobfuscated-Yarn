package net.minecraft.nbt.scanner;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.ArrayDeque;
import java.util.Deque;
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
	private final Deque<Tree> selectionStack = new ArrayDeque();

	public SelectiveNbtCollector(Query... queries) {
		this.queriesLeft = queries.length;
		Builder<NbtType<?>> builder = ImmutableSet.builder();
		Tree tree = Tree.method_40060();

		for (Query query : queries) {
			tree.add(query);
			builder.add(query.type());
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
		Tree tree = (Tree)this.selectionStack.element();
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
		Tree tree = (Tree)this.selectionStack.element();
		if (this.getDepth() > tree.depth()) {
			return super.startSubNbt(type, key);
		} else if (tree.selectedFields().remove(key, type)) {
			this.queriesLeft--;
			return super.startSubNbt(type, key);
		} else {
			if (type == NbtCompound.TYPE) {
				Tree tree2 = (Tree)tree.fieldsToRecurse().get(key);
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
		if (this.getDepth() == ((Tree)this.selectionStack.element()).depth()) {
			this.selectionStack.pop();
		}

		return super.endNested();
	}

	public int getQueriesLeft() {
		return this.queriesLeft;
	}
}
