package net.minecraft.client.gui.screen.pack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

@Environment(EnvType.CLIENT)
public class PackListWidget extends AlwaysSelectedEntryListWidget<PackListWidget.ResourcePackEntry> {
	static final Identifier SELECT_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/select_highlighted");
	static final Identifier SELECT_TEXTURE = Identifier.ofVanilla("transferable_list/select");
	static final Identifier UNSELECT_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/unselect_highlighted");
	static final Identifier UNSELECT_TEXTURE = Identifier.ofVanilla("transferable_list/unselect");
	static final Identifier MOVE_UP_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/move_up_highlighted");
	static final Identifier MOVE_UP_TEXTURE = Identifier.ofVanilla("transferable_list/move_up");
	static final Identifier MOVE_DOWN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/move_down_highlighted");
	static final Identifier MOVE_DOWN_TEXTURE = Identifier.ofVanilla("transferable_list/move_down");
	static final Text INCOMPATIBLE = Text.translatable("pack.incompatible");
	static final Text INCOMPATIBLE_CONFIRM = Text.translatable("pack.incompatible.confirm.title");
	private final Text title;
	final PackScreen screen;

	public PackListWidget(MinecraftClient client, PackScreen screen, int width, int height, Text title) {
		super(client, width, height, 33, 36);
		this.screen = screen;
		this.title = title;
		this.centerListVertically = false;
		this.setRenderHeader(true, (int)(9.0F * 1.5F));
	}

