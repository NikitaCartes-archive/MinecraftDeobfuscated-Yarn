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
 * 
 * @see ExclusiveNbtCollector
 */
public class SelectiveNbtCollector extends NbtCollector {
	private int queriesLeft;
	private final Set<NbtType<?>> allPossibleTypes;
	private final Deque<NbtTreeNode> selectionStack = new ArrayDeque();

	public SelectiveNbtCollector(NbtScanQuery... queries) {
		this.queriesLeft = queries.length;
		Builder<NbtType<?>> builder = ImmutableSet.builder();
		NbtTreeNode nbtTreeNode = NbtTreeNode.createRoot();

		for (NbtScanQuery nbtScanQuery : queries) {
			nbtTreeNode.add(nbtScanQuery);
			builder.add(nbtScanQuery.type());
		}

		this.selectionStack.push(nbtTreeNode);
		builder.add(NbtCompound.TYPE);
		this.allPossibleTypes = builder.build();
	}

	@Override
	public NbtScanner.Result start(NbtType<?> rootType) {
		return rootType != NbtCompound.TYPE ? NbtScanner.Result.HALT : super.start(rootType);
	}

	@Override
	public NbtScanner.NestedResult visitSubNbtType(NbtType<?> type) {
		NbtTreeNode nbtTreeNode = (NbtTreeNode)this.selectionStack.element();
		if (this.getDepth() > nbtTreeNode.depth()) {
			return super.visitSubNbtType(type);
		} else if (this.queriesLeft <= 0) {
			return NbtScanner.NestedResult.HALT;
		} else {
			return !this.allPossibleTypes.contains(type) ? NbtScanner.NestedResult.SKIP : super.visitSubNbtType(type);
		}
	}

	@Override
	public NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key) {
		NbtTreeNode nbtTreeNode = (NbtTreeNode)this.selectionStack.element();
		if (this.getDepth() > nbtTreeNode.depth()) {
			return super.startSubNbt(type, key);
		} else if (nbtTreeNode.selectedFields().remove(key, type)) {
			this.queriesLeft--;
			return super.startSubNbt(type, key);
		} else {
			if (type == NbtCompound.TYPE) {
				NbtTreeNode nbtTreeNode2 = (NbtTreeNode)nbtTreeNode.fieldsToRecurse().get(key);
				if (nbtTreeNode2 != null) {
					this.selectionStack.push(nbtTreeNode2);
					return super.startSubNbt(type, key);
				}
			}

			return NbtScanner.NestedResult.SKIP;
		}
	}

	@Override
	public NbtScanner.Result endNested() {
		if (this.getDepth() == ((NbtTreeNode)this.selectionStack.element()).depth()) {
			this.selectionStack.pop();
		}

		return super.endNested();
	}

	public int getQueriesLeft() {
		return this.queriesLeft;
	}
}
