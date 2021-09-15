package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;

public class class_6582 implements class_6583 {
	private final List<class_6583> field_34719;

	public class_6582(List<class_6583> list) {
		this.field_34719 = list;
	}

	@Nullable
	@Override
	public BlockState apply(class_6568 arg, int i, int j, int k) {
		for (class_6583 lv : this.field_34719) {
			BlockState blockState = lv.apply(arg, i, j, k);
			if (blockState != null) {
				return blockState;
			}
		}

		return null;
	}
}
