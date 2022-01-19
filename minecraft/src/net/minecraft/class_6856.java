package net.minecraft;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtCollector;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.scanner.Query;
import net.minecraft.nbt.scanner.Tree;

public class class_6856 extends NbtCollector {
	private final Deque<Tree> field_36378 = new ArrayDeque();

	public class_6856(Query... querys) {
		Tree tree = Tree.method_40060();

		for (Query query : querys) {
			tree.add(query);
		}

		this.field_36378.push(tree);
	}

	@Override
	public NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key) {
		Tree tree = (Tree)this.field_36378.element();
		if (tree.method_40061(type, key)) {
			return NbtScanner.NestedResult.SKIP;
		} else {
			if (type == NbtCompound.TYPE) {
				Tree tree2 = (Tree)tree.fieldsToRecurse().get(key);
				if (tree2 != null) {
					this.field_36378.push(tree2);
				}
			}

			return super.startSubNbt(type, key);
		}
	}

	@Override
	public NbtScanner.Result endNested() {
		if (this.getDepth() == ((Tree)this.field_36378.element()).depth()) {
			this.field_36378.pop();
		}

		return super.endNested();
	}
}
