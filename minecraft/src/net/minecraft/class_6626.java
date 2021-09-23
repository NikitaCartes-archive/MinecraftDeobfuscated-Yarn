package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.util.math.BlockBox;

public class class_6626 implements StructurePiecesHolder {
	private final List<StructurePiece> field_34944 = Lists.<StructurePiece>newArrayList();

	@Override
	public void addPiece(StructurePiece piece) {
		this.field_34944.add(piece);
	}

	public void method_38717(Collection<StructurePiece> collection) {
		this.field_34944.addAll(collection);
	}

	@Nullable
	@Override
	public StructurePiece getIntersecting(BlockBox box) {
		return StructurePiece.method_38702(this.field_34944, box);
	}

	@Deprecated
	public void method_38715(int i) {
		for (StructurePiece structurePiece : this.field_34944) {
			structurePiece.translate(0, i, 0);
		}
	}

	@Deprecated
	public void method_38716(int i, int j, Random random, int k) {
		int l = i - k;
		BlockBox blockBox = this.method_38721();
		int m = blockBox.getBlockCountY() + j + 1;
		if (m < l) {
			m += random.nextInt(l - m);
		}

		int n = m - blockBox.getMaxY();
		this.method_38715(n);
	}

	public void method_38718(Random random, int i, int j) {
		BlockBox blockBox = this.method_38721();
		int k = j - i + 1 - blockBox.getBlockCountY();
		int l;
		if (k > 1) {
			l = i + random.nextInt(k);
		} else {
			l = i;
		}

		int m = l - blockBox.getMinY();
		this.method_38715(m);
	}

	public class_6624 method_38714() {
		return new class_6624(this.field_34944);
	}

	public void method_38719() {
		this.field_34944.clear();
	}

	public boolean method_38720() {
		return this.field_34944.isEmpty();
	}

	public BlockBox method_38721() {
		return StructurePiece.method_38703(this.field_34944.stream());
	}
}
