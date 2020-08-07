package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Rect2i;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

@Environment(EnvType.CLIENT)
public class BookEditScreen extends Screen {
	private static final Text field_25893 = new TranslatableText("book.editTitle");
	private static final Text field_25894 = new TranslatableText("book.finalizeWarning");
	private static final OrderedText field_25895 = OrderedText.styledString("_", Style.EMPTY.withColor(Formatting.field_1074));
	private static final OrderedText field_25896 = OrderedText.styledString("_", Style.EMPTY.withColor(Formatting.field_1080));
	private final PlayerEntity player;
	private final ItemStack itemStack;
	private boolean dirty;
	private boolean signing;
	private int tickCounter;
	private int currentPage;
	private final List<String> pages = Lists.<String>newArrayList();
	private String title = "";
	private final SelectionManager field_24269 = new SelectionManager(
		this::getCurrentPageContent,
		this::setPageContent,
		this::method_27595,
		this::method_27584,
		string -> string.length() < 1024 && this.textRenderer.getStringBoundedHeight(string, 114) <= 128
	);
	private final SelectionManager field_24270 = new SelectionManager(
		() -> this.title, string -> this.title = string, this::method_27595, this::method_27584, string -> string.length() < 16
	);
	private long lastClickTime;
	private int lastClickIndex = -1;
	private PageTurnWidget nextPageButton;
	private PageTurnWidget previousPageButton;
	private ButtonWidget doneButton;
	private ButtonWidget signButton;
	private ButtonWidget finalizeButton;
	private ButtonWidget cancelButton;
	private final Hand hand;
	@Nullable
	private BookEditScreen.PageContent pageContent = BookEditScreen.PageContent.EMPTY;
	private Text field_25891 = LiteralText.EMPTY;
	private final Text field_25892;

	public BookEditScreen(PlayerEntity playerEntity, ItemStack itemStack, Hand hand) {
		super(NarratorManager.EMPTY);
		this.player = playerEntity;
		this.itemStack = itemStack;
		this.hand = hand;
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null) {
			ListTag listTag = compoundTag.getList("pages", 8).method_10612();

			for (int i = 0; i < listTag.size(); i++) {
				this.pages.add(listTag.getString(i));
			}
		}

		if (this.pages.isEmpty()) {
			this.pages.add("");
		}

