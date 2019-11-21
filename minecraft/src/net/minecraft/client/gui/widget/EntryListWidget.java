package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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

	public EntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		this.minecraft = client;
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.itemHeight = itemHeight;
		this.left = 0;
		this.right = width;
	}

	public void setRenderSelection(boolean renderSelection) {
		this.renderSelection = renderSelection;
	}

	protected void setRenderHeader(boolean renderHeader, int headerHeight) {
		this.renderHeader = renderHeader;
		this.headerHeight = headerHeight;
		if (!renderHeader) {
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

	protected void replaceEntries(Collection<E> newEntries) {
		this.children.clear();
		this.children.addAll(newEntries);
	}

	protected E getEntry(int index) {
		return (E)this.children().get(index);
	}

	protected int addEntry(E entry) {
		this.children.add(entry);
		return this.children.size() - 1;
	}

	protected int getItemCount() {
		return this.children().size();
	}

	protected boolean isSelectedItem(int index) {
		return Objects.equals(this.getSelected(), this.children().get(index));
	}

	@Nullable
	protected final E getEntryAtPosition(double x, double y) {
		int i = this.getRowWidth() / 2;
		int j = this.left + this.width / 2;
		int k = j - i;
		int l = j + i;
		int m = MathHelper.floor(y - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
		int n = m / this.itemHeight;
		return (E)(x < (double)this.getScrollbarPosition() && x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getItemCount()
			? this.children().get(n)
			: null);
	}

	public void updateSize(int width, int height, int top, int bottom) {
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.left = 0;
		this.right = width;
	}

	public void setLeftPos(int left) {
		this.left = left;
		this.right = left + this.width;
	}

	protected int getMaxPosition() {
		return this.getItemCount() * this.itemHeight + this.headerHeight;
	}

	protected void clickedHeader(int x, int y) {
	}

	protected void renderHeader(int x, int y, Tessellator tessellator) {
	}

	protected void renderBackground() {
	}

	protected void renderDecorations(int mouseX, int mouseY) {
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		int i = this.getScrollbarPosition();
		int j = i + 6;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex((double)this.left, (double)this.bottom, 0.0)
			.texture((float)this.left / 32.0F, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0F)
			.color(32, 32, 32, 255)
			.next();
		bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0)
			.texture((float)this.right / 32.0F, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0F)
			.color(32, 32, 32, 255)
			.next();
		bufferBuilder.vertex((double)this.right, (double)this.top, 0.0)
			.texture((float)this.right / 32.0F, (float)(this.top + (int)this.getScrollAmount()) / 32.0F)
			.color(32, 32, 32, 255)
			.next();
		bufferBuilder.vertex((double)this.left, (double)this.top, 0.0)
			.texture((float)this.left / 32.0F, (float)(this.top + (int)this.getScrollAmount()) / 32.0F)
			.color(32, 32, 32, 255)
			.next();
		tessellator.draw();
		int k = this.getRowLeft();
		int l = this.top + 4 - (int)this.getScrollAmount();
		if (this.renderHeader) {
			this.renderHeader(k, l, tessellator);
		}

		this.renderList(k, l, mouseX, mouseY, delta);
		RenderSystem.disableDepthTest();
		this.renderHoleBackground(0, this.top, 255, 255);
		this.renderHoleBackground(this.bottom, this.height, 255, 255);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE
		);
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.disableTexture();
		int m = 4;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex((double)this.left, (double)(this.top + 4), 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 0).next();
		bufferBuilder.vertex((double)this.right, (double)(this.top + 4), 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 0).next();
		bufferBuilder.vertex((double)this.right, (double)this.top, 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.left, (double)this.top, 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
		tessellator.draw();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex((double)this.left, (double)this.bottom, 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.right, (double)(this.bottom - 4), 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 0).next();
		bufferBuilder.vertex((double)this.left, (double)(this.bottom - 4), 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 0).next();
		tessellator.draw();
		int n = this.getMaxScroll();
		if (n > 0) {
			int o = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
			o = MathHelper.clamp(o, 32, this.bottom - this.top - 8);
			int p = (int)this.getScrollAmount() * (this.bottom - this.top - o) / n + this.top;
			if (p < this.top) {
				p = this.top;
			}

			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)i, (double)this.bottom, 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)j, (double)this.bottom, 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)j, (double)this.top, 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)i, (double)this.top, 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)i, (double)(p + o), 0.0).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex((double)j, (double)(p + o), 0.0).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex((double)j, (double)p, 0.0).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex((double)i, (double)p, 0.0).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)i, (double)(p + o - 1), 0.0).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((double)(j - 1), (double)(p + o - 1), 0.0).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((double)(j - 1), (double)p, 0.0).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((double)i, (double)p, 0.0).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
			tessellator.draw();
		}

		this.renderDecorations(mouseX, mouseY);
		RenderSystem.enableTexture();
		RenderSystem.shadeModel(7424);
		RenderSystem.enableAlphaTest();
		RenderSystem.disableBlend();
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

	private void scroll(int amount) {
		this.setScrollAmount(this.getScrollAmount() + (double)amount);
		this.yDrag = -2;
	}

	public double getScrollAmount() {
		return this.scrollAmount;
	}

	public void setScrollAmount(double amount) {
		this.scrollAmount = MathHelper.clamp(amount, 0.0, (double)this.getMaxScroll());
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.updateScrollingState(mouseX, mouseY, button);
		if (!this.isMouseOver(mouseX, mouseY)) {
			return false;
		} else {
			E entry = this.getEntryAtPosition(mouseX, mouseY);
			if (entry != null) {
				if (entry.mouseClicked(mouseX, mouseY, button)) {
					this.setFocused(entry);
					this.setDragging(true);
					return true;
				}
			} else if (button == 0) {
				this.clickedHeader(
					(int)(mouseX - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.top) + (int)this.getScrollAmount() - 4
				);
				return true;
			}

			return this.scrolling;
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.getFocused() != null) {
			this.getFocused().mouseReleased(mouseX, mouseY, button);
		}

		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		} else if (button == 0 && this.scrolling) {
			if (mouseY < (double)this.top) {
				this.setScrollAmount(0.0);
			} else if (mouseY > (double)this.bottom) {
				this.setScrollAmount((double)this.getMaxScroll());
			} else {
				double d = (double)Math.max(1, this.getMaxScroll());
				int i = this.bottom - this.top;
				int j = MathHelper.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
				double e = Math.max(1.0, d / (double)(i - j));
				this.setScrollAmount(this.getScrollAmount() + deltaY * e);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		this.setScrollAmount(this.getScrollAmount() - amount * (double)this.itemHeight / 2.0);
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode == 264) {
			this.moveSelection(1);
			return true;
		} else if (keyCode == 265) {
			this.moveSelection(-1);
			return true;
		} else {
			return false;
		}
	}

	protected void moveSelection(int amount) {
		if (!this.children().isEmpty()) {
			int i = this.children().indexOf(this.getSelected());
			int j = MathHelper.clamp(i + amount, 0, this.getItemCount() - 1);
			E entry = (E)this.children().get(j);
			this.setSelected(entry);
			this.ensureVisible(entry);
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseY >= (double)this.top && mouseY <= (double)this.bottom && mouseX >= (double)this.left && mouseX <= (double)this.right;
	}

	protected void renderList(int x, int y, int mouseX, int mouseY, float delta) {
		int i = this.getItemCount();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		for (int j = 0; j < i; j++) {
			int k = this.getRowTop(j);
			int l = this.getRowBottom(j);
			if (l >= this.top && k <= this.bottom) {
				int m = y + j * this.itemHeight + this.headerHeight;
				int n = this.itemHeight - 4;
				E entry = this.getEntry(j);
				int o = this.getRowWidth();
				if (this.renderSelection && this.isSelectedItem(j)) {
					int p = this.left + this.width / 2 - o / 2;
					int q = this.left + this.width / 2 + o / 2;
					RenderSystem.disableTexture();
					float f = this.isFocused() ? 1.0F : 0.5F;
					RenderSystem.color4f(f, f, f, 1.0F);
					bufferBuilder.begin(7, VertexFormats.POSITION);
					bufferBuilder.vertex((double)p, (double)(m + n + 2), 0.0).next();
					bufferBuilder.vertex((double)q, (double)(m + n + 2), 0.0).next();
					bufferBuilder.vertex((double)q, (double)(m - 2), 0.0).next();
					bufferBuilder.vertex((double)p, (double)(m - 2), 0.0).next();
					tessellator.draw();
					RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
					bufferBuilder.begin(7, VertexFormats.POSITION);
					bufferBuilder.vertex((double)(p + 1), (double)(m + n + 1), 0.0).next();
					bufferBuilder.vertex((double)(q - 1), (double)(m + n + 1), 0.0).next();
					bufferBuilder.vertex((double)(q - 1), (double)(m - 1), 0.0).next();
					bufferBuilder.vertex((double)(p + 1), (double)(m - 1), 0.0).next();
					tessellator.draw();
					RenderSystem.enableTexture();
				}

				int p = this.getRowLeft();
				entry.render(
					j,
					k,
					p,
					o,
					n,
					mouseX,
					mouseY,
					this.isMouseOver((double)mouseX, (double)mouseY) && Objects.equals(this.getEntryAtPosition((double)mouseX, (double)mouseY), entry),
					delta
				);
			}
		}
	}

	protected int getRowLeft() {
		return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
	}

	protected int getRowTop(int index) {
		return this.top + 4 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
	}

	private int getRowBottom(int i) {
		return this.getRowTop(i) + this.itemHeight;
	}

	protected boolean isFocused() {
		return false;
	}

	protected void renderHoleBackground(int top, int bottom, int alphaTop, int alphaBottom) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex((double)this.left, (double)bottom, 0.0).texture(0.0F, (float)bottom / 32.0F).color(64, 64, 64, alphaBottom).next();
		bufferBuilder.vertex((double)(this.left + this.width), (double)bottom, 0.0)
			.texture((float)this.width / 32.0F, (float)bottom / 32.0F)
			.color(64, 64, 64, alphaBottom)
			.next();
		bufferBuilder.vertex((double)(this.left + this.width), (double)top, 0.0)
			.texture((float)this.width / 32.0F, (float)top / 32.0F)
			.color(64, 64, 64, alphaTop)
			.next();
		bufferBuilder.vertex((double)this.left, (double)top, 0.0).texture(0.0F, (float)top / 32.0F).color(64, 64, 64, alphaTop).next();
		tessellator.draw();
	}

	protected E remove(int index) {
		E entry = (E)this.children.get(index);
		return this.removeEntry((E)this.children.get(index)) ? entry : null;
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

		public E get(int i) {
			return (E)this.entries.get(i);
		}

		public int size() {
			return this.entries.size();
		}

		public E set(int i, E entry) {
			E entry2 = (E)this.entries.set(i, entry);
			entry.list = EntryListWidget.this;
			return entry2;
		}

		public void add(int i, E entry) {
			this.entries.add(i, entry);
			entry.list = EntryListWidget.this;
		}

		public E remove(int i) {
			return (E)this.entries.remove(i);
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry<E extends EntryListWidget.Entry<E>> implements Element {
		@Deprecated
		EntryListWidget<E> list;

		public abstract void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta);

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return Objects.equals(this.list.getEntryAtPosition(mouseX, mouseY), this);
		}
	}
}
