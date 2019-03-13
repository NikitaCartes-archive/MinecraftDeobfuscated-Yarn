package net.minecraft.advancement;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class AdvancementPosition {
	private final SimpleAdvancement field_1263;
	private final AdvancementPosition parent;
	private final AdvancementPosition previousSibling;
	private final int childrenSize;
	private final List<AdvancementPosition> children = Lists.<AdvancementPosition>newArrayList();
	private AdvancementPosition optionalLast;
	private AdvancementPosition substituteChild;
	private int depth;
	private float row;
	private float relativeRowInSiblings;
	private float field_1266;
	private float field_1265;

	public AdvancementPosition(
		SimpleAdvancement simpleAdvancement, @Nullable AdvancementPosition advancementPosition, @Nullable AdvancementPosition advancementPosition2, int i, int j
	) {
		if (simpleAdvancement.method_686() == null) {
			throw new IllegalArgumentException("Can't position an invisible advancement!");
		} else {
			this.field_1263 = simpleAdvancement;
			this.parent = advancementPosition;
			this.previousSibling = advancementPosition2;
			this.childrenSize = i;
			this.optionalLast = this;
			this.depth = j;
			this.row = -1.0F;
			AdvancementPosition advancementPosition3 = null;

			for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
				advancementPosition3 = this.method_846(simpleAdvancement2, advancementPosition3);
			}
		}
	}

	@Nullable
	private AdvancementPosition method_846(SimpleAdvancement simpleAdvancement, @Nullable AdvancementPosition advancementPosition) {
		if (simpleAdvancement.method_686() != null) {
			advancementPosition = new AdvancementPosition(simpleAdvancement, this, advancementPosition, this.children.size() + 1, this.depth + 1);
			this.children.add(advancementPosition);
		} else {
			for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
				advancementPosition = this.method_846(simpleAdvancement2, advancementPosition);
			}
		}

		return advancementPosition;
	}

	private void initRecursively() {
		if (this.children.isEmpty()) {
			if (this.previousSibling != null) {
				this.row = this.previousSibling.row + 1.0F;
			} else {
				this.row = 0.0F;
			}
		} else {
			AdvancementPosition advancementPosition = null;

			for (AdvancementPosition advancementPosition2 : this.children) {
				advancementPosition2.initRecursively();
				advancementPosition = advancementPosition2.postInit(advancementPosition == null ? advancementPosition2 : advancementPosition);
			}

			this.method_850();
			float f = (((AdvancementPosition)this.children.get(0)).row + ((AdvancementPosition)this.children.get(this.children.size() - 1)).row) / 2.0F;
			if (this.previousSibling != null) {
				this.row = this.previousSibling.row + 1.0F;
				this.relativeRowInSiblings = this.row - f;
			} else {
				this.row = f;
			}
		}
	}

	private float positMinRow(float f, int i, float g) {
		this.row += f;
		this.depth = i;
		if (this.row < g) {
			g = this.row;
		}

		for (AdvancementPosition advancementPosition : this.children) {
			g = advancementPosition.positMinRow(f + this.relativeRowInSiblings, i + 1, g);
		}

		return g;
	}

	private void increaseRow(float f) {
		this.row += f;

		for (AdvancementPosition advancementPosition : this.children) {
			advancementPosition.increaseRow(f);
		}
	}

	private void method_850() {
		float f = 0.0F;
		float g = 0.0F;

		for (int i = this.children.size() - 1; i >= 0; i--) {
			AdvancementPosition advancementPosition = (AdvancementPosition)this.children.get(i);
			advancementPosition.row += f;
			advancementPosition.relativeRowInSiblings += f;
			g += advancementPosition.field_1266;
			f += advancementPosition.field_1265 + g;
		}
	}

	@Nullable
	private AdvancementPosition getFirstChild() {
		if (this.substituteChild != null) {
			return this.substituteChild;
		} else {
			return !this.children.isEmpty() ? (AdvancementPosition)this.children.get(0) : null;
		}
	}

	@Nullable
	private AdvancementPosition getLastChild() {
		if (this.substituteChild != null) {
			return this.substituteChild;
		} else {
			return !this.children.isEmpty() ? (AdvancementPosition)this.children.get(this.children.size() - 1) : null;
		}
	}

	private AdvancementPosition postInit(AdvancementPosition advancementPosition) {
		if (this.previousSibling == null) {
			return advancementPosition;
		} else {
			AdvancementPosition advancementPosition2 = this;
			AdvancementPosition advancementPosition3 = this;
			AdvancementPosition advancementPosition4 = this.previousSibling;
			AdvancementPosition advancementPosition5 = (AdvancementPosition)this.parent.children.get(0);
			float f = this.relativeRowInSiblings;
			float g = this.relativeRowInSiblings;
			float h = advancementPosition4.relativeRowInSiblings;

			float i;
			for (i = advancementPosition5.relativeRowInSiblings;
				advancementPosition4.getLastChild() != null && advancementPosition2.getFirstChild() != null;
				g += advancementPosition3.relativeRowInSiblings
			) {
				advancementPosition4 = advancementPosition4.getLastChild();
				advancementPosition2 = advancementPosition2.getFirstChild();
				advancementPosition5 = advancementPosition5.getFirstChild();
				advancementPosition3 = advancementPosition3.getLastChild();
				advancementPosition3.optionalLast = this;
				float j = advancementPosition4.row + h - (advancementPosition2.row + f) + 1.0F;
				if (j > 0.0F) {
					advancementPosition4.getLast(this, advancementPosition).method_848(this, j);
					f += j;
					g += j;
				}

				h += advancementPosition4.relativeRowInSiblings;
				f += advancementPosition2.relativeRowInSiblings;
				i += advancementPosition5.relativeRowInSiblings;
			}

			if (advancementPosition4.getLastChild() != null && advancementPosition3.getLastChild() == null) {
				advancementPosition3.substituteChild = advancementPosition4.getLastChild();
				advancementPosition3.relativeRowInSiblings += h - g;
			} else {
				if (advancementPosition2.getFirstChild() != null && advancementPosition5.getFirstChild() == null) {
					advancementPosition5.substituteChild = advancementPosition2.getFirstChild();
					advancementPosition5.relativeRowInSiblings += f - i;
				}

				advancementPosition = this;
			}

			return advancementPosition;
		}
	}

	private void method_848(AdvancementPosition advancementPosition, float f) {
		float g = (float)(advancementPosition.childrenSize - this.childrenSize);
		if (g != 0.0F) {
			advancementPosition.field_1266 -= f / g;
			this.field_1266 += f / g;
		}

		advancementPosition.field_1265 += f;
		advancementPosition.row += f;
		advancementPosition.relativeRowInSiblings += f;
	}

	private AdvancementPosition getLast(AdvancementPosition advancementPosition, AdvancementPosition advancementPosition2) {
		return this.optionalLast != null && advancementPosition.parent.children.contains(this.optionalLast) ? this.optionalLast : advancementPosition2;
	}

	private void apply() {
		if (this.field_1263.method_686() != null) {
			this.field_1263.method_686().setPosition((float)this.depth, this.row);
		}

		if (!this.children.isEmpty()) {
			for (AdvancementPosition advancementPosition : this.children) {
				advancementPosition.apply();
			}
		}
	}

	public static void method_852(SimpleAdvancement simpleAdvancement) {
		if (simpleAdvancement.method_686() == null) {
			throw new IllegalArgumentException("Can't position children of an invisible root!");
		} else {
			AdvancementPosition advancementPosition = new AdvancementPosition(simpleAdvancement, null, null, 1, 0);
			advancementPosition.initRecursively();
			float f = advancementPosition.positMinRow(0.0F, 0, advancementPosition.row);
			if (f < 0.0F) {
				advancementPosition.increaseRow(-f);
			}

			advancementPosition.apply();
		}
	}
}
