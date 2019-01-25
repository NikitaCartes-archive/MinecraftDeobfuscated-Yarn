package net.minecraft.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonParseException;
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
	public static final WrittenBookScreen.class_3931 EMPTY_PAGE_PROVIDER = new WrittenBookScreen.class_3931() {
		@Override
		public int method_17560() {
			return 0;
		}

		@Override
		public TextComponent method_17561(int i) {
			return new StringTextComponent("");
		}
	};
	public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
	private WrittenBookScreen.class_3931 pageProvider;
	private int pageIndex;
	private List<TextComponent> cachedPage = Collections.emptyList();
	private int cachedPageIndex = -1;
	private BookPageButtonWidget lastPageButton;
	private BookPageButtonWidget nextPageButton;

	public WrittenBookScreen(WrittenBookScreen.class_3931 arg) {
		this.pageProvider = arg;
	}

	public WrittenBookScreen() {
		this(EMPTY_PAGE_PROVIDER);
	}

	public void setPageProvider(WrittenBookScreen.class_3931 arg) {
		this.pageProvider = arg;
		this.pageIndex = MathHelper.clamp(this.pageIndex, 0, arg.method_17560());
		this.updatePageButtons();
		this.cachedPageIndex = -1;
	}

	public boolean setPage(int i) {
		int j = MathHelper.clamp(i, 0, this.pageProvider.method_17560() - 1);
		if (j != this.pageIndex) {
			this.pageIndex = j;
			this.updatePageButtons();
			this.cachedPageIndex = -1;
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_17789(int i) {
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
		return this.pageProvider.method_17560();
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
			TextComponent textComponent = this.pageProvider.method_17563(this.pageIndex);
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
				return this.method_17789(i);
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
								m += this.client.fontRenderer.getStringWidth(textComponent2.getFormattedText());
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

	public static List<String> method_17555(CompoundTag compoundTag) {
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
	public interface class_3931 {
		int method_17560();

		TextComponent method_17561(int i);

		default TextComponent method_17563(int i) {
			return (TextComponent)(i >= 0 && i < this.method_17560() ? this.method_17561(i) : new StringTextComponent(""));
		}

		static WrittenBookScreen.class_3931 method_17562(ItemStack itemStack) {
			Item item = itemStack.getItem();
			if (item == Items.field_8360) {
				return new WrittenBookScreen.class_3933(itemStack);
			} else {
				return (WrittenBookScreen.class_3931)(item == Items.field_8674 ? new WrittenBookScreen.class_3932(itemStack) : WrittenBookScreen.EMPTY_PAGE_PROVIDER);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3932 implements WrittenBookScreen.class_3931 {
		private final List<String> field_17419;

		public class_3932(ItemStack itemStack) {
			this.field_17419 = method_17564(itemStack);
		}

		private static List<String> method_17564(ItemStack itemStack) {
			CompoundTag compoundTag = itemStack.getTag();
			return (List<String>)(compoundTag != null ? WrittenBookScreen.method_17555(compoundTag) : ImmutableList.of());
		}

		@Override
		public int method_17560() {
			return this.field_17419.size();
		}

		@Override
		public TextComponent method_17561(int i) {
			return new StringTextComponent((String)this.field_17419.get(i));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3933 implements WrittenBookScreen.class_3931 {
		private final List<String> field_17420;

		public class_3933(ItemStack itemStack) {
			this.field_17420 = method_17565(itemStack);
		}

		private static List<String> method_17565(ItemStack itemStack) {
			CompoundTag compoundTag = itemStack.getTag();
			return (List<String>)(compoundTag != null && WrittenBookItem.method_8053(compoundTag)
				? WrittenBookScreen.method_17555(compoundTag)
				: ImmutableList.of(new TranslatableTextComponent("book.invalid.tag").applyFormat(TextFormat.DARK_RED).getFormattedText()));
		}

		@Override
		public int method_17560() {
			return this.field_17420.size();
		}

		@Override
		public TextComponent method_17561(int i) {
			String string = (String)this.field_17420.get(i);

			try {
				TextComponent textComponent = TextComponent.Serializer.fromJsonString(string);
				if (textComponent != null) {
					return textComponent;
				}
			} catch (JsonParseException var4) {
			}

			return new StringTextComponent(string);
		}
	}
}
