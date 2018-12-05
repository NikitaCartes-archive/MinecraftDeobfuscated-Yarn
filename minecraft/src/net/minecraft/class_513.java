package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.container.RecipeBookGui;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;

@Environment(EnvType.CLIENT)
public class class_513 {
	private final List<class_514> field_3131 = Lists.<class_514>newArrayListWithCapacity(20);
	private class_514 field_3129;
	private final class_508 field_3132 = new class_508();
	private MinecraftClient field_3126;
	private final List<class_515> field_3134 = Lists.<class_515>newArrayList();
	private List<class_516> field_3127;
	private class_361 field_3128;
	private class_361 field_3130;
	private int field_3124;
	private int field_3135;
	private RecipeBook field_3136;
	private Recipe field_3125;
	private class_516 field_3133;

	public class_513() {
		for (int i = 0; i < 20; i++) {
			this.field_3131.add(new class_514());
		}
	}

	public void method_2636(MinecraftClient minecraftClient, int i, int j) {
		this.field_3126 = minecraftClient;
		this.field_3136 = minecraftClient.player.getRecipeBook();

		for (int k = 0; k < this.field_3131.size(); k++) {
			((class_514)this.field_3131.get(k)).method_2641(i + 11 + 25 * (k % 5), j + 31 + 25 * (k / 5));
		}

		this.field_3128 = new class_361(0, i + 93, j + 137, 12, 17, false);
		this.field_3128.method_1962(1, 208, 13, 18, RecipeBookGui.TEXTURE);
		this.field_3130 = new class_361(0, i + 38, j + 137, 12, 17, true);
		this.field_3130.method_1962(1, 208, 13, 18, RecipeBookGui.TEXTURE);
	}

	public void method_2630(RecipeBookGui recipeBookGui) {
		this.field_3134.remove(recipeBookGui);
		this.field_3134.add(recipeBookGui);
	}

	public void method_2627(List<class_516> list, boolean bl) {
		this.field_3127 = list;
		this.field_3124 = (int)Math.ceil((double)list.size() / 20.0);
		if (this.field_3124 <= this.field_3135 || bl) {
			this.field_3135 = 0;
		}

		this.method_2625();
	}

	private void method_2625() {
		int i = 20 * this.field_3135;

		for (int j = 0; j < this.field_3131.size(); j++) {
			class_514 lv = (class_514)this.field_3131.get(j);
			if (i + j < this.field_3127.size()) {
				class_516 lv2 = (class_516)this.field_3127.get(i + j);
				lv.method_2640(lv2, this);
				lv.visible = true;
			} else {
				lv.visible = false;
			}
		}

		this.method_2626();
	}

	private void method_2626() {
		this.field_3128.visible = this.field_3124 > 1 && this.field_3135 < this.field_3124 - 1;
		this.field_3130.visible = this.field_3124 > 1 && this.field_3135 > 0;
	}

	public void method_2634(int i, int j, int k, int l, float f) {
		if (this.field_3124 > 1) {
			String string = this.field_3135 + 1 + "/" + this.field_3124;
			int m = this.field_3126.fontRenderer.getStringWidth(string);
			this.field_3126.fontRenderer.draw(string, (float)(i - m / 2 + 73), (float)(j + 141), -1);
		}

		class_308.method_1450();
		this.field_3129 = null;

		for (class_514 lv : this.field_3131) {
			lv.draw(k, l, f);
			if (lv.visible && lv.isHovered()) {
				this.field_3129 = lv;
			}
		}

		this.field_3130.draw(k, l, f);
		this.field_3128.draw(k, l, f);
		this.field_3132.method_2612(k, l, f);
	}

	public void method_2628(int i, int j) {
		if (this.field_3126.currentGui != null && this.field_3129 != null && !this.field_3132.method_2616()) {
			this.field_3126.currentGui.drawTooltip(this.field_3129.method_2644(this.field_3126.currentGui), i, j);
		}
	}

	@Nullable
	public Recipe method_2631() {
		return this.field_3125;
	}

	@Nullable
	public class_516 method_2635() {
		return this.field_3133;
	}

	public void method_2638() {
		this.field_3132.method_2613(false);
	}

	public boolean method_2632(double d, double e, int i, int j, int k, int l, int m) {
		this.field_3125 = null;
		this.field_3133 = null;
		if (this.field_3132.method_2616()) {
			if (this.field_3132.mouseClicked(d, e, i)) {
				this.field_3125 = this.field_3132.method_2615();
				this.field_3133 = this.field_3132.method_2614();
			} else {
				this.field_3132.method_2613(false);
			}

			return true;
		} else if (this.field_3128.mouseClicked(d, e, i)) {
			this.field_3135++;
			this.method_2625();
			return true;
		} else if (this.field_3130.mouseClicked(d, e, i)) {
			this.field_3135--;
			this.method_2625();
			return true;
		} else {
			for (class_514 lv : this.field_3131) {
				if (lv.mouseClicked(d, e, i)) {
					if (i == 0) {
						this.field_3125 = lv.method_2643();
						this.field_3133 = lv.method_2645();
					} else if (i == 1 && !this.field_3132.method_2616() && !lv.method_2642()) {
						this.field_3132.method_2617(this.field_3126, lv.method_2645(), lv.x, lv.y, j + l / 2, k + 13 + m / 2, (float)lv.getWidth());
					}

					return true;
				}
			}

			return false;
		}
	}

	public void method_2629(List<Recipe> list) {
		for (class_515 lv : this.field_3134) {
			lv.method_2646(list);
		}
	}

	public MinecraftClient method_2637() {
		return this.field_3126;
	}

	public RecipeBook method_2633() {
		return this.field_3136;
	}
}
