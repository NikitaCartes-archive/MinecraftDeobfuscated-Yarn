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
import net.minecraft.client.util.math.MatrixStack;
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
	protected void renderHeader(MatrixStack matrixStack, int y, int i, Tessellator tessellator) {
		Text text = new LiteralText("").append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
		this.client
			.textRenderer
			.draw(matrixStack, text, (float)(y + this.width / 2 - this.client.textRenderer.getStringWidth(text) / 2), (float)Math.min(this.top + 3, i), 16777215);
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarPositionX() {
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
			this.method_24232(list);
		}

		public void method_24232(SelectedResourcePackListWidget selectedResourcePackListWidget) {
			this.resourcePackList = selectedResourcePackListWidget;
		}

		protected void drawIcon() {
			this.pack.drawIcon(this.client.getTextureManager());
		}

		protected ResourcePackCompatibility getCompatibility() {
			return this.pack.getCompatibility();
		}

		public ClientResourcePackProfile getPack() {
			return this.pack;
		}

		@Override
		public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
			ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
			if (!resourcePackCompatibility.isCompatible()) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				DrawableHelper.fill(matrices, width - 1, y - 1, width + height - 9, y + mouseX + 1, -8978432);
			}

			this.drawIcon();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(matrices, width, y, 0.0F, 0.0F, 32, 32, 32, 32);
			Text text = this.pack.getDisplayName();
			Text text2 = this.pack.getDescription();
			if (this.isMoveable() && (this.client.options.touchscreen || bl)) {
				this.client.getTextureManager().bindTexture(ResourcePackListWidget.RESOURCE_PACKS_LOCATION);
				DrawableHelper.fill(matrices, width, y, width + 32, y + 32, -1601138544);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int j = mouseY - width;
				int k = i - y;
				if (!resourcePackCompatibility.isCompatible()) {
					text = ResourcePackListWidget.INCOMPATIBLE;
					text2 = resourcePackCompatibility.getNotification();
				}

				if (this.isSelectable()) {
					if (j < 32) {
						DrawableHelper.drawTexture(matrices, width, y, 0.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.drawTexture(matrices, width, y, 0.0F, 0.0F, 32, 32, 256, 256);
					}
				} else {
					if (this.isRemovable()) {
						if (j < 16) {
							DrawableHelper.drawTexture(matrices, width, y, 32.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.drawTexture(matrices, width, y, 32.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.canMoveUp()) {
						if (j < 32 && j > 16 && k < 16) {
							DrawableHelper.drawTexture(matrices, width, y, 96.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.drawTexture(matrices, width, y, 96.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.canMoveDown()) {
						if (j < 32 && j > 16 && k > 16) {
							DrawableHelper.drawTexture(matrices, width, y, 64.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.drawTexture(matrices, width, y, 64.0F, 0.0F, 32, 32, 256, 256);
						}
					}
				}
			}

			int jx = this.client.textRenderer.getStringWidth(text);
			if (jx > 157) {
				Text text3 = this.client.textRenderer.trimToWidth(text, 157 - this.client.textRenderer.getWidth("...")).append("...");
				this.client.textRenderer.drawWithShadow(matrices, text3, (float)(width + 32 + 2), (float)(y + 1), 16777215);
			} else {
				this.client.textRenderer.drawWithShadow(matrices, text, (float)(width + 32 + 2), (float)(y + 1), 16777215);
			}

			this.client.textRenderer.drawWithShadow(matrices, text, (float)(width + 32 + 2), (float)(y + 1), 16777215);
			List<Text> list = this.client.textRenderer.wrapLines(text2, 157);

			for (int l = 0; l < 2 && l < list.size(); l++) {
				this.client.textRenderer.drawWithShadow(matrices, (Text)list.get(l), (float)(width + 32 + 2), (float)(y + 12 + 10 * l), 8421504);
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
					list.remove(i);
					list.add(i - 1, this);
					this.getScreen().markDirty();
					return true;
				}

				if (d > 16.0 && e > 16.0 && this.canMoveDown()) {
					List<ResourcePackListWidget.ResourcePackEntry> list = this.resourcePackList.children();
					int i = list.indexOf(this);
					list.remove(i);
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
