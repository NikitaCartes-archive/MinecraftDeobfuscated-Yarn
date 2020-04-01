package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Texts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BookScreen extends Screen {
	public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
	private BookContent contents;
	private int pageIndex;
	private List<Text> cachedPage = Collections.emptyList();
	private int cachedPageIndex = -1;
	private PageTurnWidget nextPageButton;
	private PageTurnWidget previousPageButton;
	private final boolean pageTurnSound;

	public BookScreen(BookContent pageProvider) {
		this(pageProvider, true);
	}

	public BookScreen() {
		this(BookContent.EMPTY, false);
	}

	private BookScreen(BookContent contents, boolean playPageTurnSound) {
		super(NarratorManager.EMPTY);
		this.contents = contents;
		this.pageTurnSound = playPageTurnSound;
	}

	public void setPageProvider(BookContent pageProvider) {
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
		this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.client.openScreen(null)));
	}

	protected void addPageButtons() {
		int i = (this.width - 192) / 2;
		int j = 2;
		this.nextPageButton = this.addButton(new PageTurnWidget(i + 116, 159, true, buttonWidget -> this.goToNextPage(), this.pageTurnSound));
		this.previousPageButton = this.addButton(new PageTurnWidget(i + 43, 159, false, buttonWidget -> this.goToPreviousPage(), this.pageTurnSound));
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
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BOOK_TEXTURE);
		int i = (this.width - 192) / 2;
		int j = 2;
		this.drawTexture(i, 2, 0, 0, 192, 192);
		String string = I18n.translate("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
		if (this.cachedPageIndex != this.pageIndex) {
			Text text = this.contents.getPage(this.pageIndex);
			this.cachedPage = Texts.wrapLines(text, 114, this.textRenderer, true, true);
		}

		this.cachedPageIndex = this.pageIndex;
		int k = this.getStringWidth(string);
		this.textRenderer.draw(string, (float)(i - k + 192 - 44), 18.0F, 0);
		int l = Math.min(128 / 9, this.cachedPage.size());

		for (int m = 0; m < l; m++) {
			Text text2 = (Text)this.cachedPage.get(m);
			this.textRenderer.draw(text2.asFormattedString(), (float)(i + 36), (float)(32 + m * 9), 0);
		}

		Text text3 = this.getTextAt((double)mouseX, (double)mouseY);
		if (text3 != null) {
			this.renderTextHoverEffect(text3, mouseX, mouseY);
		}

		super.render(mouseX, mouseY, delta);
	}

	private int getStringWidth(String string) {
		return this.textRenderer.getStringWidth(this.textRenderer.isRightToLeft() ? this.textRenderer.mirror(string) : string);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			Text text = this.getTextAt(mouseX, mouseY);
			if (text != null && this.handleTextClick(text)) {
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean handleTextClick(Text text) {
		ClickEvent clickEvent = text.getStyle().getClickEvent();
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
			boolean bl = super.handleTextClick(text);
			if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				this.client.openScreen(null);
			}

			return bl;
		}
	}

	@Nullable
	public Text getTextAt(double x, double y) {
		if (this.cachedPage == null) {
			return null;
		} else {
			int i = MathHelper.floor(x - (double)((this.width - 192) / 2) - 36.0);
			int j = MathHelper.floor(y - 2.0 - 30.0);
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / 9, this.cachedPage.size());
				if (i <= 114 && j < 9 * k + k) {
					int l = j / 9;
					if (l >= 0 && l < this.cachedPage.size()) {
						Text text = (Text)this.cachedPage.get(l);
						int m = 0;

						for (Text text2 : text) {
							if (text2 instanceof LiteralText) {
								m += this.client.textRenderer.getStringWidth(text2.asFormattedString());
								if (m > i) {
									return text2;
								}
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}
}