	@Override
	protected void renderHeader(DrawContext context, int x, int y) {
		Text text = Text.empty().append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
		context.drawText(
			this.client.textRenderer, text, x + this.width / 2 - this.client.textRenderer.getWidth(text) / 2, Math.min(this.getY() + 3, y), Colors.WHITE, false
		);
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarX() {
		return this.getRight() - 6;
	}

	@Override
	protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
		if (this.isScrollbarVisible()) {
			int i = 2;
			int j = this.getRowLeft() - 2;
			int k = this.getRight() - 6 - 1;
			int l = y - 2;
			int m = y + entryHeight + 2;
			context.fill(j, l, k, m, borderColor);
			context.fill(j + 1, l + 1, k - 1, m - 1, fillColor);
		} else {
			super.drawSelectionHighlight(context, y, entryWidth, entryHeight, borderColor, fillColor);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.getSelectedOrNull() != null) {
			switch (keyCode) {
				case 32:
				case 257:
					this.getSelectedOrNull().toggle();
					return true;
				default:
					if (Screen.hasShiftDown()) {
						switch (keyCode) {
							case 264:
								this.getSelectedOrNull().moveTowardEnd();
								return true;
							case 265:
								this.getSelectedOrNull().moveTowardStart();
								return true;
						}
					}
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Environment(EnvType.CLIENT)
	public static class ResourcePackEntry extends AlwaysSelectedEntryListWidget.Entry<PackListWidget.ResourcePackEntry> {
		private static final int field_32403 = 157;
		private static final int field_32404 = 157;
		private static final String ELLIPSIS = "...";
		private final PackListWidget widget;
		protected final MinecraftClient client;
		private final ResourcePackOrganizer.Pack pack;
		private final OrderedText displayName;
		private final MultilineText description;
		private final OrderedText incompatibleText;
		private final MultilineText compatibilityNotificationText;

		public ResourcePackEntry(MinecraftClient client, PackListWidget widget, ResourcePackOrganizer.Pack pack) {
			this.client = client;
			this.pack = pack;
			this.widget = widget;
			this.displayName = trimTextToWidth(client, pack.getDisplayName());
			this.description = createMultilineText(client, pack.getDecoratedDescription());
			this.incompatibleText = trimTextToWidth(client, PackListWidget.INCOMPATIBLE);
			this.compatibilityNotificationText = createMultilineText(client, pack.getCompatibility().getNotification());
		}

		private static OrderedText trimTextToWidth(MinecraftClient client, Text text) {
			int i = client.textRenderer.getWidth(text);
			if (i > 157) {
				StringVisitable stringVisitable = StringVisitable.concat(
					client.textRenderer.trimToWidth(text, 157 - client.textRenderer.getWidth("...")), StringVisitable.plain("...")
				);
				return Language.getInstance().reorder(stringVisitable);
			} else {
				return text.asOrderedText();
			}
		}

		private static MultilineText createMultilineText(MinecraftClient client, Text text) {
			return MultilineText.create(client.textRenderer, 157, 2, text);
		}

		@Override
		public Text getNarration() {
			return Text.translatable("narrator.select", this.pack.getDisplayName());
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			ResourcePackCompatibility resourcePackCompatibility = this.pack.getCompatibility();
			if (!resourcePackCompatibility.isCompatible()) {
				int i = x + entryWidth - 3 - (this.widget.isScrollbarVisible() ? 7 : 0);
				context.fill(x - 1, y - 1, i, y + entryHeight + 1, -8978432);
			}

			context.drawTexture(this.pack.getIconId(), x, y, 0.0F, 0.0F, 32, 32, 32, 32);
			OrderedText orderedText = this.displayName;
			MultilineText multilineText = this.description;
			if (this.isSelectable()
				&& (this.client.options.getTouchscreen().getValue() || hovered || this.widget.getSelectedOrNull() == this && this.widget.isFocused())) {
				context.fill(x, y, x + 32, y + 32, -1601138544);
				int j = mouseX - x;
				int k = mouseY - y;
				if (!this.pack.getCompatibility().isCompatible()) {
					orderedText = this.incompatibleText;
					multilineText = this.compatibilityNotificationText;
				}

				if (this.pack.canBeEnabled()) {
					if (j < 32) {
						context.drawGuiTexture(PackListWidget.SELECT_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
					} else {
						context.drawGuiTexture(PackListWidget.SELECT_TEXTURE, x, y, 32, 32);
					}
				} else {
					if (this.pack.canBeDisabled()) {
						if (j < 16) {
							context.drawGuiTexture(PackListWidget.UNSELECT_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
						} else {
							context.drawGuiTexture(PackListWidget.UNSELECT_TEXTURE, x, y, 32, 32);
						}
					}

					if (this.pack.canMoveTowardStart()) {
						if (j < 32 && j > 16 && k < 16) {
							context.drawGuiTexture(PackListWidget.MOVE_UP_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
						} else {
							context.drawGuiTexture(PackListWidget.MOVE_UP_TEXTURE, x, y, 32, 32);
						}
					}

					if (this.pack.canMoveTowardEnd()) {
						if (j < 32 && j > 16 && k > 16) {
							context.drawGuiTexture(PackListWidget.MOVE_DOWN_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
						} else {
							context.drawGuiTexture(PackListWidget.MOVE_DOWN_TEXTURE, x, y, 32, 32);
						}
					}
				}
			}

			context.drawTextWithShadow(this.client.textRenderer, orderedText, x + 32 + 2, y + 1, 16777215);
			multilineText.drawWithShadow(context, x + 32 + 2, y + 12, 10, -8355712);
		}

		public String getName() {
			return this.pack.getName();
		}

		private boolean isSelectable() {
			return !this.pack.isPinned() || !this.pack.isAlwaysEnabled();
		}

		public void toggle() {
			if (this.pack.canBeEnabled() && this.enable()) {
				this.widget.screen.switchFocusedList(this.widget);
			} else if (this.pack.canBeDisabled()) {
				this.pack.disable();
				this.widget.screen.switchFocusedList(this.widget);
			}
		}

		void moveTowardStart() {
			if (this.pack.canMoveTowardStart()) {
				this.pack.moveTowardStart();
			}
		}

		void moveTowardEnd() {
			if (this.pack.canMoveTowardEnd()) {
				this.pack.moveTowardEnd();
			}
		}

		private boolean enable() {
			if (this.pack.getCompatibility().isCompatible()) {
				this.pack.enable();
				return true;
			} else {
				Text text = this.pack.getCompatibility().getConfirmMessage();
				this.client.setScreen(new ConfirmScreen(confirmed -> {
					this.client.setScreen(this.widget.screen);
					if (confirmed) {
						this.pack.enable();
					}
				}, PackListWidget.INCOMPATIBLE_CONFIRM, text));
				return false;
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d = mouseX - (double)this.widget.getRowLeft();
			double e = mouseY - (double)this.widget.getRowTop(this.widget.children().indexOf(this));
			if (this.isSelectable() && d <= 32.0) {
				this.widget.screen.clearSelection();
				if (this.pack.canBeEnabled()) {
					this.enable();
					return true;
				}

				if (d < 16.0 && this.pack.canBeDisabled()) {
					this.pack.disable();
					return true;
				}

				if (d > 16.0 && e < 16.0 && this.pack.canMoveTowardStart()) {
					this.pack.moveTowardStart();
					return true;
				}

				if (d > 16.0 && e > 16.0 && this.pack.canMoveTowardEnd()) {
					this.pack.moveTowardEnd();
					return true;
				}
			}

			return super.mouseClicked(mouseX, mouseY, button);
		}
	}
}
