package net.minecraft.nbt.scanner;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtType;

/**
 * An exclusive NBT collector builds an NBT object including everything
 * except the prescribed queries.
 * 
 * @see SelectiveNbtCollector
 */
public class ExclusiveNbtCollector extends NbtCollector {
	private final Deque<NbtTreeNode> treeStack = new ArrayDeque();

	public ExclusiveNbtCollector(NbtScanQuery... excludedQueries) {
		NbtTreeNode nbtTreeNode = NbtTreeNode.createRoot();

		for (NbtScanQuery nbtScanQuery : excludedQueries) {
			nbtTreeNode.add(nbtScanQuery);
		}

		this.treeStack.push(nbtTreeNode);
	}

	@Override
	public NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key) {
		NbtTreeNode nbtTreeNode = (NbtTreeNode)this.treeStack.element();
		if (nbtTreeNode.isTypeEqual(type, key)) {
			return NbtScanner.NestedResult.SKIP;
		} else {
			if (type == NbtCompound.TYPE) {
				NbtTreeNode nbtTreeNode2 = (NbtTreeNode)nbtTreeNode.fieldsToRecurse().get(key);
				if (nbtTreeNode2 != null) {
					this.treeStack.push(nbtTreeNode2);
				}
			}

			return super.startSubNbt(type, key);
		}
	}

	@Override
	public NbtScanner.Result endNested() {
		if (this.getDepth() == ((NbtTreeNode)this.treeStack.element()).depth()) {
			this.treeStack.pop();
		}

		return super.endNested();
	}
}
