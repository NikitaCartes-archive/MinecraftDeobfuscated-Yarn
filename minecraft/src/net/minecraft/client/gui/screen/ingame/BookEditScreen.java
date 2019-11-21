package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.ListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.packet.BookUpdateC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BookEditScreen extends Screen {
	private final PlayerEntity player;
	private final ItemStack itemStack;
	private boolean dirty;
	private boolean signing;
	private int tickCounter;
	private int currentPage;
	private final List<String> pages = Lists.<String>newArrayList();
	private String title = "";
	private int cursorIndex;
	private int highlightTo;
	private long lastClickTime;
	private int lastClickIndex = -1;
	private PageTurnWidget nextPageButton;
	private PageTurnWidget previousPageButton;
	private ButtonWidget doneButton;
	private ButtonWidget signButton;
	private ButtonWidget finalizeButton;
	private ButtonWidget cancelButton;
	private final Hand hand;

	public BookEditScreen(PlayerEntity playerEntity, ItemStack itemStack, Hand hand) {
		super(NarratorManager.EMPTY);
		this.player = playerEntity;
		this.itemStack = itemStack;
		this.hand = hand;
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null) {
			ListTag listTag = compoundTag.getList("pages", 8).copy();

			for (int i = 0; i < listTag.size(); i++) {
				this.pages.add(listTag.getString(i));
			}
		}

		if (this.pages.isEmpty()) {
			this.pages.add("");
		}
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
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.signButton = this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, I18n.translate("book.signButton"), buttonWidget -> {
			this.signing = true;
			this.updateButtons();
		}));
		this.doneButton = this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.done"), buttonWidget -> {
			this.minecraft.openScreen(null);
			this.finalizeBook(false);
		}));
		this.finalizeButton = this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, I18n.translate("book.finalizeButton"), buttonWidget -> {
			if (this.signing) {
				this.finalizeBook(true);
				this.minecraft.openScreen(null);
			}
		}));
		this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.cancel"), buttonWidget -> {
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

	private String stripFromatting(String string) {
		StringBuilder stringBuilder = new StringBuilder();

		for (char c : string.toCharArray()) {
			if (c != 167 && c != 127) {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	private void openPreviousPage() {
		if (this.currentPage > 0) {
			this.currentPage--;
			this.cursorIndex = 0;
			this.highlightTo = this.cursorIndex;
		}

		this.updateButtons();
	}

	private void openNextPage() {
		if (this.currentPage < this.countPages() - 1) {
			this.currentPage++;
			this.cursorIndex = 0;
			this.highlightTo = this.cursorIndex;
		} else {
			this.appendNewPage();
			if (this.currentPage < this.countPages() - 1) {
				this.currentPage++;
			}

			this.cursorIndex = 0;
			this.highlightTo = this.cursorIndex;
		}

		this.updateButtons();
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
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

			this.minecraft.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(this.itemStack, signBook, this.hand));
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
		} else {
			return this.signing ? this.keyPressedSignMode(keyCode, scanCode, modifiers) : this.keyPressedEditMode(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		if (super.charTyped(chr, keyCode)) {
			return true;
		} else if (this.signing) {
			if (this.title.length() < 16 && SharedConstants.isValidChar(chr)) {
				this.title = this.title + Character.toString(chr);
				this.updateButtons();
				this.dirty = true;
				return true;
			} else {
				return false;
			}
		} else if (SharedConstants.isValidChar(chr)) {
			this.writeString(Character.toString(chr));
			return true;
		} else {
			return false;
		}
	}

	private boolean keyPressedEditMode(int keyCode, int scanCode, int modifiers) {
		String string = this.getCurrentPageContent();
		if (Screen.isSelectAll(keyCode)) {
			this.highlightTo = 0;
			this.cursorIndex = string.length();
			return true;
		} else if (Screen.isCopy(keyCode)) {
			this.minecraft.keyboard.setClipboard(this.getHighlightedText());
			return true;
		} else if (Screen.isPaste(keyCode)) {
			this.writeString(this.stripFromatting(Formatting.strip(this.minecraft.keyboard.getClipboard().replaceAll("\\r", ""))));
			this.highlightTo = this.cursorIndex;
			return true;
		} else if (Screen.isCut(keyCode)) {
			this.minecraft.keyboard.setClipboard(this.getHighlightedText());
			this.removeHighlightedText();
			return true;
		} else {
			switch (keyCode) {
				case 257:
				case 335:
					this.writeString("\n");
					return true;
				case 259:
					this.applyBackspaceKey(string);
					return true;
				case 261:
					this.applyDeleteKey(string);
					return true;
				case 262:
					this.applyRightArrowKey(string);
					return true;
				case 263:
					this.applyLeftArrowKey(string);
					return true;
				case 264:
					this.applyDownArrowKey(string);
					return true;
				case 265:
					this.applyUpArrowKey(string);
					return true;
				case 266:
					this.previousPageButton.onPress();
					return true;
				case 267:
					this.nextPageButton.onPress();
					return true;
				case 268:
					this.moveCursorToTop(string);
					return true;
				case 269:
					this.moveCursorToBottom(string);
					return true;
				default:
					return false;
			}
		}
	}

	private void applyBackspaceKey(String content) {
		if (!content.isEmpty()) {
			if (this.highlightTo != this.cursorIndex) {
				this.removeHighlightedText();
			} else if (this.cursorIndex > 0) {
				String string = new StringBuilder(content).deleteCharAt(Math.max(0, this.cursorIndex - 1)).toString();
				this.setPageContent(string);
				this.cursorIndex = Math.max(0, this.cursorIndex - 1);
				this.highlightTo = this.cursorIndex;
			}
		}
	}

	private void applyDeleteKey(String content) {
		if (!content.isEmpty()) {
			if (this.highlightTo != this.cursorIndex) {
				this.removeHighlightedText();
			} else if (this.cursorIndex < content.length()) {
				String string = new StringBuilder(content).deleteCharAt(Math.max(0, this.cursorIndex)).toString();
				this.setPageContent(string);
			}
		}
	}

	private void applyLeftArrowKey(String content) {
		int i = this.font.isRightToLeft() ? 1 : -1;
		if (Screen.hasControlDown()) {
			this.cursorIndex = this.font.findWordEdge(content, i, this.cursorIndex, true);
		} else {
			this.cursorIndex = Math.max(0, this.cursorIndex + i);
		}

		if (!Screen.hasShiftDown()) {
			this.highlightTo = this.cursorIndex;
		}
	}

	private void applyRightArrowKey(String content) {
		int i = this.font.isRightToLeft() ? -1 : 1;
		if (Screen.hasControlDown()) {
			this.cursorIndex = this.font.findWordEdge(content, i, this.cursorIndex, true);
		} else {
			this.cursorIndex = Math.min(content.length(), this.cursorIndex + i);
		}

		if (!Screen.hasShiftDown()) {
			this.highlightTo = this.cursorIndex;
		}
	}

	private void applyUpArrowKey(String content) {
		if (!content.isEmpty()) {
			BookEditScreen.Position position = this.getCursorPositionForIndex(content, this.cursorIndex);
			if (position.y == 0) {
				this.cursorIndex = 0;
				if (!Screen.hasShiftDown()) {
					this.highlightTo = this.cursorIndex;
				}
			} else {
				int i = this.getCharacterCountInFrontOfCursor(
					content, new BookEditScreen.Position(position.x + this.getCharWidthInString(content, this.cursorIndex) / 3, position.y - 9)
				);
				if (i >= 0) {
					this.cursorIndex = i;
					if (!Screen.hasShiftDown()) {
						this.highlightTo = this.cursorIndex;
					}
				}
			}
		}
	}

	private void applyDownArrowKey(String content) {
		if (!content.isEmpty()) {
			BookEditScreen.Position position = this.getCursorPositionForIndex(content, this.cursorIndex);
			int i = this.font.getStringBoundedHeight(content + "" + Formatting.BLACK + "_", 114);
			if (position.y + 9 == i) {
				this.cursorIndex = content.length();
				if (!Screen.hasShiftDown()) {
					this.highlightTo = this.cursorIndex;
				}
			} else {
				int j = this.getCharacterCountInFrontOfCursor(
					content, new BookEditScreen.Position(position.x + this.getCharWidthInString(content, this.cursorIndex) / 3, position.y + 9)
				);
				if (j >= 0) {
					this.cursorIndex = j;
					if (!Screen.hasShiftDown()) {
						this.highlightTo = this.cursorIndex;
					}
				}
			}
		}
	}

	private void moveCursorToTop(String content) {
		this.cursorIndex = this.getCharacterCountInFrontOfCursor(content, new BookEditScreen.Position(0, this.getCursorPositionForIndex(content, this.cursorIndex).y));
		if (!Screen.hasShiftDown()) {
			this.highlightTo = this.cursorIndex;
		}
	}

	private void moveCursorToBottom(String content) {
		this.cursorIndex = this.getCharacterCountInFrontOfCursor(
			content, new BookEditScreen.Position(113, this.getCursorPositionForIndex(content, this.cursorIndex).y)
		);
		if (!Screen.hasShiftDown()) {
			this.highlightTo = this.cursorIndex;
		}
	}

	private void removeHighlightedText() {
		if (this.highlightTo != this.cursorIndex) {
			String string = this.getCurrentPageContent();
			int i = Math.min(this.cursorIndex, this.highlightTo);
			int j = Math.max(this.cursorIndex, this.highlightTo);
			String string2 = string.substring(0, i) + string.substring(j);
			this.cursorIndex = i;
			this.highlightTo = this.cursorIndex;
			this.setPageContent(string2);
		}
	}

	private int getCharWidthInString(String string, int index) {
		return (int)this.font.getCharWidth(string.charAt(MathHelper.clamp(index, 0, string.length() - 1)));
	}

	private boolean keyPressedSignMode(int keyCode, int scanCode, int modifiers) {
		switch (keyCode) {
			case 257:
			case 335:
				if (!this.title.isEmpty()) {
					this.finalizeBook(true);
					this.minecraft.openScreen(null);
				}

				return true;
			case 259:
				if (!this.title.isEmpty()) {
					this.title = this.title.substring(0, this.title.length() - 1);
					this.updateButtons();
				}

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
		}
	}

	private void writeString(String string) {
		if (this.highlightTo != this.cursorIndex) {
			this.removeHighlightedText();
		}

		String string2 = this.getCurrentPageContent();
		this.cursorIndex = MathHelper.clamp(this.cursorIndex, 0, string2.length());
		String string3 = new StringBuilder(string2).insert(this.cursorIndex, string).toString();
		int i = this.font.getStringBoundedHeight(string3 + "" + Formatting.BLACK + "_", 114);
		if (i <= 128 && string3.length() < 1024) {
			this.setPageContent(string3);
			this.highlightTo = this.cursorIndex = Math.min(this.getCurrentPageContent().length(), this.cursorIndex + string.length());
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.setFocused(null);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BookScreen.BOOK_TEXTURE);
		int i = (this.width - 192) / 2;
		int j = 2;
		this.blit(i, 2, 0, 0, 192, 192);
		if (this.signing) {
			String string = this.title;
			if (this.tickCounter / 6 % 2 == 0) {
				string = string + "" + Formatting.BLACK + "_";
			} else {
				string = string + "" + Formatting.GRAY + "_";
			}

			String string2 = I18n.translate("book.editTitle");
			int k = this.getStringWidth(string2);
			this.font.draw(string2, (float)(i + 36 + (114 - k) / 2), 34.0F, 0);
			int l = this.getStringWidth(string);
			this.font.draw(string, (float)(i + 36 + (114 - l) / 2), 50.0F, 0);
			String string3 = I18n.translate("book.byAuthor", this.player.getName().getString());
			int m = this.getStringWidth(string3);
			this.font.draw(Formatting.DARK_GRAY + string3, (float)(i + 36 + (114 - m) / 2), 60.0F, 0);
			String string4 = I18n.translate("book.finalizeWarning");
			this.font.drawTrimmed(string4, i + 36, 82, 114, 0);
		} else {
			String string = I18n.translate("book.pageIndicator", this.currentPage + 1, this.countPages());
			String string2 = this.getCurrentPageContent();
			int k = this.getStringWidth(string);
			this.font.draw(string, (float)(i - k + 192 - 44), 18.0F, 0);
			this.font.drawTrimmed(string2, i + 36, 32, 114, 0);
			this.drawHighlight(string2);
			if (this.tickCounter / 6 % 2 == 0) {
				BookEditScreen.Position position = this.getCursorPositionForIndex(string2, this.cursorIndex);
				if (this.font.isRightToLeft()) {
					this.localizePosition(position);
					position.x = position.x - 4;
				}

				this.translateRelativePositionToGlPosition(position);
				if (this.cursorIndex < string2.length()) {
					DrawableHelper.fill(position.x, position.y - 1, position.x + 1, position.y + 9, -16777216);
				} else {
					this.font.draw("_", (float)position.x, (float)position.y, 0);
				}
			}
		}

		super.render(mouseX, mouseY, delta);
	}

	private int getStringWidth(String string) {
		return this.font.getStringWidth(this.font.isRightToLeft() ? this.font.mirror(string) : string);
	}

	private int getCharacterCountForWidth(String string, int width) {
		return this.font.getCharacterCountForWidth(string, width);
	}

	private String getHighlightedText() {
		String string = this.getCurrentPageContent();
		int i = Math.min(this.cursorIndex, this.highlightTo);
		int j = Math.max(this.cursorIndex, this.highlightTo);
		return string.substring(i, j);
	}

	private void drawHighlight(String content) {
		if (this.highlightTo != this.cursorIndex) {
			int i = Math.min(this.cursorIndex, this.highlightTo);
			int j = Math.max(this.cursorIndex, this.highlightTo);
			String string = content.substring(i, j);
			int k = this.font.findWordEdge(content, 1, j, true);
			String string2 = content.substring(i, k);
			BookEditScreen.Position position = this.getCursorPositionForIndex(content, i);
			BookEditScreen.Position position2 = new BookEditScreen.Position(position.x, position.y + 9);

			while (!string.isEmpty()) {
				int l = this.getCharacterCountForWidth(string2, 114 - position.x);
				if (string.length() <= l) {
					position2.x = position.x + this.getStringWidth(string);
					this.drawHighlightRect(position, position2);
					break;
				}

				l = Math.min(l, string.length() - 1);
				String string3 = string.substring(0, l);
				char c = string.charAt(l);
				boolean bl = c == ' ' || c == '\n';
				string = Formatting.getFormatAtEnd(string3) + string.substring(l + (bl ? 1 : 0));
				string2 = Formatting.getFormatAtEnd(string3) + string2.substring(l + (bl ? 1 : 0));
				position2.x = position.x + this.getStringWidth(string3 + " ");
				this.drawHighlightRect(position, position2);
				position.x = 0;
				position.y = position.y + 9;
				position2.y = position2.y + 9;
			}
		}
	}

	private void drawHighlightRect(BookEditScreen.Position position1, BookEditScreen.Position position2) {
		BookEditScreen.Position position = new BookEditScreen.Position(position1.x, position1.y);
		BookEditScreen.Position position3 = new BookEditScreen.Position(position2.x, position2.y);
		if (this.font.isRightToLeft()) {
			this.localizePosition(position);
			this.localizePosition(position3);
			int i = position3.x;
			position3.x = position.x;
			position.x = i;
		}

		this.translateRelativePositionToGlPosition(position);
		this.translateRelativePositionToGlPosition(position3);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)position.x, (double)position3.y, 0.0).next();
		bufferBuilder.vertex((double)position3.x, (double)position3.y, 0.0).next();
		bufferBuilder.vertex((double)position3.x, (double)position.y, 0.0).next();
		bufferBuilder.vertex((double)position.x, (double)position.y, 0.0).next();
		tessellator.draw();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}

	private BookEditScreen.Position getCursorPositionForIndex(String content, int index) {
		BookEditScreen.Position position = new BookEditScreen.Position();
		int i = 0;
		int j = 0;

		for (String string = content; !string.isEmpty(); j = i) {
			int k = this.getCharacterCountForWidth(string, 114);
			if (string.length() <= k) {
				String string2 = string.substring(0, Math.min(Math.max(index - j, 0), string.length()));
				position.x = position.x + this.getStringWidth(string2);
				break;
			}

			String string2 = string.substring(0, k);
			char c = string.charAt(k);
			boolean bl = c == ' ' || c == '\n';
			string = Formatting.getFormatAtEnd(string2) + string.substring(k + (bl ? 1 : 0));
			i += string2.length() + (bl ? 1 : 0);
			if (i - 1 >= index) {
				String string3 = string2.substring(0, Math.min(Math.max(index - j, 0), string2.length()));
				position.x = position.x + this.getStringWidth(string3);
				break;
			}

			position.y = position.y + 9;
		}

		return position;
	}

	private void localizePosition(BookEditScreen.Position position) {
		if (this.font.isRightToLeft()) {
			position.x = 114 - position.x;
		}
	}

	private void translateGlPositionToRelativePosition(BookEditScreen.Position position) {
		position.x = position.x - (this.width - 192) / 2 - 36;
		position.y = position.y - 32;
	}

	private void translateRelativePositionToGlPosition(BookEditScreen.Position position) {
		position.x = position.x + (this.width - 192) / 2 + 36;
		position.y = position.y + 32;
	}

	private int getCharacterCountForStringWidth(String string, int width) {
		if (width < 0) {
			return 0;
		} else {
			float f = 0.0F;
			boolean bl = false;
			String string2 = string + " ";

			for (int i = 0; i < string2.length(); i++) {
				char c = string2.charAt(i);
				float g = this.font.getCharWidth(c);
				if (c == 167 && i < string2.length() - 1) {
					c = string2.charAt(++i);
					if (c == 'l' || c == 'L') {
						bl = true;
					} else if (c == 'r' || c == 'R') {
						bl = false;
					}

					g = 0.0F;
				}

				float h = f;
				f += g;
				if (bl && g > 0.0F) {
					f++;
				}

				if ((float)width >= h && (float)width < f) {
					return i;
				}
			}

			return (float)width >= f ? string2.length() - 1 : -1;
		}
	}

	private int getCharacterCountInFrontOfCursor(String content, BookEditScreen.Position cursorPosition) {
		int i = 16 * 9;
		if (cursorPosition.y > i) {
			return -1;
		} else {
			int j = Integer.MIN_VALUE;
			int k = 9;
			int l = 0;

			for (String string = content; !string.isEmpty() && j < i; k += 9) {
				int m = this.getCharacterCountForWidth(string, 114);
				if (m < string.length()) {
					String string2 = string.substring(0, m);
					if (cursorPosition.y >= j && cursorPosition.y < k) {
						int n = this.getCharacterCountForStringWidth(string2, cursorPosition.x);
						return n < 0 ? -1 : l + n;
					}

					char c = string.charAt(m);
					boolean bl = c == ' ' || c == '\n';
					string = Formatting.getFormatAtEnd(string2) + string.substring(m + (bl ? 1 : 0));
					l += string2.length() + (bl ? 1 : 0);
				} else if (cursorPosition.y >= j && cursorPosition.y < k) {
					int o = this.getCharacterCountForStringWidth(string, cursorPosition.x);
					return o < 0 ? -1 : l + o;
				}

				j = k;
			}

			return content.length();
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			long l = Util.getMeasuringTimeMs();
			String string = this.getCurrentPageContent();
			if (!string.isEmpty()) {
				BookEditScreen.Position position = new BookEditScreen.Position((int)mouseX, (int)mouseY);
				this.translateGlPositionToRelativePosition(position);
				this.localizePosition(position);
				int i = this.getCharacterCountInFrontOfCursor(string, position);
				if (i >= 0) {
					if (i != this.lastClickIndex || l - this.lastClickTime >= 250L) {
						this.cursorIndex = i;
						if (!Screen.hasShiftDown()) {
							this.highlightTo = this.cursorIndex;
						}
					} else if (this.highlightTo == this.cursorIndex) {
						this.highlightTo = this.font.findWordEdge(string, -1, i, false);
						this.cursorIndex = this.font.findWordEdge(string, 1, i, false);
					} else {
						this.highlightTo = 0;
						this.cursorIndex = this.getCurrentPageContent().length();
					}
				}

				this.lastClickIndex = i;
			}

			this.lastClickTime = l;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (button == 0 && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
			String string = (String)this.pages.get(this.currentPage);
			BookEditScreen.Position position = new BookEditScreen.Position((int)mouseX, (int)mouseY);
			this.translateGlPositionToRelativePosition(position);
			this.localizePosition(position);
			int i = this.getCharacterCountInFrontOfCursor(string, position);
			if (i >= 0) {
				this.cursorIndex = i;
			}
		}

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Environment(EnvType.CLIENT)
	class Position {
		private int x;
		private int y;

		Position() {
		}

		Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
