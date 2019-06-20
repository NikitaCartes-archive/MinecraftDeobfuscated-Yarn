package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_350<E extends class_350.class_351<E>> extends class_362 implements class_4068 {
	protected static final int DRAG_OUTSIDE = -2;
	protected final class_310 minecraft;
	protected final int itemHeight;
	private final List<E> children = new class_350.class_352();
	protected int width;
	protected int height;
	protected int field_19085;
	protected int field_19086;
	protected int field_19087;
	protected int field_19088;
	protected boolean centerListVertically = true;
	protected int yDrag = -2;
	private double scrollAmount;
	protected boolean renderSelection = true;
	protected boolean renderHeader;
	protected int headerHeight;
	private boolean scrolling;
	private E selected;

	public class_350(class_310 arg, int i, int j, int k, int l, int m) {
		this.minecraft = arg;
		this.width = i;
		this.height = j;
		this.field_19085 = k;
		this.field_19086 = l;
		this.itemHeight = m;
		this.field_19088 = 0;
		this.field_19087 = i;
	}

	public void setRenderSelection(boolean bl) {
		this.renderSelection = bl;
	}

	protected void setRenderHeader(boolean bl, int i) {
		this.renderHeader = bl;
		this.headerHeight = i;
		if (!bl) {
			this.headerHeight = 0;
		}
	}

	public int getRowWidth() {
		return 220;
	}

	@Nullable
	public E getSelected() {
		return this.selected;
	}

	public void setSelected(@Nullable E arg) {
		this.selected = arg;
	}

	@Nullable
	public E getFocused() {
		return (E)super.getFocused();
	}

	@Override
	public final List<E> children() {
		return this.children;
	}

	protected final void clearEntries() {
		this.children.clear();
	}

	protected void replaceEntries(Collection<E> collection) {
		this.children.clear();
		this.children.addAll(collection);
	}

	protected E getEntry(int i) {
		return (E)this.children().get(i);
	}

	protected int addEntry(E arg) {
		this.children.add(arg);
		return this.children.size() - 1;
	}

	protected int getItemCount() {
		return this.children().size();
	}

	protected boolean isSelectedItem(int i) {
		return Objects.equals(this.getSelected(), this.children().get(i));
	}

	@Nullable
	protected final E getEntryAtPosition(double d, double e) {
		int i = this.getRowWidth() / 2;
		int j = this.field_19088 + this.width / 2;
		int k = j - i;
		int l = j + i;
		int m = class_3532.method_15357(e - (double)this.field_19085) - this.headerHeight + (int)this.getScrollAmount() - 4;
		int n = m / this.itemHeight;
		return (E)(d < (double)this.getScrollbarPosition() && d >= (double)k && d <= (double)l && n >= 0 && m >= 0 && n < this.getItemCount()
			? this.children().get(n)
			: null);
	}

	public void updateSize(int i, int j, int k, int l) {
		this.width = i;
		this.height = j;
		this.field_19085 = k;
		this.field_19086 = l;
		this.field_19088 = 0;
		this.field_19087 = i;
	}

	public void setLeftPos(int i) {
		this.field_19088 = i;
		this.field_19087 = i + this.width;
	}

	protected int getMaxPosition() {
		return this.getItemCount() * this.itemHeight + this.headerHeight;
	}

	protected void clickedHeader(int i, int j) {
	}

	protected void renderHeader(int i, int j, class_289 arg) {
	}

	protected void renderBackground() {
	}

	protected void renderDecorations(int i, int j) {
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		int k = this.getScrollbarPosition();
		int l = k + 6;
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		this.minecraft.method_1531().method_4618(class_332.BACKGROUND_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float g = 32.0F;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315((double)this.field_19088, (double)this.field_19086, 0.0)
			.method_1312((double)((float)this.field_19088 / 32.0F), (double)((float)(this.field_19086 + (int)this.getScrollAmount()) / 32.0F))
			.method_1323(32, 32, 32, 255)
			.method_1344();
		lv2.method_1315((double)this.field_19087, (double)this.field_19086, 0.0)
			.method_1312((double)((float)this.field_19087 / 32.0F), (double)((float)(this.field_19086 + (int)this.getScrollAmount()) / 32.0F))
			.method_1323(32, 32, 32, 255)
			.method_1344();
		lv2.method_1315((double)this.field_19087, (double)this.field_19085, 0.0)
			.method_1312((double)((float)this.field_19087 / 32.0F), (double)((float)(this.field_19085 + (int)this.getScrollAmount()) / 32.0F))
			.method_1323(32, 32, 32, 255)
			.method_1344();
		lv2.method_1315((double)this.field_19088, (double)this.field_19085, 0.0)
			.method_1312((double)((float)this.field_19088 / 32.0F), (double)((float)(this.field_19085 + (int)this.getScrollAmount()) / 32.0F))
			.method_1323(32, 32, 32, 255)
			.method_1344();
		lv.method_1350();
		int m = this.getRowLeft();
		int n = this.field_19085 + 4 - (int)this.getScrollAmount();
		if (this.renderHeader) {
			this.renderHeader(m, n, lv);
		}

		this.renderList(m, n, i, j, f);
		GlStateManager.disableDepthTest();
		this.renderHoleBackground(0, this.field_19085, 255, 255);
		this.renderHoleBackground(this.field_19086, this.height, 255, 255);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ZERO, GlStateManager.class_5118.ONE
		);
		GlStateManager.disableAlphaTest();
		GlStateManager.shadeModel(7425);
		GlStateManager.disableTexture();
		int o = 4;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315((double)this.field_19088, (double)(this.field_19085 + 4), 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 0).method_1344();
		lv2.method_1315((double)this.field_19087, (double)(this.field_19085 + 4), 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 0).method_1344();
		lv2.method_1315((double)this.field_19087, (double)this.field_19085, 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
		lv2.method_1315((double)this.field_19088, (double)this.field_19085, 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
		lv.method_1350();
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315((double)this.field_19088, (double)this.field_19086, 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
		lv2.method_1315((double)this.field_19087, (double)this.field_19086, 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
		lv2.method_1315((double)this.field_19087, (double)(this.field_19086 - 4), 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 0).method_1344();
		lv2.method_1315((double)this.field_19088, (double)(this.field_19086 - 4), 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 0).method_1344();
		lv.method_1350();
		int p = this.getMaxScroll();
		if (p > 0) {
			int q = (int)((float)((this.field_19086 - this.field_19085) * (this.field_19086 - this.field_19085)) / (float)this.getMaxPosition());
			q = class_3532.method_15340(q, 32, this.field_19086 - this.field_19085 - 8);
			int r = (int)this.getScrollAmount() * (this.field_19086 - this.field_19085 - q) / p + this.field_19085;
			if (r < this.field_19085) {
				r = this.field_19085;
			}

			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)k, (double)this.field_19086, 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)l, (double)this.field_19086, 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)l, (double)this.field_19085, 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)k, (double)this.field_19085, 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
			lv.method_1350();
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)k, (double)(r + q), 0.0).method_1312(0.0, 1.0).method_1323(128, 128, 128, 255).method_1344();
			lv2.method_1315((double)l, (double)(r + q), 0.0).method_1312(1.0, 1.0).method_1323(128, 128, 128, 255).method_1344();
			lv2.method_1315((double)l, (double)r, 0.0).method_1312(1.0, 0.0).method_1323(128, 128, 128, 255).method_1344();
			lv2.method_1315((double)k, (double)r, 0.0).method_1312(0.0, 0.0).method_1323(128, 128, 128, 255).method_1344();
			lv.method_1350();
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)k, (double)(r + q - 1), 0.0).method_1312(0.0, 1.0).method_1323(192, 192, 192, 255).method_1344();
			lv2.method_1315((double)(l - 1), (double)(r + q - 1), 0.0).method_1312(1.0, 1.0).method_1323(192, 192, 192, 255).method_1344();
			lv2.method_1315((double)(l - 1), (double)r, 0.0).method_1312(1.0, 0.0).method_1323(192, 192, 192, 255).method_1344();
			lv2.method_1315((double)k, (double)r, 0.0).method_1312(0.0, 0.0).method_1323(192, 192, 192, 255).method_1344();
			lv.method_1350();
		}

		this.renderDecorations(i, j);
		GlStateManager.enableTexture();
		GlStateManager.shadeModel(7424);
		GlStateManager.enableAlphaTest();
		GlStateManager.disableBlend();
	}

	protected void centerScrollOn(E arg) {
		this.setScrollAmount((double)(this.children().indexOf(arg) * this.itemHeight + this.itemHeight / 2 - (this.field_19086 - this.field_19085) / 2));
	}

	protected void ensureVisible(E arg) {
		int i = this.getRowTop(this.children().indexOf(arg));
		int j = i - this.field_19085 - 4 - this.itemHeight;
		if (j < 0) {
			this.scroll(j);
		}

		int k = this.field_19086 - i - this.itemHeight - this.itemHeight;
		if (k < 0) {
			this.scroll(-k);
		}
	}

	private void scroll(int i) {
		this.setScrollAmount(this.getScrollAmount() + (double)i);
		this.yDrag = -2;
	}

	public double getScrollAmount() {
		return this.scrollAmount;
	}

	public void setScrollAmount(double d) {
		this.scrollAmount = class_3532.method_15350(d, 0.0, (double)this.getMaxScroll());
	}

	private int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.field_19086 - this.field_19085 - 4));
	}

	public int getScrollBottom() {
		return (int)this.getScrollAmount() - this.height - this.headerHeight;
	}

	protected void updateScrollingState(double d, double e, int i) {
		this.scrolling = i == 0 && d >= (double)this.getScrollbarPosition() && d < (double)(this.getScrollbarPosition() + 6);
	}

	protected int getScrollbarPosition() {
		return this.width / 2 + 124;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.updateScrollingState(d, e, i);
		if (!this.isMouseOver(d, e)) {
			return false;
		} else {
			E lv = this.getEntryAtPosition(d, e);
			if (lv != null) {
				if (lv.mouseClicked(d, e, i)) {
					this.setFocused(lv);
					this.setDragging(true);
					return true;
				}
			} else if (i == 0) {
				this.clickedHeader(
					(int)(d - (double)(this.field_19088 + this.width / 2 - this.getRowWidth() / 2)), (int)(e - (double)this.field_19085) + (int)this.getScrollAmount() - 4
				);
				return true;
			}

			return this.scrolling;
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (this.getFocused() != null) {
			this.getFocused().mouseReleased(d, e, i);
		}

		return false;
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (super.mouseDragged(d, e, i, f, g)) {
			return true;
		} else if (i == 0 && this.scrolling) {
			if (e < (double)this.field_19085) {
				this.setScrollAmount(0.0);
			} else if (e > (double)this.field_19086) {
				this.setScrollAmount((double)this.getMaxScroll());
			} else {
				double h = (double)Math.max(1, this.getMaxScroll());
				int j = this.field_19086 - this.field_19085;
				int k = class_3532.method_15340((int)((float)(j * j) / (float)this.getMaxPosition()), 32, j - 8);
				double l = Math.max(1.0, h / (double)(j - k));
				this.setScrollAmount(this.getScrollAmount() + g * l);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		this.setScrollAmount(this.getScrollAmount() - f * (double)this.itemHeight / 2.0);
		return true;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i == 264) {
			this.moveSelection(1);
			return true;
		} else if (i == 265) {
			this.moveSelection(-1);
			return true;
		} else {
			return false;
		}
	}

	protected void moveSelection(int i) {
		if (!this.children().isEmpty()) {
			int j = this.children().indexOf(this.getSelected());
			int k = class_3532.method_15340(j + i, 0, this.getItemCount() - 1);
			E lv = (E)this.children().get(k);
			this.setSelected(lv);
			this.ensureVisible(lv);
		}
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return e >= (double)this.field_19085 && e <= (double)this.field_19086 && d >= (double)this.field_19088 && d <= (double)this.field_19087;
	}

	protected void renderList(int i, int j, int k, int l, float f) {
		int m = this.getItemCount();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();

		for (int n = 0; n < m; n++) {
			int o = this.getRowTop(n);
			int p = this.getRowBottom(n);
			if (p >= this.field_19085 && o <= this.field_19086) {
				int q = j + n * this.itemHeight + this.headerHeight;
				int r = this.itemHeight - 4;
				E lv3 = this.getEntry(n);
				int s = this.getRowWidth();
				if (this.renderSelection && this.isSelectedItem(n)) {
					int t = this.field_19088 + this.width / 2 - s / 2;
					int u = this.field_19088 + this.width / 2 + s / 2;
					GlStateManager.disableTexture();
					float g = this.isFocused() ? 1.0F : 0.5F;
					GlStateManager.color4f(g, g, g, 1.0F);
					lv2.method_1328(7, class_290.field_1592);
					lv2.method_1315((double)t, (double)(q + r + 2), 0.0).method_1344();
					lv2.method_1315((double)u, (double)(q + r + 2), 0.0).method_1344();
					lv2.method_1315((double)u, (double)(q - 2), 0.0).method_1344();
					lv2.method_1315((double)t, (double)(q - 2), 0.0).method_1344();
					lv.method_1350();
					GlStateManager.color4f(0.0F, 0.0F, 0.0F, 1.0F);
					lv2.method_1328(7, class_290.field_1592);
					lv2.method_1315((double)(t + 1), (double)(q + r + 1), 0.0).method_1344();
					lv2.method_1315((double)(u - 1), (double)(q + r + 1), 0.0).method_1344();
					lv2.method_1315((double)(u - 1), (double)(q - 1), 0.0).method_1344();
					lv2.method_1315((double)(t + 1), (double)(q - 1), 0.0).method_1344();
					lv.method_1350();
					GlStateManager.enableTexture();
				}

				int t = this.getRowLeft();
				lv3.render(n, o, t, s, r, k, l, this.isMouseOver((double)k, (double)l) && Objects.equals(this.getEntryAtPosition((double)k, (double)l), lv3), f);
			}
		}
	}

	protected int getRowLeft() {
		return this.field_19088 + this.width / 2 - this.getRowWidth() / 2 + 2;
	}

	protected int getRowTop(int i) {
		return this.field_19085 + 4 - (int)this.getScrollAmount() + i * this.itemHeight + this.headerHeight;
	}

	private int getRowBottom(int i) {
		return this.getRowTop(i) + this.itemHeight;
	}

	protected boolean isFocused() {
		return false;
	}

	protected void renderHoleBackground(int i, int j, int k, int l) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		this.minecraft.method_1531().method_4618(class_332.BACKGROUND_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315((double)this.field_19088, (double)j, 0.0).method_1312(0.0, (double)((float)j / 32.0F)).method_1323(64, 64, 64, l).method_1344();
		lv2.method_1315((double)(this.field_19088 + this.width), (double)j, 0.0)
			.method_1312((double)((float)this.width / 32.0F), (double)((float)j / 32.0F))
			.method_1323(64, 64, 64, l)
			.method_1344();
		lv2.method_1315((double)(this.field_19088 + this.width), (double)i, 0.0)
			.method_1312((double)((float)this.width / 32.0F), (double)((float)i / 32.0F))
			.method_1323(64, 64, 64, k)
			.method_1344();
		lv2.method_1315((double)this.field_19088, (double)i, 0.0).method_1312(0.0, (double)((float)i / 32.0F)).method_1323(64, 64, 64, k).method_1344();
		lv.method_1350();
	}

	protected E remove(int i) {
		E lv = (E)this.children.get(i);
		return this.removeEntry((E)this.children.get(i)) ? lv : null;
	}

	protected boolean removeEntry(E arg) {
		boolean bl = this.children.remove(arg);
		if (bl && arg == this.getSelected()) {
			this.setSelected(null);
		}

		return bl;
	}

	@Environment(EnvType.CLIENT)
	public abstract static class class_351<E extends class_350.class_351<E>> implements class_364 {
		@Deprecated
		class_350<E> list;

		public abstract void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f);

		@Override
		public boolean isMouseOver(double d, double e) {
			return Objects.equals(this.list.getEntryAtPosition(d, e), this);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_352 extends AbstractList<E> {
		private final List<E> field_2146 = Lists.<E>newArrayList();

		private class_352() {
		}

		public E method_1912(int i) {
			return (E)this.field_2146.get(i);
		}

		public int size() {
			return this.field_2146.size();
		}

		public E method_1909(int i, E arg) {
			E lv = (E)this.field_2146.set(i, arg);
			arg.list = class_350.this;
			return lv;
		}

		public void method_1910(int i, E arg) {
			this.field_2146.add(i, arg);
			arg.list = class_350.this;
		}

		public E method_1911(int i) {
			return (E)this.field_2146.remove(i);
		}
	}
}
