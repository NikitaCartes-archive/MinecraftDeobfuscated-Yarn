package net.minecraft.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.gui.widget.BookPageButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WrittenBookScreen extends Screen {
	public static final WrittenBookScreen.Contents EMPTY_PROVIDER = new WrittenBookScreen.Contents() {
		@Override
		public int getLineCount() {
			return 0;
		}

		@Override
		public TextComponent getLine(int i) {
			return new StringTextComponent("");
		}
	};
	public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
	private WrittenBookScreen.Contents contents;
	private int pageIndex;
	private List<TextComponent> cachedPage = Collections.emptyList();
	private int cachedPageIndex = -1;
	private BookPageButtonWidget lastPageButton;
	private BookPageButtonWidget nextPageButton;

	public WrittenBookScreen(WrittenBookScreen.Contents contents) {
		this.contents = contents;
	}

	public WrittenBookScreen() {
		this(EMPTY_PROVIDER);
	}

	public void setPageProvider(WrittenBookScreen.Contents contents) {
		this.contents = contents;
		this.pageIndex = MathHelper.clamp(this.pageIndex, 0, contents.getLineCount());
		this.updatePageButtons();
		this.cachedPageIndex = -1;
	}

	public boolean setPage(int i) {
		int j = MathHelper.clamp(i, 0, this.contents.getLineCount() - 1);
		if (j != this.pageIndex) {
			this.pageIndex = j;
			this.updatePageButtons();
			this.cachedPageIndex = -1;
			return true;
		} else {
			return false;
		}
	}

	protected boolean jumpToPage(int i) {
		return this.setPage(i);
	}

	@Override
	protected void onInitialized() {
		this.addCloseButton();
		this.addPageButtons();
	}

	protected void addCloseButton() {
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, 196, 200, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				WrittenBookScreen.this.client.openScreen(null);
			}
		});
	}

	protected void addPageButtons() {
		int i = (this.width - 192) / 2;
		int j = 2;
		this.lastPageButton = this.addButton(new BookPageButtonWidget(1, i + 116, 159, true) {
			@Override
			public void onPressed(double d, double e) {
				WrittenBookScreen.this.goToNextPage();
			}

			@Override
			public void playPressedSound(SoundLoader soundLoader) {
				WrittenBookScreen.this.playPageTurnSound();
			}
		});
		this.nextPageButton = this.addButton(new BookPageButtonWidget(2, i + 43, 159, false) {
			@Override
			public void onPressed(double d, double e) {
				WrittenBookScreen.this.goToPreviousPage();
			}

			@Override
			public void playPressedSound(SoundLoader soundLoader) {
				WrittenBookScreen.this.playPageTurnSound();
			}
		});
		this.updatePageButtons();
	}

	private int getPageCount() {
		return this.contents.getLineCount();
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
		this.lastPageButton.visible = this.pageIndex < this.getPageCount() - 1;
		this.nextPageButton.visible = this.pageIndex > 0;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else {
			switch (i) {
				case 266:
					this.nextPageButton.onPressed(0.0, 0.0);
					return true;
				case 267:
					this.lastPageButton.onPressed(0.0, 0.0);
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BOOK_TEXTURE);
		int k = (this.width - 192) / 2;
		int l = 2;
		this.drawTexturedRect(k, 2, 0, 0, 192, 192);
		String string = I18n.translate("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
		if (this.cachedPageIndex != this.pageIndex) {
			TextComponent textComponent = this.contents.getLineOrDefault(this.pageIndex);
			this.cachedPage = TextComponentUtil.wrapLines(textComponent, 114, this.fontRenderer, true, true);
		}

		this.cachedPageIndex = this.pageIndex;
		int m = this.getStringWidth(string);
		this.fontRenderer.draw(string, (float)(k - m + 192 - 44), 18.0F, 0);
		int n = Math.min(128 / 9, this.cachedPage.size());

		for (int o = 0; o < n; o++) {
			TextComponent textComponent2 = (TextComponent)this.cachedPage.get(o);
			this.fontRenderer.draw(textComponent2.getFormattedText(), (float)(k + 36), (float)(32 + o * 9), 0);
		}

		TextComponent textComponent3 = this.getLineAt((double)i, (double)j);
		if (textComponent3 != null) {
			this.drawTextComponentHover(textComponent3, i, j);
		}

		super.draw(i, j, f);
	}

	private int getStringWidth(String string) {
		return this.fontRenderer.getStringWidth(this.fontRenderer.isRightToLeft() ? this.fontRenderer.mirror(string) : string);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			TextComponent textComponent = this.getLineAt(d, e);
			if (textComponent != null && this.handleTextComponentClick(textComponent)) {
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean handleTextComponentClick(TextComponent textComponent) {
		ClickEvent clickEvent = textComponent.getStyle().getClickEvent();
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
			boolean bl = super.handleTextComponentClick(textComponent);
			if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				this.client.openScreen(null);
			}

			return bl;
		}
	}

	@Nullable
	public TextComponent getLineAt(double d, double e) {
		if (this.cachedPage == null) {
			return null;
		} else {
			int i = MathHelper.floor(d - (double)((this.width - 192) / 2) - 36.0);
			int j = MathHelper.floor(e - 2.0 - 30.0);
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / 9, this.cachedPage.size());
				if (i <= 114 && j < 9 * k + k) {
					int l = j / 9;
					if (l >= 0 && l < this.cachedPage.size()) {
						TextComponent textComponent = (TextComponent)this.cachedPage.get(l);
						int m = 0;

						for (TextComponent textComponent2 : textComponent) {
							if (textComponent2 instanceof StringTextComponent) {
								m += this.client.textRenderer.getStringWidth(textComponent2.getFormattedText());
								if (m > i) {
									return textComponent2;
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

	public static List<String> getLines(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.getList("pages", 8).copy();
		Builder<String> builder = ImmutableList.builder();

		for (int i = 0; i < listTag.size(); i++) {
			builder.add(listTag.getString(i));
		}

		return builder.build();
	}

	protected void playPageTurnSound() {
		MinecraftClient.getInstance().getSoundLoader().play(PositionedSoundInstance.master(SoundEvents.field_17481, 1.0F));
	}

	@Environment(EnvType.CLIENT)
	public interface Contents {
		int getLineCount();

		TextComponent getLine(int i);

		default TextComponent getLineOrDefault(int i) {
			return (TextComponent)(i >= 0 && i < this.getLineCount() ? this.getLine(i) : new StringTextComponent(""));
		}

		static WrittenBookScreen.Contents create(ItemStack itemStack) {
			Item item = itemStack.getItem();
			if (item == Items.field_8360) {
				return new WrittenBookScreen.WrittenBookContents(itemStack);
			} else {
				return (WrittenBookScreen.Contents)(item == Items.field_8674 ? new WrittenBookScreen.WritableBookContents(itemStack) : WrittenBookScreen.EMPTY_PROVIDER);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WritableBookContents implements WrittenBookScreen.Contents {
		private final List<String> lines;

		public WritableBookContents(ItemStack itemStack) {
			this.lines = getLines(itemStack);
		}

		private static List<String> getLines(ItemStack itemStack) {
			CompoundTag compoundTag = itemStack.getTag();
			return (List<String>)(compoundTag != null ? WrittenBookScreen.getLines(compoundTag) : ImmutableList.of());
		}

		@Override
		public int getLineCount() {
			return this.lines.size();
		}

		@Override
		public TextComponent getLine(int i) {
			return new StringTextComponent((String)this.lines.get(i));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WrittenBookContents implements WrittenBookScreen.Contents {
		private final List<String> lines;

		public WrittenBookContents(ItemStack itemStack) {
			this.lines = getLines(itemStack);
		}

		private static List<String> getLines(ItemStack itemStack) {
			CompoundTag compoundTag = itemStack.getTag();
			return (List<String>)(compoundTag != null && WrittenBookItem.isValidBook(compoundTag)
				? WrittenBookScreen.getLines(compoundTag)
				: ImmutableList.of(new TranslatableTextComponent("book.invalid.tag").applyFormat(TextFormat.field_1079).getFormattedText()));
		}

		@Override
		public int getLineCount() {
			return this.lines.size();
		}

		@Override
		public TextComponent getLine(int i) {
			String string = (String)this.lines.get(i);

			try {
				TextComponent textComponent = TextComponent.Serializer.fromJsonString(string);
				if (textComponent != null) {
					return textComponent;
				}
			} catch (Exception var4) {
			}

			return new StringTextComponent(string);
		}
	}
}
