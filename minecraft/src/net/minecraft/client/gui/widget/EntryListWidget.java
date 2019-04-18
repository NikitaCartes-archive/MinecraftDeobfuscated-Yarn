package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class EntryListWidget<E extends EntryListWidget.Entry<E>> extends AbstractParentElement implements Drawable {
	protected static final int DRAG_OUTSIDE = -2;
	protected final MinecraftClient minecraft;
	protected final int itemHeight;
	private final List<E> children = new EntryListWidget.Entries();
	protected int width;
	protected int height;
	protected int top;
	protected int bottom;
	protected int right;
	protected int left;
	protected boolean centerListVertically = true;
	protected int yDrag = -2;
	private double scrollAmount;
	protected boolean renderSelection = true;
	protected boolean renderHeader;
	protected int headerHeight;
	private boolean scrolling;
	private E selected;

	public EntryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		this.minecraft = minecraftClient;
		this.width = i;
		this.height = j;
		this.top = k;
		this.bottom = l;
		this.itemHeight = m;
		this.left = 0;
		this.right = i;
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

	public void setSelected(@Nullable E entry) {
		this.selected = entry;
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

	protected int addEntry(E entry) {
		this.children.add(entry);
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
		int j = this.left + this.width / 2;
		int k = j - i;
		int l = j + i;
		int m = MathHelper.floor(e - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
		int n = m / this.itemHeight;
		return (E)(d < (double)this.getScrollbarPosition() && d >= (double)k && d <= (double)l && n >= 0 && m >= 0 && n < this.getItemCount()
			? this.children().get(n)
			: null);
	}

	public void updateSize(int i, int j, int k, int l) {
		this.width = i;
		this.height = j;
		this.top = k;
		this.bottom = l;
		this.left = 0;
		this.right = i;
	}

	public void setLeftPos(int i) {
		this.left = i;
		this.right = i + this.width;
	}

	protected int getMaxPosition() {
		return this.getItemCount() * this.itemHeight + this.headerHeight;
	}

	protected void clickedHeader(int i, int j) {
	}

	protected void renderHeader(int i, int j, Tessellator tessellator) {
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
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float g = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex((double)this.left, (double)this.bottom, 0.0)
			.texture((double)((float)this.left / 32.0F), (double)((float)(this.bottom + (int)this.getScrollAmount()) / 32.0F))
			.color(32, 32, 32, 255)
			.next();
		bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0)
			.texture((double)((float)this.right / 32.0F), (double)((float)(this.bottom + (int)this.getScrollAmount()) / 32.0F))
			.color(32, 32, 32, 255)
			.next();
		bufferBuilder.vertex((double)this.right, (double)this.top, 0.0)
			.texture((double)((float)this.right / 32.0F), (double)((float)(this.top + (int)this.getScrollAmount()) / 32.0F))
			.color(32, 32, 32, 255)
			.next();
		bufferBuilder.vertex((double)this.left, (double)this.top, 0.0)
			.texture((double)((float)this.left / 32.0F), (double)((float)(this.top + (int)this.getScrollAmount()) / 32.0F))
			.color(32, 32, 32, 255)
			.next();
		tessellator.draw();
		int m = this.getRowLeft();
		int n = this.top + 4 - (int)this.getScrollAmount();
		if (this.renderHeader) {
			this.renderHeader(m, n, tessellator);
		}

		this.renderList(m, n, i, j, f);
		GlStateManager.disableDepthTest();
		this.renderHoleBackground(0, this.top, 255, 255);
		this.renderHoleBackground(this.bottom, this.height, 255, 255);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE
		);
		GlStateManager.disableAlphaTest();
		GlStateManager.shadeModel(7425);
		GlStateManager.disableTexture();
		int o = 4;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex((double)this.left, (double)(this.top + 4), 0.0).texture(0.0, 1.0).color(0, 0, 0, 0).next();
		bufferBuilder.vertex((double)this.right, (double)(this.top + 4), 0.0).texture(1.0, 1.0).color(0, 0, 0, 0).next();
		bufferBuilder.vertex((double)this.right, (double)this.top, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.left, (double)this.top, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
		tessellator.draw();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex((double)this.left, (double)this.bottom, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.right, (double)(this.bottom - 4), 0.0).texture(1.0, 0.0).color(0, 0, 0, 0).next();
		bufferBuilder.vertex((double)this.left, (double)(this.bottom - 4), 0.0).texture(0.0, 0.0).color(0, 0, 0, 0).next();
		tessellator.draw();
		int p = this.getMaxScroll();
		if (p > 0) {
			int q = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
			q = MathHelper.clamp(q, 32, this.bottom - this.top - 8);
			int r = (int)this.getScrollAmount() * (this.bottom - this.top - q) / p + this.top;
			if (r < this.top) {
				r = this.top;
			}

			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)k, (double)this.bottom, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)l, (double)this.bottom, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)l, (double)this.top, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)k, (double)this.top, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)k, (double)(r + q), 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex((double)l, (double)(r + q), 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex((double)l, (double)r, 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex((double)k, (double)r, 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)k, (double)(r + q - 1), 0.0).texture(0.0, 1.0).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((double)(l - 1), (double)(r + q - 1), 0.0).texture(1.0, 1.0).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((double)(l - 1), (double)r, 0.0).texture(1.0, 0.0).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((double)k, (double)r, 0.0).texture(0.0, 0.0).color(192, 192, 192, 255).next();
			tessellator.draw();
		}

		this.renderDecorations(i, j);
		GlStateManager.enableTexture();
		GlStateManager.shadeModel(7424);
		GlStateManager.enableAlphaTest();
		GlStateManager.disableBlend();
	}

	protected void centerScrollOn(E entry) {
		this.setScrollAmount((double)(this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - (this.bottom - this.top) / 2));
	}

	protected void ensureVisible(E entry) {
		int i = this.getRowTop(this.children().indexOf(entry));
		int j = i - this.top - 4 - this.itemHeight;
		if (j < 0) {
			this.scroll(j);
		}

		int k = this.bottom - i - this.itemHeight - this.itemHeight;
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
		this.scrollAmount = MathHelper.clamp(d, 0.0, (double)this.getMaxScroll());
	}

	private int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
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
			E entry = this.getEntryAtPosition(d, e);
			if (entry != null) {
				if (entry.mouseClicked(d, e, i)) {
					this.setFocused(entry);
					this.setDragging(true);
					return true;
				}
			} else if (i == 0) {
				this.clickedHeader((int)(d - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(e - (double)this.top) + (int)this.getScrollAmount() - 4);
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
			if (e < (double)this.top) {
				this.setScrollAmount(0.0);
			} else if (e > (double)this.bottom) {
				this.setScrollAmount((double)this.getMaxScroll());
			} else {
				double h = (double)Math.max(1, this.getMaxScroll());
				int j = this.bottom - this.top;
				int k = MathHelper.clamp((int)((float)(j * j) / (float)this.getMaxPosition()), 32, j - 8);
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
			int k = MathHelper.clamp(j + i, 0, this.getItemCount() - 1);
			E entry = (E)this.children().get(k);
			this.setSelected(entry);
			this.ensureVisible(entry);
		}
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return e >= (double)this.top && e <= (double)this.bottom && d >= (double)this.left && d <= (double)this.right;
	}

	protected void renderList(int i, int j, int k, int l, float f) {
		int m = this.getItemCount();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.itemHeight + this.headerHeight;
			int p = this.itemHeight - 4;
			E entry = this.getEntry(n);
			int q = this.getRowWidth();
			if (this.renderSelection && this.isSelectedItem(n)) {
				int r = this.left + this.width / 2 - q / 2;
				int s = this.left + this.width / 2 + q / 2;
				GlStateManager.disableTexture();
				float g = this.isFocused() ? 1.0F : 0.5F;
				GlStateManager.color4f(g, g, g, 1.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex((double)r, (double)(o + p + 2), 0.0).next();
				bufferBuilder.vertex((double)s, (double)(o + p + 2), 0.0).next();
				bufferBuilder.vertex((double)s, (double)(o - 2), 0.0).next();
				bufferBuilder.vertex((double)r, (double)(o - 2), 0.0).next();
				tessellator.draw();
				GlStateManager.color4f(0.0F, 0.0F, 0.0F, 1.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex((double)(r + 1), (double)(o + p + 1), 0.0).next();
				bufferBuilder.vertex((double)(s - 1), (double)(o + p + 1), 0.0).next();
				bufferBuilder.vertex((double)(s - 1), (double)(o - 1), 0.0).next();
				bufferBuilder.vertex((double)(r + 1), (double)(o - 1), 0.0).next();
				tessellator.draw();
				GlStateManager.enableTexture();
			}

			int r = this.getRowTop(n);
			int s = this.getRowLeft();
			entry.render(n, r, s, q, p, k, l, this.isMouseOver((double)k, (double)l) && Objects.equals(this.getEntryAtPosition((double)k, (double)l), entry), f);
		}
	}

	protected int getRowLeft() {
		return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
	}

	protected int getRowTop(int i) {
		return this.top + 4 - (int)this.getScrollAmount() + i * this.itemHeight + this.headerHeight;
	}

	protected boolean isFocused() {
		return false;
	}

	protected void renderHoleBackground(int i, int j, int k, int l) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex((double)this.left, (double)j, 0.0).texture(0.0, (double)((float)j / 32.0F)).color(64, 64, 64, l).next();
		bufferBuilder.vertex((double)(this.left + this.width), (double)j, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)j / 32.0F))
			.color(64, 64, 64, l)
			.next();
		bufferBuilder.vertex((double)(this.left + this.width), (double)i, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)i / 32.0F))
			.color(64, 64, 64, k)
			.next();
		bufferBuilder.vertex((double)this.left, (double)i, 0.0).texture(0.0, (double)((float)i / 32.0F)).color(64, 64, 64, k).next();
		tessellator.draw();
	}

	protected E remove(int i) {
		E entry = (E)this.children.get(i);
		return this.removeEntry((E)this.children.get(i)) ? entry : null;
	}

	protected boolean removeEntry(E entry) {
		boolean bl = this.children.remove(entry);
		if (bl && entry == this.getSelected()) {
			this.setSelected(null);
		}

		return bl;
	}

	@Environment(EnvType.CLIENT)
	class Entries extends AbstractList<E> {
		private final List<E> entries = Lists.<E>newArrayList();

		private Entries() {
		}

		public E method_1912(int i) {
			return (E)this.entries.get(i);
		}

		public int size() {
			return this.entries.size();
		}

		public E method_1909(int i, E entry) {
			E entry2 = (E)this.entries.set(i, entry);
			entry.list = EntryListWidget.this;
			return entry2;
		}

		public void method_1910(int i, E entry) {
			this.entries.add(i, entry);
			entry.list = EntryListWidget.this;
		}

		public E method_1911(int i) {
			return (E)this.entries.remove(i);
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry<E extends EntryListWidget.Entry<E>> implements Element {
		@Deprecated
		EntryListWidget<E> list;

		public abstract void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f);

		@Override
		public boolean isMouseOver(double d, double e) {
			return Objects.equals(this.list.getEntryAtPosition(d, e), this);
		}
	}
}