		this.field_25892 = new TranslatableText("book.byAuthor", playerEntity.getName()).formatted(Formatting.field_1063);
	}

	private void method_27584(String string) {
		if (this.client != null) {
			SelectionManager.setClipboard(this.client, string);
		}
	}

	private String method_27595() {
		return this.client != null ? SelectionManager.getClipboard(this.client) : "";
	}

	private int countPages() {
		return this.pages.size();
	}

	@Override
	public void tick() {
		super.tick();
		this.tickCounter++;
	}

	@Override
	protected void init() {
		this.invalidatePageContent();
		this.client.keyboard.enableRepeatEvents(true);
		this.signButton = this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, new TranslatableText("book.signButton"), buttonWidget -> {
			this.signing = true;
			this.updateButtons();
		}));
		this.doneButton = this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, ScreenTexts.DONE, buttonWidget -> {
			this.client.openScreen(null);
			this.finalizeBook(false);
		}));
		this.finalizeButton = this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, new TranslatableText("book.finalizeButton"), buttonWidget -> {
			if (this.signing) {
				this.finalizeBook(true);
				this.client.openScreen(null);
			}
		}));
		this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, ScreenTexts.CANCEL, buttonWidget -> {
			if (this.signing) {
				this.signing = false;
			}

			this.updateButtons();
		}));
		int i = (this.width - 192) / 2;
		int j = 2;
		this.nextPageButton = this.addButton(new PageTurnWidget(i + 116, 159, true, buttonWidget -> this.openNextPage(), true));
		this.previousPageButton = this.addButton(new PageTurnWidget(i + 43, 159, false, buttonWidget -> this.openPreviousPage(), true));
		this.updateButtons();
	}

	private void openPreviousPage() {
		if (this.currentPage > 0) {
			this.currentPage--;
		}

		this.updateButtons();
		this.method_27872();
	}

	private void openNextPage() {
		if (this.currentPage < this.countPages() - 1) {
			this.currentPage++;
		} else {
			this.appendNewPage();
			if (this.currentPage < this.countPages() - 1) {
				this.currentPage++;
			}
		}

		this.updateButtons();
		this.method_27872();
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void updateButtons() {
		this.previousPageButton.visible = !this.signing && this.currentPage > 0;
		this.nextPageButton.visible = !this.signing;
		this.doneButton.visible = !this.signing;
		this.signButton.visible = !this.signing;
		this.cancelButton.visible = this.signing;
		this.finalizeButton.visible = this.signing;
		this.finalizeButton.active = !this.title.trim().isEmpty();
	}

	private void removeEmptyPages() {
		ListIterator<String> listIterator = this.pages.listIterator(this.pages.size());

		while (listIterator.hasPrevious() && ((String)listIterator.previous()).isEmpty()) {
			listIterator.remove();
		}
	}

	private void finalizeBook(boolean signBook) {
		if (this.dirty) {
			this.removeEmptyPages();
			ListTag listTag = new ListTag();
			this.pages.stream().map(StringTag::of).forEach(listTag::add);
			if (!this.pages.isEmpty()) {
				this.itemStack.putSubTag("pages", listTag);
			}

			if (signBook) {
				this.itemStack.putSubTag("author", StringTag.of(this.player.getGameProfile().getName()));
				this.itemStack.putSubTag("title", StringTag.of(this.title.trim()));
			}

			this.client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(this.itemStack, signBook, this.hand));
		}
	}

	private void appendNewPage() {
		if (this.countPages() < 100) {
			this.pages.add("");
			this.dirty = true;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (this.signing) {
			return this.keyPressedSignMode(keyCode, scanCode, modifiers);
		} else {
			boolean bl = this.method_27592(keyCode, scanCode, modifiers);
			if (bl) {
				this.invalidatePageContent();
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		if (super.charTyped(chr, keyCode)) {
			return true;
		} else if (this.signing) {
			boolean bl = this.field_24270.insert(chr);
			if (bl) {
				this.updateButtons();
				this.dirty = true;
				return true;
			} else {
				return false;
			}
		} else if (SharedConstants.isValidChar(chr)) {
			this.field_24269.insert(Character.toString(chr));
			this.invalidatePageContent();
			return true;
		} else {
			return false;
		}
	}

	private boolean method_27592(int i, int j, int k) {
		if (Screen.isSelectAll(i)) {
			this.field_24269.selectAll();
			return true;
		} else if (Screen.isCopy(i)) {
			this.field_24269.copy();
			return true;
		} else if (Screen.isPaste(i)) {
			this.field_24269.paste();
			return true;
		} else if (Screen.isCut(i)) {
			this.field_24269.cut();
			return true;
		} else {
			switch (i) {
				case 257:
				case 335:
					this.field_24269.insert("\n");
					return true;
				case 259:
					this.field_24269.delete(-1);
					return true;
				case 261:
					this.field_24269.delete(1);
					return true;
				case 262:
					this.field_24269.moveCursor(1, Screen.hasShiftDown());
					return true;
				case 263:
					this.field_24269.moveCursor(-1, Screen.hasShiftDown());
					return true;
				case 264:
					this.method_27598();
					return true;
				case 265:
					this.method_27597();
					return true;
				case 266:
					this.previousPageButton.onPress();
					return true;
				case 267:
					this.nextPageButton.onPress();
					return true;
				case 268:
					this.moveCursorToTop();
					return true;
				case 269:
					this.moveCursorToBottom();
					return true;
				default:
					return false;
			}
		}
	}

	private void method_27597() {
		this.method_27580(-1);
	}

	private void method_27598() {
		this.method_27580(1);
	}

	private void method_27580(int i) {
		int j = this.field_24269.getSelectionStart();
		int k = this.getPageContent().method_27601(j, i);
		this.field_24269.method_27560(k, Screen.hasShiftDown());
	}

	private void moveCursorToTop() {
		int i = this.field_24269.getSelectionStart();
		int j = this.getPageContent().method_27600(i);
		this.field_24269.method_27560(j, Screen.hasShiftDown());
	}

	private void moveCursorToBottom() {
		BookEditScreen.PageContent pageContent = this.getPageContent();
		int i = this.field_24269.getSelectionStart();
		int j = pageContent.method_27604(i);
		this.field_24269.method_27560(j, Screen.hasShiftDown());
	}

	private boolean keyPressedSignMode(int keyCode, int scanCode, int modifiers) {
		switch (keyCode) {
			case 257:
			case 335:
				if (!this.title.isEmpty()) {
					this.finalizeBook(true);
					this.client.openScreen(null);
				}

				return true;
			case 259:
				this.field_24270.delete(-1);
				this.updateButtons();
				this.dirty = true;
				return true;
			default:
				return false;
		}
	}

	private String getCurrentPageContent() {
		return this.currentPage >= 0 && this.currentPage < this.pages.size() ? (String)this.pages.get(this.currentPage) : "";
	}

	private void setPageContent(String newContent) {
		if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
			this.pages.set(this.currentPage, newContent);
			this.dirty = true;
			this.invalidatePageContent();
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.setFocused(null);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BookScreen.BOOK_TEXTURE);
		int i = (this.width - 192) / 2;
		int j = 2;
		this.drawTexture(matrices, i, 2, 0, 0, 192, 192);
		if (this.signing) {
			boolean bl = this.tickCounter / 6 % 2 == 0;
			OrderedText orderedText = OrderedText.concat(OrderedText.styledString(this.title, Style.EMPTY), bl ? field_25895 : field_25896);
			int k = this.textRenderer.getWidth(field_25893);
			this.textRenderer.draw(matrices, field_25893, (float)(i + 36 + (114 - k) / 2), 34.0F, 0);
			int l = this.textRenderer.getWidth(orderedText);
			this.textRenderer.draw(matrices, orderedText, (float)(i + 36 + (114 - l) / 2), 50.0F, 0);
			int m = this.textRenderer.getWidth(this.field_25892);
			this.textRenderer.draw(matrices, this.field_25892, (float)(i + 36 + (114 - m) / 2), 60.0F, 0);
			this.textRenderer.drawTrimmed(field_25894, i + 36, 82, 114, 0);
		} else {
			int n = this.textRenderer.getWidth(this.field_25891);
			this.textRenderer.draw(matrices, this.field_25891, (float)(i - n + 192 - 44), 18.0F, 0);
			BookEditScreen.PageContent pageContent = this.getPageContent();

			for (BookEditScreen.Line line : pageContent.lines) {
				this.textRenderer.draw(matrices, line.text, (float)line.x, (float)line.y, -16777216);
			}

			this.method_27588(pageContent.field_24277);
			this.method_27581(matrices, pageContent.position, pageContent.field_24274);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	private void method_27581(MatrixStack matrixStack, BookEditScreen.Position position, boolean bl) {
		if (this.tickCounter / 6 % 2 == 0) {
			position = this.method_27590(position);
			if (!bl) {
				DrawableHelper.fill(matrixStack, position.x, position.y - 1, position.x + 1, position.y + 9, -16777216);
			} else {
				this.textRenderer.draw(matrixStack, "_", (float)position.x, (float)position.y, 0);
			}
		}
	}

	private void method_27588(Rect2i[] rect2is) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.field_5110);
		bufferBuilder.begin(7, VertexFormats.POSITION);

		for (Rect2i rect2i : rect2is) {
			int i = rect2i.getX();
			int j = rect2i.getY();
			int k = i + rect2i.getWidth();
			int l = j + rect2i.getHeight();
			bufferBuilder.vertex((double)i, (double)l, 0.0).next();
			bufferBuilder.vertex((double)k, (double)l, 0.0).next();
			bufferBuilder.vertex((double)k, (double)j, 0.0).next();
			bufferBuilder.vertex((double)i, (double)j, 0.0).next();
		}

		tessellator.draw();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}

	private BookEditScreen.Position method_27582(BookEditScreen.Position position) {
		return new BookEditScreen.Position(position.x - (this.width - 192) / 2 - 36, position.y - 32);
	}

	private BookEditScreen.Position method_27590(BookEditScreen.Position position) {
		return new BookEditScreen.Position(position.x + (this.width - 192) / 2 + 36, position.y + 32);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		} else {
			if (button == 0) {
				long l = Util.getMeasuringTimeMs();
				BookEditScreen.PageContent pageContent = this.getPageContent();
				int i = pageContent.method_27602(this.textRenderer, this.method_27582(new BookEditScreen.Position((int)mouseX, (int)mouseY)));
				if (i >= 0) {
					if (i != this.lastClickIndex || l - this.lastClickTime >= 250L) {
						this.field_24269.method_27560(i, Screen.hasShiftDown());
					} else if (!this.field_24269.method_27568()) {
						this.method_27589(i);
					} else {
						this.field_24269.selectAll();
					}

					this.invalidatePageContent();
				}

				this.lastClickIndex = i;
				this.lastClickTime = l;
			}

			return true;
		}
	}

	private void method_27589(int i) {
		String string = this.getCurrentPageContent();
		this.field_24269.method_27548(TextHandler.moveCursorByWords(string, -1, i, false), TextHandler.moveCursorByWords(string, 1, i, false));
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		} else {
			if (button == 0) {
				BookEditScreen.PageContent pageContent = this.getPageContent();
				int i = pageContent.method_27602(this.textRenderer, this.method_27582(new BookEditScreen.Position((int)mouseX, (int)mouseY)));
				this.field_24269.method_27560(i, true);
				this.invalidatePageContent();
			}

			return true;
		}
	}

	private BookEditScreen.PageContent getPageContent() {
		if (this.pageContent == null) {
			this.pageContent = this.createPageContent();
			this.field_25891 = new TranslatableText("book.pageIndicator", this.currentPage + 1, this.countPages());
		}

		return this.pageContent;
	}

	private void invalidatePageContent() {
		this.pageContent = null;
	}

	private void method_27872() {
		this.field_24269.moveCaretToEnd();
		this.invalidatePageContent();
	}

	private BookEditScreen.PageContent createPageContent() {
		String string = this.getCurrentPageContent();
		if (string.isEmpty()) {
			return BookEditScreen.PageContent.EMPTY;
		} else {
			int i = this.field_24269.getSelectionStart();
			int j = this.field_24269.getSelectionEnd();
			IntList intList = new IntArrayList();
			List<BookEditScreen.Line> list = Lists.<BookEditScreen.Line>newArrayList();
			MutableInt mutableInt = new MutableInt();
			MutableBoolean mutableBoolean = new MutableBoolean();
			TextHandler textHandler = this.textRenderer.getTextHandler();
			textHandler.wrapLines(string, 114, Style.EMPTY, true, (style, ix, jx) -> {
				int k = mutableInt.getAndIncrement();
				String string2x = string.substring(ix, jx);
				mutableBoolean.setValue(string2x.endsWith("\n"));
				String string3 = StringUtils.stripEnd(string2x, " \n");
				int lx = k * 9;
				BookEditScreen.Position positionx = this.method_27590(new BookEditScreen.Position(0, lx));
				intList.add(ix);
				list.add(new BookEditScreen.Line(style, string3, positionx.x, positionx.y));
			});
			int[] is = intList.toIntArray();
			boolean bl = i == string.length();
			BookEditScreen.Position position;
			if (bl && mutableBoolean.isTrue()) {
				position = new BookEditScreen.Position(0, list.size() * 9);
			} else {
				int k = method_27591(is, i);
				int l = this.textRenderer.getWidth(string.substring(is[k], i));
				position = new BookEditScreen.Position(l, k * 9);
			}

			List<Rect2i> list2 = Lists.<Rect2i>newArrayList();
			if (i != j) {
				int l = Math.min(i, j);
				int m = Math.max(i, j);
				int n = method_27591(is, l);
				int o = method_27591(is, m);
				if (n == o) {
					int p = n * 9;
					int q = is[n];
					list2.add(this.method_27585(string, textHandler, l, m, p, q));
				} else {
					int p = n + 1 > is.length ? string.length() : is[n + 1];
					list2.add(this.method_27585(string, textHandler, l, p, n * 9, is[n]));

					for (int q = n + 1; q < o; q++) {
						int r = q * 9;
						String string2 = string.substring(is[q], is[q + 1]);
						int s = (int)textHandler.getWidth(string2);
						list2.add(this.method_27583(new BookEditScreen.Position(0, r), new BookEditScreen.Position(s, r + 9)));
					}

					list2.add(this.method_27585(string, textHandler, is[o], m, o * 9, is[o]));
				}
			}

			return new BookEditScreen.PageContent(
				string, position, bl, is, (BookEditScreen.Line[])list.toArray(new BookEditScreen.Line[0]), (Rect2i[])list2.toArray(new Rect2i[0])
			);
		}
	}

	private static int method_27591(int[] is, int i) {
		int j = Arrays.binarySearch(is, i);
		return j < 0 ? -(j + 2) : j;
	}

	private Rect2i method_27585(String string, TextHandler textHandler, int i, int j, int k, int l) {
		String string2 = string.substring(l, i);
		String string3 = string.substring(l, j);
		BookEditScreen.Position position = new BookEditScreen.Position((int)textHandler.getWidth(string2), k);
		BookEditScreen.Position position2 = new BookEditScreen.Position((int)textHandler.getWidth(string3), k + 9);
		return this.method_27583(position, position2);
	}

	private Rect2i method_27583(BookEditScreen.Position position, BookEditScreen.Position position2) {
		BookEditScreen.Position position3 = this.method_27590(position);
		BookEditScreen.Position position4 = this.method_27590(position2);
		int i = Math.min(position3.x, position4.x);
		int j = Math.max(position3.x, position4.x);
		int k = Math.min(position3.y, position4.y);
		int l = Math.max(position3.y, position4.y);
		return new Rect2i(i, k, j - i, l - k);
	}

	@Environment(EnvType.CLIENT)
	static class Line {
		private final Style style;
		private final String content;
		private final Text text;
		private final int x;
		private final int y;

		public Line(Style style, String content, int x, int y) {
			this.style = style;
			this.content = content;
			this.x = x;
			this.y = y;
			this.text = new LiteralText(content).setStyle(style);
		}
	}

	@Environment(EnvType.CLIENT)
	static class PageContent {
		private static final BookEditScreen.PageContent EMPTY = new BookEditScreen.PageContent(
			"", new BookEditScreen.Position(0, 0), true, new int[]{0}, new BookEditScreen.Line[]{new BookEditScreen.Line(Style.EMPTY, "", 0, 0)}, new Rect2i[0]
		);
		private final String pageContent;
		private final BookEditScreen.Position position;
		private final boolean field_24274;
		private final int[] field_24275;
		private final BookEditScreen.Line[] lines;
		private final Rect2i[] field_24277;

		public PageContent(String pageContent, BookEditScreen.Position position, boolean bl, int[] is, BookEditScreen.Line[] lines, Rect2i[] rect2is) {
			this.pageContent = pageContent;
			this.position = position;
			this.field_24274 = bl;
			this.field_24275 = is;
			this.lines = lines;
			this.field_24277 = rect2is;
		}

		public int method_27602(TextRenderer textRenderer, BookEditScreen.Position position) {
			int i = position.y / 9;
			if (i < 0) {
				return 0;
			} else if (i >= this.lines.length) {
				return this.pageContent.length();
			} else {
				BookEditScreen.Line line = this.lines[i];
				return this.field_24275[i] + textRenderer.getTextHandler().getTrimmedLength(line.content, position.x, line.style);
			}
		}

		public int method_27601(int i, int j) {
			int k = BookEditScreen.method_27591(this.field_24275, i);
			int l = k + j;
			int o;
			if (0 <= l && l < this.field_24275.length) {
				int m = i - this.field_24275[k];
				int n = this.lines[l].content.length();
				o = this.field_24275[l] + Math.min(m, n);
			} else {
				o = i;
			}

			return o;
		}

		public int method_27600(int i) {
			int j = BookEditScreen.method_27591(this.field_24275, i);
			return this.field_24275[j];
		}

		public int method_27604(int i) {
			int j = BookEditScreen.method_27591(this.field_24275, i);
			return this.field_24275[j] + this.lines[j].content.length();
		}
	}

	@Environment(EnvType.CLIENT)
	static class Position {
		public final int x;
		public final int y;

		Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
