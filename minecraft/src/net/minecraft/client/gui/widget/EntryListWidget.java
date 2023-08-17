package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class EntryListWidget<E extends EntryListWidget.Entry<E>> extends AbstractParentElement implements Drawable, Selectable {
	protected static final int field_45909 = 6;
	private static final Identifier SCROLLER_TEXTURE = new Identifier("widget/scroller");
	protected final MinecraftClient client;
	protected final int itemHeight;
	private final List<E> children = new EntryListWidget.Entries();
	protected int width;
	protected int height;
	protected int top;
	protected int bottom;
	protected int right;
	protected int left;
	protected boolean centerListVertically = true;
	private double scrollAmount;
	private boolean renderHeader;
	protected int headerHeight;
	private boolean scrolling;
	@Nullable
	private E selected;
	private boolean renderBackground = true;
	@Nullable
	private E hoveredEntry;

	public EntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		this.client = client;
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.itemHeight = itemHeight;
		this.left = 0;
		this.right = width;
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

	/**
	 * {@return the selected entry of this entry list, or {@code null} if there is none}
	 */
	@Nullable
	public E getSelectedOrNull() {
		return this.selected;
	}

	public void setSelected(@Nullable E entry) {
		this.selected = entry;
	}

	public E getFirst() {
		return (E)this.children.get(0);
	}

	public void setRenderBackground(boolean renderBackground) {
		this.renderBackground = renderBackground;
	}

	@Nullable
	public E getFocused() {
		return (E)super.getFocused();
	}

	@Override
	public final List<E> children() {
		return this.children;
	}

	protected void clearEntries() {
		this.children.clear();
		this.selected = null;
	}

	protected void replaceEntries(Collection<E> newEntries) {
		this.clearEntries();
		this.children.addAll(newEntries);
	}

	protected E getEntry(int index) {
		return (E)this.children().get(index);
	}

	protected int addEntry(E entry) {
		this.children.add(entry);
		return this.children.size() - 1;
	}

	protected void addEntryToTop(E entry) {
		double d = (double)this.getMaxScroll() - this.getScrollAmount();
		this.children.add(0, entry);
		this.setScrollAmount((double)this.getMaxScroll() - d);
	}

	protected boolean removeEntryWithoutScrolling(E entry) {
		double d = (double)this.getMaxScroll() - this.getScrollAmount();
		boolean bl = this.removeEntry(entry);
		this.setScrollAmount((double)this.getMaxScroll() - d);
		return bl;
	}

	protected int getEntryCount() {
		return this.children().size();
	}

	protected boolean isSelectedEntry(int index) {
		return Objects.equals(this.getSelectedOrNull(), this.children().get(index));
	}

	@Nullable
	protected final E getEntryAtPosition(double x, double y) {
		int i = this.getRowWidth() / 2;
		int j = this.left + this.width / 2;
		int k = j - i;
		int l = j + i;
		int m = MathHelper.floor(y - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
		int n = m / this.itemHeight;
		return (E)(x < (double)this.getScrollbarPositionX() && x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getEntryCount()
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
		return this.getEntryCount() * this.itemHeight + this.headerHeight;
	}

	protected void clickedHeader(int x, int y) {
	}

	protected void renderHeader(DrawContext context, int x, int y) {
	}

	protected void renderDecorations(DrawContext context, int mouseX, int mouseY) {
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.hoveredEntry = this.isMouseOver((double)mouseX, (double)mouseY) ? this.getEntryAtPosition((double)mouseX, (double)mouseY) : null;
		if (this.renderBackground) {
			context.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
			int i = 32;
			context.drawTexture(
				Screen.OPTIONS_BACKGROUND_TEXTURE,
				this.left,
				this.top,
				(float)this.right,
				(float)(this.bottom + (int)this.getScrollAmount()),
				this.right - this.left,
				this.bottom - this.top,
				32,
				32
			);
			context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			int j = 4;
			context.fillGradient(RenderLayer.getGuiOverlay(), this.left, this.top, this.right, this.top + 4, -16777216, 0, 0);
			context.fillGradient(RenderLayer.getGuiOverlay(), this.left, this.bottom - 4, this.right, this.bottom, 0, -16777216, 0);
		}

		int i = this.getRowLeft();
		int j = this.top + 4 - (int)this.getScrollAmount();
		this.enableScissor(context);
		if (this.renderHeader) {
			this.renderHeader(context, i, j);
		}

		this.renderList(context, mouseX, mouseY, delta);
		context.disableScissor();
		int k = this.getMaxScroll();
		if (k > 0) {
			int l = this.getScrollbarPositionX();
			int m = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
			m = MathHelper.clamp(m, 32, this.bottom - this.top - 8);
			int n = (int)this.getScrollAmount() * (this.bottom - this.top - m) / k + this.top;
			if (n < this.top) {
				n = this.top;
			}

			context.fill(l, this.top, l + 6, this.bottom, -16777216);
			context.drawGuiTexture(SCROLLER_TEXTURE, l, n, 6, m);
		}

		this.renderDecorations(context, mouseX, mouseY);
		RenderSystem.disableBlend();
	}

	protected void enableScissor(DrawContext context) {
		context.enableScissor(this.left, this.top, this.right, this.bottom);
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
	}

	public double getScrollAmount() {
		return this.scrollAmount;
	}

	public void setScrollAmount(double amount) {
		this.scrollAmount = MathHelper.clamp(amount, 0.0, (double)this.getMaxScroll());
	}

	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
	}

	public int method_35721() {
		return (int)this.getScrollAmount() - this.height - this.headerHeight;
	}

	protected void updateScrollingState(double mouseX, double mouseY, int button) {
		this.scrolling = button == 0 && mouseX >= (double)this.getScrollbarPositionX() && mouseX < (double)(this.getScrollbarPositionX() + 6);
	}

	protected int getScrollbarPositionX() {
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
					E entry2 = this.getFocused();
					if (entry2 != entry && entry2 instanceof ParentElement parentElement) {
						parentElement.setFocused(null);
					}

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
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		this.setScrollAmount(this.getScrollAmount() - verticalAmount * (double)this.itemHeight / 2.0);
		return true;
	}

	@Override
	public void setFocused(@Nullable Element focused) {
		super.setFocused(focused);
		int i = this.children.indexOf(focused);
		if (i >= 0) {
			E entry = (E)this.children.get(i);
			this.setSelected(entry);
			if (this.client.getNavigationType().isKeyboard()) {
				this.ensureVisible(entry);
			}
		}
	}

	@Nullable
	protected E getNeighboringEntry(NavigationDirection direction) {
		return this.getNeighboringEntry(direction, entry -> true);
	}

	@Nullable
	protected E getNeighboringEntry(NavigationDirection direction, Predicate<E> predicate) {
		return this.getNeighboringEntry(direction, predicate, this.getSelectedOrNull());
	}

	@Nullable
	protected E getNeighboringEntry(NavigationDirection direction, Predicate<E> predicate, @Nullable E selected) {
		int i = switch (direction) {
			case RIGHT, LEFT -> 0;
			case UP -> -1;
			case DOWN -> 1;
		};
		if (!this.children().isEmpty() && i != 0) {
			int j;
			if (selected == null) {
				j = i > 0 ? 0 : this.children().size() - 1;
			} else {
				j = this.children().indexOf(selected) + i;
			}

			for (int k = j; k >= 0 && k < this.children.size(); k += i) {
				E entry = (E)this.children().get(k);
				if (predicate.test(entry)) {
					return entry;
				}
			}
		}

		return null;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseY >= (double)this.top && mouseY <= (double)this.bottom && mouseX >= (double)this.left && mouseX <= (double)this.right;
	}

	protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
		int i = this.getRowLeft();
		int j = this.getRowWidth();
		int k = this.itemHeight - 4;
		int l = this.getEntryCount();

		for (int m = 0; m < l; m++) {
			int n = this.getRowTop(m);
			int o = this.getRowBottom(m);
			if (o >= this.top && n <= this.bottom) {
				this.renderEntry(context, mouseX, mouseY, delta, m, i, n, j, k);
			}
		}
	}

	protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
		E entry = this.getEntry(index);
		entry.drawBorder(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
		if (this.isSelectedEntry(index)) {
			int i = this.isFocused() ? -1 : -8355712;
			this.drawSelectionHighlight(context, y, entryWidth, entryHeight, i, -16777216);
		}

		entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
	}

	protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
		int i = this.left + (this.width - entryWidth) / 2;
		int j = this.left + (this.width + entryWidth) / 2;
		context.fill(i, y - 2, j, y + entryHeight + 2, borderColor);
		context.fill(i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
	}

	public int getRowLeft() {
		return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
	}

	public int getRowRight() {
		return this.getRowLeft() + this.getRowWidth();
	}

	protected int getRowTop(int index) {
		return this.top + 4 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
	}

	protected int getRowBottom(int index) {
		return this.getRowTop(index) + this.itemHeight;
	}

	@Override
	public Selectable.SelectionType getType() {
		if (this.isFocused()) {
			return Selectable.SelectionType.FOCUSED;
		} else {
			return this.hoveredEntry != null ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
		}
	}

	@Nullable
	protected E remove(int index) {
		E entry = (E)this.children.get(index);
		return this.removeEntry((E)this.children.get(index)) ? entry : null;
	}

	protected boolean removeEntry(E entry) {
		boolean bl = this.children.remove(entry);
		if (bl && entry == this.getSelectedOrNull()) {
			this.setSelected(null);
		}

		return bl;
	}

	@Nullable
	protected E getHoveredEntry() {
		return this.hoveredEntry;
	}

	void setEntryParentList(EntryListWidget.Entry<E> entry) {
		entry.parentList = this;
	}

	protected void appendNarrations(NarrationMessageBuilder builder, E entry) {
		List<E> list = this.children();
		if (list.size() > 1) {
			int i = list.indexOf(entry);
			if (i != -1) {
				builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.list", i + 1, list.size()));
			}
		}
	}

	@Override
	public ScreenRect getNavigationFocus() {
		return new ScreenRect(this.left, this.top, this.right - this.left, this.bottom - this.top);
	}

	@Environment(EnvType.CLIENT)
	class Entries extends AbstractList<E> {
		private final List<E> entries = Lists.<E>newArrayList();

		public E get(int i) {
			return (E)this.entries.get(i);
		}

		public int size() {
			return this.entries.size();
		}

		public E set(int i, E entry) {
			E entry2 = (E)this.entries.set(i, entry);
			EntryListWidget.this.setEntryParentList(entry);
			return entry2;
		}

		public void add(int i, E entry) {
			this.entries.add(i, entry);
			EntryListWidget.this.setEntryParentList(entry);
		}

		public E remove(int i) {
			return (E)this.entries.remove(i);
		}
	}

	@Environment(EnvType.CLIENT)
	protected abstract static class Entry<E extends EntryListWidget.Entry<E>> implements Element {
		@Deprecated
		EntryListWidget<E> parentList;

		@Override
		public void setFocused(boolean focused) {
		}

		@Override
		public boolean isFocused() {
			return this.parentList.getFocused() == this;
		}

		/**
		 * Renders an entry in a list.
		 * 
		 * @param x the X coordinate of the entry
		 * @param y the Y coordinate of the entry
		 * @param index the index of the entry
		 * @param hovered whether the mouse is hovering over the entry
		 * @param mouseY the Y coordinate of the mouse
		 * @param mouseX the X coordinate of the mouse
		 * @param entryHeight the height of the entry
		 * @param entryWidth the width of the entry
		 */
		public abstract void render(
			DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
		);

		public void drawBorder(
			DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
		) {
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
		}
	}
}
