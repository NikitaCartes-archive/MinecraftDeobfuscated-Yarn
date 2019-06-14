package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BookScreen extends Screen {
	public static final BookScreen.Contents EMPTY_PROVIDER = new BookScreen.Contents() {
		@Override
		public int getLineCount() {
			return 0;
		}

		@Override
		public Text getLine(int i) {
			return new LiteralText("");
		}
	};
	public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
	private BookScreen.Contents contents;
	private int pageIndex;
	private List<Text> cachedPage = Collections.emptyList();
	private int cachedPageIndex = -1;
	private PageTurnWidget field_17122;
	private PageTurnWidget field_17123;
	private final boolean pageTurnSound;

	public BookScreen(BookScreen.Contents contents) {
		this(contents, true);
	}

	public BookScreen() {
		this(EMPTY_PROVIDER, false);
	}

	private BookScreen(BookScreen.Contents contents, boolean bl) {
		super(NarratorManager.EMPTY);
		this.contents = contents;
		this.pageTurnSound = bl;
	}

	public void setPageProvider(BookScreen.Contents contents) {
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
	protected void init() {
		this.addCloseButton();
		this.addPageButtons();
	}

	protected void addCloseButton() {
		this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.method_1507(null)));
	}

	protected void addPageButtons() {
		int i = (this.width - 192) / 2;
		int j = 2;
		this.field_17122 = this.addButton(new PageTurnWidget(i + 116, 159, true, buttonWidget -> this.goToNextPage(), this.pageTurnSound));
		this.field_17123 = this.addButton(new PageTurnWidget(i + 43, 159, false, buttonWidget -> this.goToPreviousPage(), this.pageTurnSound));
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
		this.field_17122.visible = this.pageIndex < this.getPageCount() - 1;
		this.field_17123.visible = this.pageIndex > 0;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else {
			switch (i) {
				case 266:
					this.field_17123.onPress();
					return true;
				case 267:
					this.field_17122.onPress();
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().bindTexture(BOOK_TEXTURE);
		int k = (this.width - 192) / 2;
		int l = 2;
		this.blit(k, 2, 0, 0, 192, 192);
		String string = I18n.translate("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
		if (this.cachedPageIndex != this.pageIndex) {
			Text text = this.contents.getLineOrDefault(this.pageIndex);
			this.cachedPage = TextComponentUtil.wrapLines(text, 114, this.font, true, true);
		}

		this.cachedPageIndex = this.pageIndex;
		int m = this.getStringWidth(string);
		this.font.draw(string, (float)(k - m + 192 - 44), 18.0F, 0);
		int n = Math.min(128 / 9, this.cachedPage.size());

		for (int o = 0; o < n; o++) {
			Text text2 = (Text)this.cachedPage.get(o);
			this.font.draw(text2.asFormattedString(), (float)(k + 36), (float)(32 + o * 9), 0);
		}

		Text text3 = this.getLineAt((double)i, (double)j);
		if (text3 != null) {
			this.renderComponentHoverEffect(text3, i, j);
		}

		super.render(i, j, f);
	}

	private int getStringWidth(String string) {
		return this.font.getStringWidth(this.font.isRightToLeft() ? this.font.mirror(string) : string);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			Text text = this.getLineAt(d, e);
			if (text != null && this.handleComponentClicked(text)) {
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean handleComponentClicked(Text text) {
		ClickEvent clickEvent = text.getStyle().getClickEvent();
		if (clickEvent == null) {
			return false;
		} else if (clickEvent.getAction() == ClickEvent.Action.field_11748) {
			String string = clickEvent.getValue();

			try {
				int i = Integer.parseInt(string) - 1;
				return this.jumpToPage(i);
			} catch (Exception var5) {
				return false;
			}
		} else {
			boolean bl = super.handleComponentClicked(text);
			if (bl && clickEvent.getAction() == ClickEvent.Action.field_11750) {
				this.minecraft.method_1507(null);
			}

			return bl;
		}
	}

	@Nullable
	public Text getLineAt(double d, double e) {
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
						Text text = (Text)this.cachedPage.get(l);
						int m = 0;

						for (Text text2 : text) {
							if (text2 instanceof LiteralText) {
								m += this.minecraft.field_1772.getStringWidth(text2.asFormattedString());
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

	public static List<String> getLines(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.getList("pages", 8).method_10612();
		Builder<String> builder = ImmutableList.builder();

		for (int i = 0; i < listTag.size(); i++) {
			builder.add(listTag.getString(i));
		}

		return builder.build();
	}

	@Environment(EnvType.CLIENT)
	public interface Contents {
		int getLineCount();

		Text getLine(int i);

		default Text getLineOrDefault(int i) {
			return (Text)(i >= 0 && i < this.getLineCount() ? this.getLine(i) : new LiteralText(""));
		}

		static BookScreen.Contents create(ItemStack itemStack) {
			Item item = itemStack.getItem();
			if (item == Items.field_8360) {
				return new BookScreen.WrittenBookContents(itemStack);
			} else {
				return (BookScreen.Contents)(item == Items.field_8674 ? new BookScreen.WritableBookContents(itemStack) : BookScreen.EMPTY_PROVIDER);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WritableBookContents implements BookScreen.Contents {
		private final List<String> lines;

		public WritableBookContents(ItemStack itemStack) {
			this.lines = getLines(itemStack);
		}

		private static List<String> getLines(ItemStack itemStack) {
			CompoundTag compoundTag = itemStack.getTag();
			return (List<String>)(compoundTag != null ? BookScreen.getLines(compoundTag) : ImmutableList.of());
		}

		@Override
		public int getLineCount() {
			return this.lines.size();
		}

		@Override
		public Text getLine(int i) {
			return new LiteralText((String)this.lines.get(i));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WrittenBookContents implements BookScreen.Contents {
		private final List<String> lines;

		public WrittenBookContents(ItemStack itemStack) {
			this.lines = getLines(itemStack);
		}

		private static List<String> getLines(ItemStack itemStack) {
			CompoundTag compoundTag = itemStack.getTag();
			return (List<String>)(compoundTag != null && WrittenBookItem.isValid(compoundTag)
				? BookScreen.getLines(compoundTag)
				: ImmutableList.of(new TranslatableText("book.invalid.tag").formatted(Formatting.field_1079).asFormattedString()));
		}

		@Override
		public int getLineCount() {
			return this.lines.size();
		}

		@Override
		public Text getLine(int i) {
			String string = (String)this.lines.get(i);

			try {
				Text text = Text.Serializer.fromJson(string);
				if (text != null) {
					return text;
				}
			} catch (Exception var4) {
			}

			return new LiteralText(string);
		}
	}
}
