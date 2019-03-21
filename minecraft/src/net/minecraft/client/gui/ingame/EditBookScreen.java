package net.minecraft.client.gui.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.ListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.WrittenBookScreen;
import net.minecraft.client.gui.widget.BookPageButtonWidget;
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
import net.minecraft.text.TextFormat;
import net.minecraft.util.Hand;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EditBookScreen extends Screen {
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
	private BookPageButtonWidget buttonPreviousPage;
	private BookPageButtonWidget buttonNextPage;
	private ButtonWidget buttonDone;
	private ButtonWidget buttonSign;
	private ButtonWidget buttonFinalize;
	private ButtonWidget buttonCancel;
	private final Hand hand;

	public EditBookScreen(PlayerEntity playerEntity, ItemStack itemStack, Hand hand) {
		super(NarratorManager.field_18967);
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
	}

	private int countPages() {
		return this.pages.size();
	}

	@Override
	public void update() {
		super.update();
		this.tickCounter++;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.buttonSign = this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, 196, 98, 20, I18n.translate("book.signButton"), buttonWidget -> {
			this.signing = true;
			this.updateButtons();
		}));
		this.buttonDone = this.addButton(new ButtonWidget(this.screenWidth / 2 + 2, 196, 98, 20, I18n.translate("gui.done"), buttonWidget -> {
			this.client.openScreen(null);
			this.finalizeBook(false);
		}));
		this.buttonFinalize = this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, 196, 98, 20, I18n.translate("book.finalizeButton"), buttonWidget -> {
			if (this.signing) {
				this.finalizeBook(true);
				this.client.openScreen(null);
			}
		}));
		this.buttonCancel = this.addButton(new ButtonWidget(this.screenWidth / 2 + 2, 196, 98, 20, I18n.translate("gui.cancel"), buttonWidget -> {
			if (this.signing) {
				this.signing = false;
			}

			this.updateButtons();
		}));
		int i = (this.screenWidth - 192) / 2;
		int j = 2;
		this.buttonPreviousPage = this.addButton(new BookPageButtonWidget(i + 116, 159, true, buttonWidget -> this.openNextPage(), true));
		this.buttonNextPage = this.addButton(new BookPageButtonWidget(i + 43, 159, false, buttonWidget -> this.openPreviousPage(), true));
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
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void updateButtons() {
		this.buttonNextPage.visible = !this.signing && this.currentPage > 0;
		this.buttonDone.visible = !this.signing;
		this.buttonSign.visible = !this.signing;
		this.buttonCancel.visible = this.signing;
		this.buttonFinalize.visible = this.signing;
		this.buttonFinalize.active = !this.title.trim().isEmpty();
	}

	private void removeEmptyPages() {
		ListIterator<String> listIterator = this.pages.listIterator(this.pages.size());

		while (listIterator.hasPrevious() && ((String)listIterator.previous()).isEmpty()) {
			listIterator.remove();
		}
	}

	private void finalizeBook(boolean bl) {
		if (this.dirty) {
			this.removeEmptyPages();
			ListTag listTag = new ListTag();
			this.pages.stream().map(StringTag::new).forEach(listTag::add);
			if (!this.pages.isEmpty()) {
				this.itemStack.setChildTag("pages", listTag);
			}

			if (bl) {
				this.itemStack.setChildTag("author", new StringTag(this.player.getGameProfile().getName()));
				this.itemStack.setChildTag("title", new StringTag(this.title.trim()));
			}

			this.client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(this.itemStack, bl, this.hand));
		}
	}

	private void appendNewPage() {
		if (this.countPages() < 100) {
			this.pages.add("");
			this.dirty = true;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else {
			return this.signing ? this.keyPressedSignMode(i, j, k) : this.keyPressedEditMode(i, j, k);
		}
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (super.charTyped(c, i)) {
			return true;
		} else if (this.signing) {
			if (this.title.length() < 16 && SharedConstants.isValidChar(c)) {
				this.title = this.title + Character.toString(c);
				this.updateButtons();
				this.dirty = true;
				return true;
			} else {
				return false;
			}
		} else if (SharedConstants.isValidChar(c)) {
			this.writeString(Character.toString(c));
			return true;
		} else {
			return false;
		}
	}

	private boolean keyPressedEditMode(int i, int j, int k) {
		String string = this.getCurrentPageContent();
		if (Screen.isSelectAllShortcutPressed(i)) {
			this.highlightTo = 0;
			this.cursorIndex = string.length();
			return true;
		} else if (Screen.isCopyShortcutPressed(i)) {
			this.client.keyboard.setClipboard(this.getHighlightedText());
			return true;
		} else if (Screen.isPasteShortcutPressed(i)) {
			this.writeString(this.stripFromatting(TextFormat.stripFormatting(this.client.keyboard.getClipboard().replaceAll("\\r", ""))));
			this.highlightTo = this.cursorIndex;
			return true;
		} else if (Screen.isCutShortcutPressed(i)) {
			this.client.keyboard.setClipboard(this.getHighlightedText());
			this.removeHighlightedText();
			return true;
		} else {
			switch (i) {
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
					this.buttonNextPage.onPressed();
					return true;
				case 267:
					this.buttonPreviousPage.onPressed();
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

	private void applyBackspaceKey(String string) {
		if (!string.isEmpty()) {
			if (this.highlightTo != this.cursorIndex) {
				this.removeHighlightedText();
			} else if (this.cursorIndex > 0) {
				String string2 = new StringBuilder(string).deleteCharAt(Math.max(0, this.cursorIndex - 1)).toString();
				this.setPageContent(string2);
				this.cursorIndex = Math.max(0, this.cursorIndex - 1);
				this.highlightTo = this.cursorIndex;
			}
		}
	}

	private void applyDeleteKey(String string) {
		if (!string.isEmpty()) {
			if (this.highlightTo != this.cursorIndex) {
				this.removeHighlightedText();
			} else if (this.cursorIndex < string.length()) {
				String string2 = new StringBuilder(string).deleteCharAt(Math.max(0, this.cursorIndex)).toString();
				this.setPageContent(string2);
			}
		}
	}

	private void applyLeftArrowKey(String string) {
		int i = this.fontRenderer.isRightToLeft() ? 1 : -1;
		if (Screen.isControlPressed()) {
			this.cursorIndex = this.fontRenderer.findWordEdge(string, i, this.cursorIndex, true);
		} else {
			this.cursorIndex = Math.max(0, this.cursorIndex + i);
		}

		if (!Screen.isShiftPressed()) {
			this.highlightTo = this.cursorIndex;
		}
	}

	private void applyRightArrowKey(String string) {
		int i = this.fontRenderer.isRightToLeft() ? -1 : 1;
		if (Screen.isControlPressed()) {
			this.cursorIndex = this.fontRenderer.findWordEdge(string, i, this.cursorIndex, true);
		} else {
			this.cursorIndex = Math.min(string.length(), this.cursorIndex + i);
		}

		if (!Screen.isShiftPressed()) {
			this.highlightTo = this.cursorIndex;
		}
	}

	private void applyUpArrowKey(String string) {
		if (!string.isEmpty()) {
			EditBookScreen.Position position = this.getCursorPositionForIndex(string, this.cursorIndex);
			if (position.y == 0) {
				this.cursorIndex = 0;
				if (!Screen.isShiftPressed()) {
					this.highlightTo = this.cursorIndex;
				}
			} else {
				int i = this.getCharacterCountInFrontOfCursor(
					string, new EditBookScreen.Position(position.x + this.getCharWidthInString(string, this.cursorIndex) / 3, position.y - 9)
				);
				if (i >= 0) {
					this.cursorIndex = i;
					if (!Screen.isShiftPressed()) {
						this.highlightTo = this.cursorIndex;
					}
				}
			}
		}
	}

	private void applyDownArrowKey(String string) {
		if (!string.isEmpty()) {
			EditBookScreen.Position position = this.getCursorPositionForIndex(string, this.cursorIndex);
			int i = this.fontRenderer.getStringBoundedHeight(string + "" + TextFormat.BLACK + "_", 114);
			if (position.y + 9 == i) {
				this.cursorIndex = string.length();
				if (!Screen.isShiftPressed()) {
					this.highlightTo = this.cursorIndex;
				}
			} else {
				int j = this.getCharacterCountInFrontOfCursor(
					string, new EditBookScreen.Position(position.x + this.getCharWidthInString(string, this.cursorIndex) / 3, position.y + 9)
				);
				if (j >= 0) {
					this.cursorIndex = j;
					if (!Screen.isShiftPressed()) {
						this.highlightTo = this.cursorIndex;
					}
				}
			}
		}
	}

	private void moveCursorToTop(String string) {
		this.cursorIndex = this.getCharacterCountInFrontOfCursor(string, new EditBookScreen.Position(0, this.getCursorPositionForIndex(string, this.cursorIndex).y));
		if (!Screen.isShiftPressed()) {
			this.highlightTo = this.cursorIndex;
		}
	}

	private void moveCursorToBottom(String string) {
		this.cursorIndex = this.getCharacterCountInFrontOfCursor(string, new EditBookScreen.Position(113, this.getCursorPositionForIndex(string, this.cursorIndex).y));
		if (!Screen.isShiftPressed()) {
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

	private int getCharWidthInString(String string, int i) {
		return (int)this.fontRenderer.getCharWidth(string.charAt(MathHelper.clamp(i, 0, string.length() - 1)));
	}

	private boolean keyPressedSignMode(int i, int j, int k) {
		switch (i) {
			case 257:
			case 335:
				if (!this.title.isEmpty()) {
					this.finalizeBook(true);
					this.client.openScreen(null);
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

	private void setPageContent(String string) {
		if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
			this.pages.set(this.currentPage, string);
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
		int i = this.fontRenderer.getStringBoundedHeight(string3 + "" + TextFormat.BLACK + "_", 114);
		if (i <= 128 && string3.length() < 1024) {
			this.setPageContent(string3);
			this.highlightTo = this.cursorIndex = Math.min(this.getCurrentPageContent().length(), this.cursorIndex + string.length());
		}
	}

	@Override
	public void render(int i, int j, float f) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(WrittenBookScreen.BOOK_TEXTURE);
		int k = (this.screenWidth - 192) / 2;
		int l = 2;
		this.drawTexturedRect(k, 2, 0, 0, 192, 192);
		if (this.signing) {
			String string = this.title;
			if (this.tickCounter / 6 % 2 == 0) {
				string = string + "" + TextFormat.BLACK + "_";
			} else {
				string = string + "" + TextFormat.field_1080 + "_";
			}

			String string2 = I18n.translate("book.editTitle");
			int m = this.getStringWidth(string2);
			this.fontRenderer.draw(string2, (float)(k + 36 + (114 - m) / 2), 34.0F, 0);
			int n = this.getStringWidth(string);
			this.fontRenderer.draw(string, (float)(k + 36 + (114 - n) / 2), 50.0F, 0);
			String string3 = I18n.translate("book.byAuthor", this.player.getName().getString());
			int o = this.getStringWidth(string3);
			this.fontRenderer.draw(TextFormat.field_1063 + string3, (float)(k + 36 + (114 - o) / 2), 60.0F, 0);
			String string4 = I18n.translate("book.finalizeWarning");
			this.fontRenderer.drawStringBounded(string4, k + 36, 82, 114, 0);
		} else {
			String string = I18n.translate("book.pageIndicator", this.currentPage + 1, this.countPages());
			String string2 = this.getCurrentPageContent();
			int m = this.getStringWidth(string);
			this.fontRenderer.draw(string, (float)(k - m + 192 - 44), 18.0F, 0);
			this.fontRenderer.drawStringBounded(string2, k + 36, 32, 114, 0);
			this.drawHighlight(string2);
			if (this.tickCounter / 6 % 2 == 0) {
				EditBookScreen.Position position = this.getCursorPositionForIndex(string2, this.cursorIndex);
				if (this.fontRenderer.isRightToLeft()) {
					this.localizePosition(position);
					position.x = position.x - 4;
				}

				this.translateRelativePositionToGlPosition(position);
				if (this.cursorIndex < string2.length()) {
					DrawableHelper.drawRect(position.x, position.y - 1, position.x + 1, position.y + 9, -16777216);
				} else {
					this.fontRenderer.draw("_", (float)position.x, (float)position.y, 0);
				}
			}
		}

		super.render(i, j, f);
	}

	private int getStringWidth(String string) {
		return this.fontRenderer.getStringWidth(this.fontRenderer.isRightToLeft() ? this.fontRenderer.mirror(string) : string);
	}

	private int getCharacterCountForWidth(String string, int i) {
		return this.fontRenderer.getCharacterCountForWidth(string, i);
	}

	private String getHighlightedText() {
		String string = this.getCurrentPageContent();
		int i = Math.min(this.cursorIndex, this.highlightTo);
		int j = Math.max(this.cursorIndex, this.highlightTo);
		return string.substring(i, j);
	}

	private void drawHighlight(String string) {
		if (this.highlightTo != this.cursorIndex) {
			int i = Math.min(this.cursorIndex, this.highlightTo);
			int j = Math.max(this.cursorIndex, this.highlightTo);
			String string2 = string.substring(i, j);
			int k = this.fontRenderer.findWordEdge(string, 1, j, true);
			String string3 = string.substring(i, k);
			EditBookScreen.Position position = this.getCursorPositionForIndex(string, i);
			EditBookScreen.Position position2 = new EditBookScreen.Position(position.x, position.y + 9);

			while (!string2.isEmpty()) {
				int l = this.getCharacterCountForWidth(string3, 114 - position.x);
				if (string2.length() <= l) {
					position2.x = position.x + this.getStringWidth(string2);
					this.drawHighlightRect(position, position2);
					break;
				}

				l = Math.min(l, string2.length() - 1);
				String string4 = string2.substring(0, l);
				char c = string2.charAt(l);
				boolean bl = c == ' ' || c == '\n';
				string2 = TextFormat.method_538(string4) + string2.substring(l + (bl ? 1 : 0));
				string3 = TextFormat.method_538(string4) + string3.substring(l + (bl ? 1 : 0));
				position2.x = position.x + this.getStringWidth(string4 + " ");
				this.drawHighlightRect(position, position2);
				position.x = 0;
				position.y = position.y + 9;
				position2.y = position2.y + 9;
			}
		}
	}

	private void drawHighlightRect(EditBookScreen.Position position, EditBookScreen.Position position2) {
		EditBookScreen.Position position3 = new EditBookScreen.Position(position.x, position.y);
		EditBookScreen.Position position4 = new EditBookScreen.Position(position2.x, position2.y);
		if (this.fontRenderer.isRightToLeft()) {
			this.localizePosition(position3);
			this.localizePosition(position4);
			int i = position4.x;
			position4.x = position3.x;
			position3.x = i;
		}

		this.translateRelativePositionToGlPosition(position3);
		this.translateRelativePositionToGlPosition(position4);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.LogicOp.field_5110);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)position3.x, (double)position4.y, 0.0).next();
		bufferBuilder.vertex((double)position4.x, (double)position4.y, 0.0).next();
		bufferBuilder.vertex((double)position4.x, (double)position3.y, 0.0).next();
		bufferBuilder.vertex((double)position3.x, (double)position3.y, 0.0).next();
		tessellator.draw();
		GlStateManager.disableColorLogicOp();
		GlStateManager.enableTexture();
	}

	private EditBookScreen.Position getCursorPositionForIndex(String string, int i) {
		EditBookScreen.Position position = new EditBookScreen.Position();
		int j = 0;
		int k = 0;

		for (String string2 = string; !string2.isEmpty(); k = j) {
			int l = this.getCharacterCountForWidth(string2, 114);
			if (string2.length() <= l) {
				String string3 = string2.substring(0, Math.min(Math.max(i - k, 0), string2.length()));
				position.x = position.x + this.getStringWidth(string3);
				break;
			}

			String string3 = string2.substring(0, l);
			char c = string2.charAt(l);
			boolean bl = c == ' ' || c == '\n';
			string2 = TextFormat.method_538(string3) + string2.substring(l + (bl ? 1 : 0));
			j += string3.length() + (bl ? 1 : 0);
			if (j - 1 >= i) {
				String string4 = string3.substring(0, Math.min(Math.max(i - k, 0), string3.length()));
				position.x = position.x + this.getStringWidth(string4);
				break;
			}

			position.y = position.y + 9;
		}

		return position;
	}

	private void localizePosition(EditBookScreen.Position position) {
		if (this.fontRenderer.isRightToLeft()) {
			position.x = 114 - position.x;
		}
	}

	private void translateGlPositionToRelativePosition(EditBookScreen.Position position) {
		position.x = position.x - (this.screenWidth - 192) / 2 - 36;
		position.y = position.y - 32;
	}

	private void translateRelativePositionToGlPosition(EditBookScreen.Position position) {
		position.x = position.x + (this.screenWidth - 192) / 2 + 36;
		position.y = position.y + 32;
	}

	private int getCharacterCountForStringWidth(String string, int i) {
		if (i < 0) {
			return 0;
		} else {
			float f = 0.0F;
			boolean bl = false;
			String string2 = string + " ";

			for (int j = 0; j < string2.length(); j++) {
				char c = string2.charAt(j);
				float g = this.fontRenderer.getCharWidth(c);
				if (c == 167 && j < string2.length() - 1) {
					c = string2.charAt(++j);
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

				if ((float)i >= h && (float)i < f) {
					return j;
				}
			}

			return (float)i >= f ? string2.length() - 1 : -1;
		}
	}

	private int getCharacterCountInFrontOfCursor(String string, EditBookScreen.Position position) {
		int i = 16 * 9;
		if (position.y > i) {
			return -1;
		} else {
			int j = Integer.MIN_VALUE;
			int k = 9;
			int l = 0;

			for (String string2 = string; !string2.isEmpty() && j < i; k += 9) {
				int m = this.getCharacterCountForWidth(string2, 114);
				if (m < string2.length()) {
					String string3 = string2.substring(0, m);
					if (position.y >= j && position.y < k) {
						int n = this.getCharacterCountForStringWidth(string3, position.x);
						return n < 0 ? -1 : l + n;
					}

					char c = string2.charAt(m);
					boolean bl = c == ' ' || c == '\n';
					string2 = TextFormat.method_538(string3) + string2.substring(m + (bl ? 1 : 0));
					l += string3.length() + (bl ? 1 : 0);
				} else if (position.y >= j && position.y < k) {
					int o = this.getCharacterCountForStringWidth(string2, position.x);
					return o < 0 ? -1 : l + o;
				}

				j = k;
			}

			return string.length();
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			long l = SystemUtil.getMeasuringTimeMs();
			String string = this.getCurrentPageContent();
			if (!string.isEmpty()) {
				EditBookScreen.Position position = new EditBookScreen.Position((int)d, (int)e);
				this.translateGlPositionToRelativePosition(position);
				this.localizePosition(position);
				int j = this.getCharacterCountInFrontOfCursor(string, position);
				if (j >= 0) {
					if (j != this.lastClickIndex || l - this.lastClickTime >= 250L) {
						this.cursorIndex = j;
						if (!Screen.isShiftPressed()) {
							this.highlightTo = this.cursorIndex;
						}
					} else if (this.highlightTo == this.cursorIndex) {
						this.highlightTo = this.fontRenderer.findWordEdge(string, -1, j, false);
						this.cursorIndex = this.fontRenderer.findWordEdge(string, 1, j, false);
					} else {
						this.highlightTo = 0;
						this.cursorIndex = this.getCurrentPageContent().length();
					}
				}

				this.lastClickIndex = j;
			}

			this.lastClickTime = l;
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (i == 0 && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
			String string = (String)this.pages.get(this.currentPage);
			EditBookScreen.Position position = new EditBookScreen.Position((int)d, (int)e);
			this.translateGlPositionToRelativePosition(position);
			this.localizePosition(position);
			int j = this.getCharacterCountInFrontOfCursor(string, position);
			if (j >= 0) {
				this.cursorIndex = j;
			}
		}

		return super.mouseDragged(d, e, i, f, g);
	}

	@Environment(EnvType.CLIENT)
	class Position {
		private int x;
		private int y;

		Position() {
		}

		Position(int i, int j) {
			this.x = i;
			this.y = j;
		}
	}
}
