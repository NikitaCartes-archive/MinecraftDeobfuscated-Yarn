package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.menu.AlwaysSelectedItemListWidget;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.menu.options.ResourcePackOptionsScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ResourcePackListWidget extends AlwaysSelectedItemListWidget<ResourcePackListWidget.ResourcePackItem> {
	private static final Identifier RESOURCE_PACKS_LOCATION = new Identifier("textures/gui/resource_packs.png");
	private static final TextComponent INCOMPATIBLE = new TranslatableTextComponent("resourcePack.incompatible");
	private static final TextComponent INCOMPATIBLE_CONFIRM = new TranslatableTextComponent("resourcePack.incompatible.confirm.title");
	protected final MinecraftClient field_3166;
	private final TextComponent field_18978;

	public ResourcePackListWidget(MinecraftClient minecraftClient, int i, int j, TextComponent textComponent) {
		super(minecraftClient, i, j, 32, j - 55 + 4, 36);
		this.field_3166 = minecraftClient;
		this.verticallyCenter = false;
		this.setRenderHeader(true, (int)(9.0F * 1.5F));
		this.field_18978 = textComponent;
	}

	@Override
	protected void renderHeader(int i, int j, Tessellator tessellator) {
		TextComponent textComponent = new StringTextComponent("").append(this.field_18978).applyFormat(TextFormat.field_1073, TextFormat.field_1067);
		this.field_3166
			.textRenderer
			.draw(
				textComponent.getFormattedText(),
				(float)(i + this.width / 2 - this.field_3166.textRenderer.getStringWidth(textComponent.getFormattedText()) / 2),
				(float)Math.min(this.top + 3, j),
				16777215
			);
	}

	@Override
	public int getItemWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.right - 6;
	}

	public void addEntry(ResourcePackListWidget.ResourcePackItem resourcePackItem) {
		this.addItem(resourcePackItem);
		resourcePackItem.field_19130 = this;
	}

	@Environment(EnvType.CLIENT)
	public static class ResourcePackItem extends AlwaysSelectedItemListWidget.Item<ResourcePackListWidget.ResourcePackItem> {
		private ResourcePackListWidget field_19130;
		protected final MinecraftClient field_19128;
		protected final ResourcePackOptionsScreen field_19129;
		private final ClientResourcePackContainer field_19131;

		public ResourcePackItem(
			ResourcePackListWidget resourcePackListWidget, ResourcePackOptionsScreen resourcePackOptionsScreen, ClientResourcePackContainer clientResourcePackContainer
		) {
			this.field_19129 = resourcePackOptionsScreen;
			this.field_19128 = MinecraftClient.getInstance();
			this.field_19131 = clientResourcePackContainer;
			this.field_19130 = resourcePackListWidget;
		}

		public void method_20145(SelectedResourcePackListWidget selectedResourcePackListWidget) {
			this.method_20150()
				.getSortingDirection()
				.locate(selectedResourcePackListWidget.children(), this, ResourcePackListWidget.ResourcePackItem::method_20150, true);
			this.field_19130 = selectedResourcePackListWidget;
		}

		protected void method_20144() {
			this.field_19131.drawIcon(this.field_19128.getTextureManager());
		}

		protected ResourcePackCompatibility method_20147() {
			return this.field_19131.getCompatibility();
		}

		protected String method_20148() {
			return this.field_19131.getDescription().getFormattedText();
		}

		protected String method_20149() {
			return this.field_19131.getDisplayName().getFormattedText();
		}

		public ClientResourcePackContainer method_20150() {
			return this.field_19131;
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			ResourcePackCompatibility resourcePackCompatibility = this.method_20147();
			if (!resourcePackCompatibility.isCompatible()) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				DrawableHelper.fill(k - 1, j - 1, k + l - 9, j + m + 1, -8978432);
			}

			this.method_20144();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.blit(k, j, 0.0F, 0.0F, 32, 32, 32, 32);
			String string = this.method_20149();
			String string2 = this.method_20148();
			if (this.method_20151() && (this.field_19128.options.touchscreen || bl)) {
				this.field_19128.getTextureManager().bindTexture(ResourcePackListWidget.RESOURCE_PACKS_LOCATION);
				DrawableHelper.fill(k, j, k + 32, j + 32, -1601138544);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int p = n - k;
				int q = o - j;
				if (!resourcePackCompatibility.isCompatible()) {
					string = ResourcePackListWidget.INCOMPATIBLE.getFormattedText();
					string2 = resourcePackCompatibility.getNotification().getFormattedText();
				}

				if (this.method_20152()) {
					if (p < 32) {
						DrawableHelper.blit(k, j, 0.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.blit(k, j, 0.0F, 0.0F, 32, 32, 256, 256);
					}
				} else {
					if (this.method_20153()) {
						if (p < 16) {
							DrawableHelper.blit(k, j, 32.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.blit(k, j, 32.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.method_20154()) {
						if (p < 32 && p > 16 && q < 16) {
							DrawableHelper.blit(k, j, 96.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.blit(k, j, 96.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.method_20155()) {
						if (p < 32 && p > 16 && q > 16) {
							DrawableHelper.blit(k, j, 64.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.blit(k, j, 64.0F, 0.0F, 32, 32, 256, 256);
						}
					}
				}
			}

			int px = this.field_19128.textRenderer.getStringWidth(string);
			if (px > 157) {
				string = this.field_19128.textRenderer.trimToWidth(string, 157 - this.field_19128.textRenderer.getStringWidth("...")) + "...";
			}

			this.field_19128.textRenderer.drawWithShadow(string, (float)(k + 32 + 2), (float)(j + 1), 16777215);
			List<String> list = this.field_19128.textRenderer.wrapStringToWidthAsList(string2, 157);

			for (int r = 0; r < 2 && r < list.size(); r++) {
				this.field_19128.textRenderer.drawWithShadow((String)list.get(r), (float)(k + 32 + 2), (float)(j + 12 + 10 * r), 8421504);
			}
		}

		protected boolean method_20151() {
			return !this.field_19131.sortsTillEnd() || !this.field_19131.canBeSorted();
		}

		protected boolean method_20152() {
			return !this.field_19129.method_2669(this);
		}

		protected boolean method_20153() {
			return this.field_19129.method_2669(this) && !this.field_19131.canBeSorted();
		}

		protected boolean method_20154() {
			List<ResourcePackListWidget.ResourcePackItem> list = this.field_19130.children();
			int i = list.indexOf(this);
			return i > 0 && !((ResourcePackListWidget.ResourcePackItem)list.get(i - 1)).field_19131.sortsTillEnd();
		}

		protected boolean method_20155() {
			List<ResourcePackListWidget.ResourcePackItem> list = this.field_19130.children();
			int i = list.indexOf(this);
			return i >= 0 && i < list.size() - 1 && !((ResourcePackListWidget.ResourcePackItem)list.get(i + 1)).field_19131.sortsTillEnd();
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			double f = d - (double)this.field_19130.getRowLeft();
			double g = e - (double)this.field_19130.getRowTop(this.field_19130.children().indexOf(this));
			if (this.method_20151() && f <= 32.0) {
				if (this.method_20152()) {
					this.method_20156().method_2660();
					ResourcePackCompatibility resourcePackCompatibility = this.method_20147();
					if (resourcePackCompatibility.isCompatible()) {
						this.method_20156().select(this);
					} else {
						TextComponent textComponent = resourcePackCompatibility.getConfirmMessage();
						this.field_19128.openScreen(new YesNoScreen(bl -> {
							this.field_19128.openScreen(this.method_20156());
							if (bl) {
								this.method_20156().select(this);
							}
						}, ResourcePackListWidget.INCOMPATIBLE_CONFIRM, textComponent));
					}

					return true;
				}

				if (f < 16.0 && this.method_20153()) {
					this.method_20156().remove(this);
					return true;
				}

				if (f > 16.0 && g < 16.0 && this.method_20154()) {
					List<ResourcePackListWidget.ResourcePackItem> list = this.field_19130.children();
					int j = list.indexOf(this);
					list.remove(this);
					list.add(j - 1, this);
					this.method_20156().method_2660();
					return true;
				}

				if (f > 16.0 && g > 16.0 && this.method_20155()) {
					List<ResourcePackListWidget.ResourcePackItem> list = this.field_19130.children();
					int j = list.indexOf(this);
					list.remove(this);
					list.add(j + 1, this);
					this.method_20156().method_2660();
					return true;
				}
			}

			return false;
		}

		public ResourcePackOptionsScreen method_20156() {
			return this.field_19129;
		}
	}
}
