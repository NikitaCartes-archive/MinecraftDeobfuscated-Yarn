package net.minecraft.advancement;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class AdvancementPositioner {
	private final Advancement field_1263;
	private final AdvancementPositioner parent;
	private final AdvancementPositioner previousSibling;
	private final int childrenSize;
	private final List<AdvancementPositioner> children = Lists.<AdvancementPositioner>newArrayList();
	private AdvancementPositioner optionalLast;
	private AdvancementPositioner substituteChild;
	private int depth;
	private float row;
	private float relativeRowInSiblings;
	private float field_1266;
	private float field_1265;

	public AdvancementPositioner(
		Advancement advancement, @Nullable AdvancementPositioner advancementPositioner, @Nullable AdvancementPositioner advancementPositioner2, int i, int j
	) {
		if (advancement.getDisplay() == null) {
			throw new IllegalArgumentException("Can't position an invisible advancement!");
		} else {
			this.field_1263 = advancement;
			this.parent = advancementPositioner;
			this.previousSibling = advancementPositioner2;
			this.childrenSize = i;
			this.optionalLast = this;
			this.depth = j;
			this.row = -1.0F;
			AdvancementPositioner advancementPositioner3 = null;

			for (Advancement advancement2 : advancement.getChildren()) {
				advancementPositioner3 = this.method_846(advancement2, advancementPositioner3);
			}
		}
	}

	@Nullable
	private AdvancementPositioner method_846(Advancement advancement, @Nullable AdvancementPositioner advancementPositioner) {
		if (advancement.getDisplay() != null) {
			advancementPositioner = new AdvancementPositioner(advancement, this, advancementPositioner, this.children.size() + 1, this.depth + 1);
			this.children.add(advancementPositioner);
		} else {
			for (Advancement advancement2 : advancement.getChildren()) {
				advancementPositioner = this.method_846(advancement2, advancementPositioner);
			}
		}

		return advancementPositioner;
	}

	private void calculateRecursively() {
		if (this.children.isEmpty()) {
			if (this.previousSibling != null) {
				this.row = this.previousSibling.row + 1.0F;
			} else {
				this.row = 0.0F;
			}
		} else {
			AdvancementPositioner advancementPositioner = null;

			for (AdvancementPositioner advancementPositioner2 : this.children) {
				advancementPositioner2.calculateRecursively();
				advancementPositioner = advancementPositioner2.onFinishCalculation(advancementPositioner == null ? advancementPositioner2 : advancementPositioner);
			}

			this.onFinishChildrenCalculation();
			float f = (((AdvancementPositioner)this.children.get(0)).row + ((AdvancementPositioner)this.children.get(this.children.size() - 1)).row) / 2.0F;
			if (this.previousSibling != null) {
				this.row = this.previousSibling.row + 1.0F;
				this.relativeRowInSiblings = this.row - f;
			} else {
				this.row = f;
			}
		}
	}

	private float findMinRowRecursively(float f, int i, float g) {
		this.row += f;
		this.depth = i;
		if (this.row < g) {
			g = this.row;
		}

		for (AdvancementPositioner advancementPositioner : this.children) {
			g = advancementPositioner.findMinRowRecursively(f + this.relativeRowInSiblings, i + 1, g);
		}

		return g;
	}

	private void increaseRowRecursively(float f) {
		this.row += f;

		for (AdvancementPositioner advancementPositioner : this.children) {
			advancementPositioner.increaseRowRecursively(f);
		}
	}

	private void onFinishChildrenCalculation() {
		float f = 0.0F;
		float g = 0.0F;

		for (int i = this.children.size() - 1; i >= 0; i--) {
			AdvancementPositioner advancementPositioner = (AdvancementPositioner)this.children.get(i);
			advancementPositioner.row += f;
			advancementPositioner.relativeRowInSiblings += f;
			g += advancementPositioner.field_1266;
			f += advancementPositioner.field_1265 + g;
		}
	}

	@Nullable
	private AdvancementPositioner getFirstChild() {
		if (this.substituteChild != null) {
			return this.substituteChild;
		} else {
			return !this.children.isEmpty() ? (AdvancementPositioner)this.children.get(0) : null;
		}
	}

	@Nullable
	private AdvancementPositioner getLastChild() {
		if (this.substituteChild != null) {
			return this.substituteChild;
		} else {
			return !this.children.isEmpty() ? (AdvancementPositioner)this.children.get(this.children.size() - 1) : null;
		}
	}

	private AdvancementPositioner onFinishCalculation(AdvancementPositioner advancementPositioner) {
		if (this.previousSibling == null) {
			return advancementPositioner;
		} else {
			AdvancementPositioner advancementPositioner2 = this;
			AdvancementPositioner advancementPositioner3 = this;
			AdvancementPositioner advancementPositioner4 = this.previousSibling;
			AdvancementPositioner advancementPositioner5 = (AdvancementPositioner)this.parent.children.get(0);
			float f = this.relativeRowInSiblings;
			float g = this.relativeRowInSiblings;
			float h = advancementPositioner4.relativeRowInSiblings;

			float i;
			for (i = advancementPositioner5.relativeRowInSiblings;
				advancementPositioner4.getLastChild() != null && advancementPositioner2.getFirstChild() != null;
				g += advancementPositioner3.relativeRowInSiblings
			) {
				advancementPositioner4 = advancementPositioner4.getLastChild();
				advancementPositioner2 = advancementPositioner2.getFirstChild();
				advancementPositioner5 = advancementPositioner5.getFirstChild();
				advancementPositioner3 = advancementPositioner3.getLastChild();
				advancementPositioner3.optionalLast = this;
				float j = advancementPositioner4.row + h - (advancementPositioner2.row + f) + 1.0F;
				if (j > 0.0F) {
					advancementPositioner4.getLast(this, advancementPositioner).pushDown(this, j);
					f += j;
					g += j;
				}

				h += advancementPositioner4.relativeRowInSiblings;
				f += advancementPositioner2.relativeRowInSiblings;
				i += advancementPositioner5.relativeRowInSiblings;
			}

			if (advancementPositioner4.getLastChild() != null && advancementPositioner3.getLastChild() == null) {
				advancementPositioner3.substituteChild = advancementPositioner4.getLastChild();
				advancementPositioner3.relativeRowInSiblings += h - g;
			} else {
				if (advancementPositioner2.getFirstChild() != null && advancementPositioner5.getFirstChild() == null) {
					advancementPositioner5.substituteChild = advancementPositioner2.getFirstChild();
					advancementPositioner5.relativeRowInSiblings += f - i;
				}

				advancementPositioner = this;
			}

			return advancementPositioner;
		}
	}

	private void pushDown(AdvancementPositioner advancementPositioner, float f) {
		float g = (float)(advancementPositioner.childrenSize - this.childrenSize);
		if (g != 0.0F) {
			advancementPositioner.field_1266 -= f / g;
			this.field_1266 += f / g;
		}

		advancementPositioner.field_1265 += f;
		advancementPositioner.row += f;
		advancementPositioner.relativeRowInSiblings += f;
	}

	private AdvancementPositioner getLast(AdvancementPositioner advancementPositioner, AdvancementPositioner advancementPositioner2) {
		return this.optionalLast != null && advancementPositioner.parent.children.contains(this.optionalLast) ? this.optionalLast : advancementPositioner2;
	}

	private void apply() {
		if (this.field_1263.getDisplay() != null) {
			this.field_1263.getDisplay().setPosition((float)this.depth, this.row);
		}

		if (!this.children.isEmpty()) {
			for (AdvancementPositioner advancementPositioner : this.children) {
				advancementPositioner.apply();
			}
		}
	}

	public static void method_852(Advancement advancement) {
		if (advancement.getDisplay() == null) {
			throw new IllegalArgumentException("Can't position children of an invisible root!");
		} else {
			AdvancementPositioner advancementPositioner = new AdvancementPositioner(advancement, null, null, 1, 0);
			advancementPositioner.calculateRecursively();
			float f = advancementPositioner.findMinRowRecursively(0.0F, 0, advancementPositioner.row);
			if (f < 0.0F) {
				advancementPositioner.increaseRowRecursively(-f);
			}

			advancementPositioner.apply();
		}
	}
}
