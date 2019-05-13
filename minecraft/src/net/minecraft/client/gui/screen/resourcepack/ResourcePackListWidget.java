package net.minecraft.client.gui.screen.resourcepack;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ResourcePackListWidget extends AlwaysSelectedEntryListWidget<ResourcePackListWidget.ResourcePackEntry> {
	private static final Identifier RESOURCE_PACKS_LOCATION = new Identifier("textures/gui/resource_packs.png");
	private static final Component INCOMPATIBLE = new TranslatableComponent("resourcePack.incompatible");
	private static final Component INCOMPATIBLE_CONFIRM = new TranslatableComponent("resourcePack.incompatible.confirm.title");
	protected final MinecraftClient client;
	private final Component title;

	public ResourcePackListWidget(MinecraftClient minecraftClient, int i, int j, Component component) {
		super(minecraftClient, i, j, 32, j - 55 + 4, 36);
		this.client = minecraftClient;
		this.centerListVertically = false;
		this.setRenderHeader(true, (int)(9.0F * 1.5F));
		this.title = component;
	}

	@Override
	protected void renderHeader(int i, int j, Tessellator tessellator) {
		Component component = new TextComponent("").append(this.title).applyFormat(ChatFormat.field_1073, ChatFormat.field_1067);
		this.client
			.textRenderer
			.draw(
				component.getFormattedText(),
				(float)(i + this.width / 2 - this.client.textRenderer.getStringWidth(component.getFormattedText()) / 2),
				(float)Math.min(this.top + 3, j),
				16777215
			);
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.right - 6;
	}

	public void addEntry(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
		this.addEntry(resourcePackEntry);
		resourcePackEntry.widget = this;
	}

	@Environment(EnvType.CLIENT)
	public static class ResourcePackEntry extends AlwaysSelectedEntryListWidget.Entry<ResourcePackListWidget.ResourcePackEntry> {
		private ResourcePackListWidget widget;
		protected final MinecraftClient client;
		protected final ResourcePackOptionsScreen screen;
		private final ClientResourcePackContainer packContainer;

		public ResourcePackEntry(
			ResourcePackListWidget resourcePackListWidget, ResourcePackOptionsScreen resourcePackOptionsScreen, ClientResourcePackContainer clientResourcePackContainer
		) {
			this.screen = resourcePackOptionsScreen;
			this.client = MinecraftClient.getInstance();
			this.packContainer = clientResourcePackContainer;
			this.widget = resourcePackListWidget;
		}

		public void method_20145(SelectedResourcePackListWidget selectedResourcePackListWidget) {
			this.getPackContainer()
				.getInitialPosition()
				.insert(selectedResourcePackListWidget.children(), this, ResourcePackListWidget.ResourcePackEntry::getPackContainer, true);
			this.widget = selectedResourcePackListWidget;
		}

		protected void drawIcon() {
			this.packContainer.drawIcon(this.client.getTextureManager());
		}

		protected ResourcePackCompatibility getCompatibility() {
			return this.packContainer.getCompatibility();
		}

		protected String getDescription() {
			return this.packContainer.getDescription().getFormattedText();
		}

		protected String getDisplayName() {
			return this.packContainer.getDisplayName().getFormattedText();
		}

		public ClientResourcePackContainer getPackContainer() {
			return this.packContainer;
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
			if (!resourcePackCompatibility.isCompatible()) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				DrawableHelper.fill(k - 1, j - 1, k + l - 9, j + m + 1, -8978432);
			}

			this.drawIcon();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.blit(k, j, 0.0F, 0.0F, 32, 32, 32, 32);
			String string = this.getDisplayName();
			String string2 = this.getDescription();
			if (this.method_20151() && (this.client.options.touchscreen || bl)) {
				this.client.getTextureManager().bindTexture(ResourcePackListWidget.RESOURCE_PACKS_LOCATION);
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

					if (this.canSortUp()) {
						if (p < 32 && p > 16 && q < 16) {
							DrawableHelper.blit(k, j, 96.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.blit(k, j, 96.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.canSortDown()) {
						if (p < 32 && p > 16 && q > 16) {
							DrawableHelper.blit(k, j, 64.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.blit(k, j, 64.0F, 0.0F, 32, 32, 256, 256);
						}
					}
				}
			}

			int px = this.client.textRenderer.getStringWidth(string);
			if (px > 157) {
				string = this.client.textRenderer.trimToWidth(string, 157 - this.client.textRenderer.getStringWidth("...")) + "...";
			}

			this.client.textRenderer.drawWithShadow(string, (float)(k + 32 + 2), (float)(j + 1), 16777215);
			List<String> list = this.client.textRenderer.wrapStringToWidthAsList(string2, 157);

			for (int r = 0; r < 2 && r < list.size(); r++) {
				this.client.textRenderer.drawWithShadow((String)list.get(r), (float)(k + 32 + 2), (float)(j + 12 + 10 * r), 8421504);
			}
		}

		protected boolean method_20151() {
			return !this.packContainer.isPositionFixed() || !this.packContainer.canBeSorted();
		}

		protected boolean method_20152() {
			return !this.screen.method_2669(this);
		}

		protected boolean method_20153() {
			return this.screen.method_2669(this) && !this.packContainer.canBeSorted();
		}

		protected boolean canSortUp() {
			List<ResourcePackListWidget.ResourcePackEntry> list = this.widget.children();
			int i = list.indexOf(this);
			return i > 0 && !((ResourcePackListWidget.ResourcePackEntry)list.get(i - 1)).packContainer.isPositionFixed();
		}

		protected boolean canSortDown() {
			List<ResourcePackListWidget.ResourcePackEntry> list = this.widget.children();
			int i = list.indexOf(this);
			return i >= 0 && i < list.size() - 1 && !((ResourcePackListWidget.ResourcePackEntry)list.get(i + 1)).packContainer.isPositionFixed();
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			double f = d - (double)this.widget.getRowLeft();
			double g = e - (double)this.widget.getRowTop(this.widget.children().indexOf(this));
			if (this.method_20151() && f <= 32.0) {
				if (this.method_20152()) {
					this.getScreen().setEdited();
					ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
					if (resourcePackCompatibility.isCompatible()) {
						this.getScreen().method_2674(this);
					} else {
						Component component = resourcePackCompatibility.getConfirmMessage();
						this.client.method_1507(new ConfirmScreen(bl -> {
							this.client.method_1507(this.getScreen());
							if (bl) {
								this.getScreen().method_2674(this);
							}
						}, ResourcePackListWidget.INCOMPATIBLE_CONFIRM, component));
					}

					return true;
				}

				if (f < 16.0 && this.method_20153()) {
					this.getScreen().method_2663(this);
					return true;
				}

				if (f > 16.0 && g < 16.0 && this.canSortUp()) {
					List<ResourcePackListWidget.ResourcePackEntry> list = this.widget.children();
					int j = list.indexOf(this);
					list.remove(this);
					list.add(j - 1, this);
					this.getScreen().setEdited();
					return true;
				}

				if (f > 16.0 && g > 16.0 && this.canSortDown()) {
					List<ResourcePackListWidget.ResourcePackEntry> list = this.widget.children();
					int j = list.indexOf(this);
					list.remove(this);
					list.add(j + 1, this);
					this.getScreen().setEdited();
					return true;
				}
			}

			return false;
		}

		public ResourcePackOptionsScreen getScreen() {
			return this.screen;
		}
	}
}
