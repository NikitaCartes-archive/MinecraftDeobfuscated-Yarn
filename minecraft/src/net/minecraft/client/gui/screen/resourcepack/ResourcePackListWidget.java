package net.minecraft.client.gui.screen.resourcepack;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ResourcePackListWidget extends AlwaysSelectedEntryListWidget<ResourcePackListWidget.ResourcePackEntry> {
	private static final Identifier RESOURCE_PACKS_LOCATION = new Identifier("textures/gui/resource_packs.png");
	private static final Text INCOMPATIBLE = new TranslatableText("resourcePack.incompatible");
	private static final Text INCOMPATIBLE_CONFIRM = new TranslatableText("resourcePack.incompatible.confirm.title");
	protected final MinecraftClient client;
	private final Text title;

	public ResourcePackListWidget(MinecraftClient client, int width, int height, Text title) {
		super(client, width, height, 32, height - 55 + 4, 36);
		this.client = client;
		this.centerListVertically = false;
		this.setRenderHeader(true, (int)(9.0F * 1.5F));
		this.title = title;
	}

	@Override
	protected void renderHeader(int i, int j, Tessellator tessellator) {
		Text text = new LiteralText("").append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
		this.client
			.textRenderer
			.draw(
				text.asFormattedString(),
				(float)(i + this.width / 2 - this.client.textRenderer.getStringWidth(text.asFormattedString()) / 2),
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

	public void add(ResourcePackListWidget.ResourcePackEntry entry) {
		this.addEntry(entry);
		entry.resourcePackList = this;
	}

	@Environment(EnvType.CLIENT)
	public static class ResourcePackEntry extends AlwaysSelectedEntryListWidget.Entry<ResourcePackListWidget.ResourcePackEntry> {
		private ResourcePackListWidget resourcePackList;
		protected final MinecraftClient client;
		protected final ResourcePackOptionsScreen screen;
		private final ClientResourcePackProfile pack;

		public ResourcePackEntry(ResourcePackListWidget listWidget, ResourcePackOptionsScreen screen, ClientResourcePackProfile pack) {
			this.screen = screen;
			this.client = MinecraftClient.getInstance();
			this.pack = pack;
			this.resourcePackList = listWidget;
		}

		public void enable(SelectedResourcePackListWidget list) {
			this.getPack().getInitialPosition().insert(list.children(), this, ResourcePackListWidget.ResourcePackEntry::getPack, true);
			this.resourcePackList = list;
		}

		protected void drawIcon() {
			this.pack.drawIcon(this.client.getTextureManager());
		}

		protected ResourcePackCompatibility getCompatibility() {
			return this.pack.getCompatibility();
		}

		protected String getDescription() {
			return this.pack.getDescription().asFormattedString();
		}

		protected String getDisplayName() {
			return this.pack.getDisplayName().asFormattedString();
		}

		public ClientResourcePackProfile getPack() {
			return this.pack;
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
			if (!resourcePackCompatibility.isCompatible()) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				DrawableHelper.fill(k - 1, j - 1, k + l - 9, j + m + 1, -8978432);
			}

			this.drawIcon();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.blit(k, j, 0.0F, 0.0F, 32, 32, 32, 32);
			String string = this.getDisplayName();
			String string2 = this.getDescription();
			if (this.isMoveable() && (this.client.options.touchscreen || bl)) {
				this.client.getTextureManager().bindTexture(ResourcePackListWidget.RESOURCE_PACKS_LOCATION);
				DrawableHelper.fill(k, j, k + 32, j + 32, -1601138544);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int p = n - k;
				int q = o - j;
				if (!resourcePackCompatibility.isCompatible()) {
					string = ResourcePackListWidget.INCOMPATIBLE.asFormattedString();
					string2 = resourcePackCompatibility.getNotification().asFormattedString();
				}

				if (this.isSelectable()) {
					if (p < 32) {
						DrawableHelper.blit(k, j, 0.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.blit(k, j, 0.0F, 0.0F, 32, 32, 256, 256);
					}
				} else {
					if (this.isRemovable()) {
						if (p < 16) {
							DrawableHelper.blit(k, j, 32.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.blit(k, j, 32.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.canMoveUp()) {
						if (p < 32 && p > 16 && q < 16) {
							DrawableHelper.blit(k, j, 96.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.blit(k, j, 96.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.canMoveDown()) {
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

		protected boolean isMoveable() {
			return !this.pack.isPinned() || !this.pack.isAlwaysEnabled();
		}

		protected boolean isSelectable() {
			return !this.screen.isEnabled(this);
		}

		protected boolean isRemovable() {
			return this.screen.isEnabled(this) && !this.pack.isAlwaysEnabled();
		}

		protected boolean canMoveUp() {
			List<ResourcePackListWidget.ResourcePackEntry> list = this.resourcePackList.children();
			int i = list.indexOf(this);
			return i > 0 && !((ResourcePackListWidget.ResourcePackEntry)list.get(i - 1)).pack.isPinned();
		}

		protected boolean canMoveDown() {
			List<ResourcePackListWidget.ResourcePackEntry> list = this.resourcePackList.children();
			int i = list.indexOf(this);
			return i >= 0 && i < list.size() - 1 && !((ResourcePackListWidget.ResourcePackEntry)list.get(i + 1)).pack.isPinned();
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d = mouseX - (double)this.resourcePackList.getRowLeft();
			double e = mouseY - (double)this.resourcePackList.getRowTop(this.resourcePackList.children().indexOf(this));
			if (this.isMoveable() && d <= 32.0) {
				if (this.isSelectable()) {
					this.getScreen().markDirty();
					ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
					if (resourcePackCompatibility.isCompatible()) {
						this.getScreen().enable(this);
					} else {
						Text text = resourcePackCompatibility.getConfirmMessage();
						this.client.openScreen(new ConfirmScreen(bl -> {
							this.client.openScreen(this.getScreen());
							if (bl) {
								this.getScreen().enable(this);
							}
						}, ResourcePackListWidget.INCOMPATIBLE_CONFIRM, text));
					}

					return true;
				}

				if (d < 16.0 && this.isRemovable()) {
					this.getScreen().disable(this);
					return true;
				}

				if (d > 16.0 && e < 16.0 && this.canMoveUp()) {
					List<ResourcePackListWidget.ResourcePackEntry> list = this.resourcePackList.children();
					int i = list.indexOf(this);
					list.remove(this);
					list.add(i - 1, this);
					this.getScreen().markDirty();
					return true;
				}

				if (d > 16.0 && e > 16.0 && this.canMoveDown()) {
					List<ResourcePackListWidget.ResourcePackEntry> list = this.resourcePackList.children();
					int i = list.indexOf(this);
					list.remove(this);
					list.add(i + 1, this);
					this.getScreen().markDirty();
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
