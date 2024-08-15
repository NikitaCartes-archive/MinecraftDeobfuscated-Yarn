package net.minecraft.client.gui.screen.ingame;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BookScreen extends Screen {
	public static final int field_32328 = 16;
	public static final int field_32329 = 36;
	public static final int field_32330 = 30;
	private static final int field_52807 = 256;
	private static final int field_52808 = 256;
	public static final BookScreen.Contents EMPTY_PROVIDER = new BookScreen.Contents(List.of());
	public static final Identifier BOOK_TEXTURE = Identifier.ofVanilla("textures/gui/book.png");
	protected static final int MAX_TEXT_WIDTH = 114;
	protected static final int MAX_TEXT_HEIGHT = 128;
	protected static final int WIDTH = 192;
	protected static final int HEIGHT = 192;
	private BookScreen.Contents contents;
	private int pageIndex;
	private List<OrderedText> cachedPage = Collections.emptyList();
	private int cachedPageIndex = -1;
	private Text pageIndexText = ScreenTexts.EMPTY;
	private PageTurnWidget nextPageButton;
	private PageTurnWidget previousPageButton;
	private final boolean pageTurnSound;

	public BookScreen(BookScreen.Contents pageProvider) {
		this(pageProvider, true);
	}

	public BookScreen() {
		this(EMPTY_PROVIDER, false);
	}

	private BookScreen(BookScreen.Contents contents, boolean playPageTurnSound) {
		super(NarratorManager.EMPTY);
		this.contents = contents;
		this.pageTurnSound = playPageTurnSound;
	}

	public void setPageProvider(BookScreen.Contents pageProvider) {
		this.contents = pageProvider;
		this.pageIndex = MathHelper.clamp(this.pageIndex, 0, pageProvider.getPageCount());
		this.updatePageButtons();
		this.cachedPageIndex = -1;
	}

	public boolean setPage(int index) {
		int i = MathHelper.clamp(index, 0, this.contents.getPageCount() - 1);
		if (i != this.pageIndex) {
			this.pageIndex = i;
			this.updatePageButtons();
			this.cachedPageIndex = -1;
			return true;
		} else {
			return false;
		}
	}

	protected boolean jumpToPage(int page) {
		return this.setPage(page);
	}

	@Override
	protected void init() {
		this.addCloseButton();
		this.addPageButtons();
	}

	protected void addCloseButton() {
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, 196, 200, 20).build());
	}

	protected void addPageButtons() {
		int i = (this.width - 192) / 2;
		int j = 2;
		this.nextPageButton = this.addDrawableChild(new PageTurnWidget(i + 116, 159, true, button -> this.goToNextPage(), this.pageTurnSound));
		this.previousPageButton = this.addDrawableChild(new PageTurnWidget(i + 43, 159, false, button -> this.goToPreviousPage(), this.pageTurnSound));
		this.updatePageButtons();
	}

	private int getPageCount() {
		return this.contents.getPageCount();
	}

	protected void goToPreviousPage() {
		if (this.pageIndex > 0) {
			this.pageIndex--;
		}

		this.updatePageButtons();
	}

	protected void goToNextPage() {
		if (this.pageIndex < this.getPageCount() - 1) {
			this.pageIndex++;
		}

		this.updatePageButtons();
	}

	private void updatePageButtons() {
		this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
		this.previousPageButton.visible = this.pageIndex > 0;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else {
			switch (keyCode) {
				case 266:
					this.previousPageButton.onPress();
					return true;
				case 267:
					this.nextPageButton.onPress();
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		int i = (this.width - 192) / 2;
		int j = 2;
		if (this.cachedPageIndex != this.pageIndex) {
			StringVisitable stringVisitable = this.contents.getPage(this.pageIndex);
			this.cachedPage = this.textRenderer.wrapLines(stringVisitable, 114);
			this.pageIndexText = Text.translatable("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
		}

		this.cachedPageIndex = this.pageIndex;
		int k = this.textRenderer.getWidth(this.pageIndexText);
		context.drawText(this.textRenderer, this.pageIndexText, i - k + 192 - 44, 18, 0, false);
		int l = Math.min(128 / 9, this.cachedPage.size());

		for (int m = 0; m < l; m++) {
			OrderedText orderedText = (OrderedText)this.cachedPage.get(m);
			context.drawText(this.textRenderer, orderedText, i + 36, 32 + m * 9, 0, false);
		}

		Style style = this.getTextStyleAt((double)mouseX, (double)mouseY);
		if (style != null) {
			context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
		}
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderInGameBackground(context);
		context.drawTexture(RenderLayer::getGuiTextured, BOOK_TEXTURE, (this.width - 192) / 2, 2, 0.0F, 0.0F, 192, 192, 256, 256);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			Style style = this.getTextStyleAt(mouseX, mouseY);
			if (style != null && this.handleTextClick(style)) {
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean handleTextClick(Style style) {
		ClickEvent clickEvent = style.getClickEvent();
		if (clickEvent == null) {
			return false;
		} else if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
			String string = clickEvent.getValue();

			try {
				int i = Integer.parseInt(string) - 1;
				return this.jumpToPage(i);
			} catch (Exception var5) {
				return false;
			}
		} else {
			boolean bl = super.handleTextClick(style);
			if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				this.closeScreen();
			}

			return bl;
		}
	}

	protected void closeScreen() {
		this.client.setScreen(null);
	}

	@Nullable
	public Style getTextStyleAt(double x, double y) {
		if (this.cachedPage.isEmpty()) {
			return null;
		} else {
			int i = MathHelper.floor(x - (double)((this.width - 192) / 2) - 36.0);
			int j = MathHelper.floor(y - 2.0 - 30.0);
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / 9, this.cachedPage.size());
				if (i <= 114 && j < 9 * k + k) {
					int l = j / 9;
					if (l >= 0 && l < this.cachedPage.size()) {
						OrderedText orderedText = (OrderedText)this.cachedPage.get(l);
						return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, i);
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Contents(List<Text> pages) {
		public int getPageCount() {
			return this.pages.size();
		}

		public StringVisitable getPage(int index) {
			return index >= 0 && index < this.getPageCount() ? (StringVisitable)this.pages.get(index) : StringVisitable.EMPTY;
		}

		@Nullable
		public static BookScreen.Contents create(ItemStack stack) {
			boolean bl = MinecraftClient.getInstance().shouldFilterText();
			WrittenBookContentComponent writtenBookContentComponent = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
			if (writtenBookContentComponent != null) {
				return new BookScreen.Contents(writtenBookContentComponent.getPages(bl));
			} else {
				WritableBookContentComponent writableBookContentComponent = stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
				return writableBookContentComponent != null ? new BookScreen.Contents(writableBookContentComponent.stream(bl).map(Text::literal).toList()) : null;
			}
		}
	}
}
