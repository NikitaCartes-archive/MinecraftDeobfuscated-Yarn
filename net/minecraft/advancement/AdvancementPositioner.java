/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.advancement.Advancement;
import org.jetbrains.annotations.Nullable;

public class AdvancementPositioner {
    private final Advancement advancement;
    private final AdvancementPositioner parent;
    private final AdvancementPositioner previousSibling;
    private final int childrenSize;
    private final List<AdvancementPositioner> children = Lists.newArrayList();
    private AdvancementPositioner optionalLast;
    private AdvancementPositioner substituteChild;
    private int depth;
    private float row;
    private float relativeRowInSiblings;
    private float field_1266;
    private float field_1265;

    public AdvancementPositioner(Advancement advancement, @Nullable AdvancementPositioner advancementPositioner, @Nullable AdvancementPositioner advancementPositioner2, int i, int j) {
        if (advancement.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position an invisible advancement!");
        }
        this.advancement = advancement;
        this.parent = advancementPositioner;
        this.previousSibling = advancementPositioner2;
        this.childrenSize = i;
        this.optionalLast = this;
        this.depth = j;
        this.row = -1.0f;
        AdvancementPositioner advancementPositioner3 = null;
        for (Advancement advancement2 : advancement.getChildren()) {
            advancementPositioner3 = this.findChildrenRecursively(advancement2, advancementPositioner3);
        }
    }

    @Nullable
    private AdvancementPositioner findChildrenRecursively(Advancement advancement, @Nullable AdvancementPositioner advancementPositioner) {
        if (advancement.getDisplay() != null) {
            advancementPositioner = new AdvancementPositioner(advancement, this, advancementPositioner, this.children.size() + 1, this.depth + 1);
            this.children.add(advancementPositioner);
        } else {
            for (Advancement advancement2 : advancement.getChildren()) {
                advancementPositioner = this.findChildrenRecursively(advancement2, advancementPositioner);
            }
        }
        return advancementPositioner;
    }

    private void calculateRecursively() {
        if (this.children.isEmpty()) {
            this.row = this.previousSibling != null ? this.previousSibling.row + 1.0f : 0.0f;
            return;
        }
        AdvancementPositioner advancementPositioner = null;
        for (AdvancementPositioner advancementPositioner2 : this.children) {
            advancementPositioner2.calculateRecursively();
            advancementPositioner = advancementPositioner2.onFinishCalculation(advancementPositioner == null ? advancementPositioner2 : advancementPositioner);
        }
        this.onFinishChildrenCalculation();
        float f = (this.children.get((int)0).row + this.children.get((int)(this.children.size() - 1)).row) / 2.0f;
        if (this.previousSibling != null) {
            this.row = this.previousSibling.row + 1.0f;
            this.relativeRowInSiblings = this.row - f;
        } else {
            this.row = f;
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
        float f = 0.0f;
        float g = 0.0f;
        for (int i = this.children.size() - 1; i >= 0; --i) {
            AdvancementPositioner advancementPositioner = this.children.get(i);
            advancementPositioner.row += f;
            advancementPositioner.relativeRowInSiblings += f;
            f += advancementPositioner.field_1265 + (g += advancementPositioner.field_1266);
        }
    }

    @Nullable
    private AdvancementPositioner getFirstChild() {
        if (this.substituteChild != null) {
            return this.substituteChild;
        }
        if (!this.children.isEmpty()) {
            return this.children.get(0);
        }
        return null;
    }

    @Nullable
    private AdvancementPositioner getLastChild() {
        if (this.substituteChild != null) {
            return this.substituteChild;
        }
        if (!this.children.isEmpty()) {
            return this.children.get(this.children.size() - 1);
        }
        return null;
    }

    private AdvancementPositioner onFinishCalculation(AdvancementPositioner advancementPositioner) {
        if (this.previousSibling == null) {
            return advancementPositioner;
        }
        AdvancementPositioner advancementPositioner2 = this;
        AdvancementPositioner advancementPositioner3 = this;
        AdvancementPositioner advancementPositioner4 = this.previousSibling;
        AdvancementPositioner advancementPositioner5 = this.parent.children.get(0);
        float f = this.relativeRowInSiblings;
        float g = this.relativeRowInSiblings;
        float h = advancementPositioner4.relativeRowInSiblings;
        float i = advancementPositioner5.relativeRowInSiblings;
        while (advancementPositioner4.getLastChild() != null && advancementPositioner2.getFirstChild() != null) {
            advancementPositioner4 = advancementPositioner4.getLastChild();
            advancementPositioner2 = advancementPositioner2.getFirstChild();
            advancementPositioner5 = advancementPositioner5.getFirstChild();
            advancementPositioner3 = advancementPositioner3.getLastChild();
            advancementPositioner3.optionalLast = this;
            float j = advancementPositioner4.row + h - (advancementPositioner2.row + f) + 1.0f;
            if (j > 0.0f) {
                advancementPositioner4.getLast(this, advancementPositioner).pushDown(this, j);
                f += j;
                g += j;
            }
            h += advancementPositioner4.relativeRowInSiblings;
            f += advancementPositioner2.relativeRowInSiblings;
            i += advancementPositioner5.relativeRowInSiblings;
            g += advancementPositioner3.relativeRowInSiblings;
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

    private void pushDown(AdvancementPositioner advancementPositioner, float f) {
        float g = advancementPositioner.childrenSize - this.childrenSize;
        if (g != 0.0f) {
            advancementPositioner.field_1266 -= f / g;
            this.field_1266 += f / g;
        }
        advancementPositioner.field_1265 += f;
        advancementPositioner.row += f;
        advancementPositioner.relativeRowInSiblings += f;
    }

    private AdvancementPositioner getLast(AdvancementPositioner advancementPositioner, AdvancementPositioner advancementPositioner2) {
        if (this.optionalLast != null && advancementPositioner.parent.children.contains(this.optionalLast)) {
            return this.optionalLast;
        }
        return advancementPositioner2;
    }

    private void apply() {
        if (this.advancement.getDisplay() != null) {
            this.advancement.getDisplay().setPosition(this.depth, this.row);
        }
        if (!this.children.isEmpty()) {
            for (AdvancementPositioner advancementPositioner : this.children) {
                advancementPositioner.apply();
            }
        }
    }

    public static void arrangeForTree(Advancement advancement) {
        if (advancement.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position children of an invisible root!");
        }
        AdvancementPositioner advancementPositioner = new AdvancementPositioner(advancement, null, null, 1, 0);
        advancementPositioner.calculateRecursively();
        float f = advancementPositioner.findMinRowRecursively(0.0f, 0, advancementPositioner.row);
        if (f < 0.0f) {
            advancementPositioner.increaseRowRecursively(-f);
        }
        advancementPositioner.apply();
    }
}

