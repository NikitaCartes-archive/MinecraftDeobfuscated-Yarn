package net.minecraft.client.gui.screen.resourcepack;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
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
			.draw(matrixStack, text, (float)(y + this.width / 2 - this.client.textRenderer.getWidth(text) / 2), (float)Math.min(this.top + 3, i), 16777215);
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
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
			if (!resourcePackCompatibility.isCompatible()) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				DrawableHelper.fill(matrices, x - 1, y - 1, x + entryWidth - 9, y + entryHeight + 1, -8978432);
			}

			this.drawIcon();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
			Text text = this.pack.getDisplayName();
			Text text2 = this.pack.getDescription();
			if (this.isMoveable() && (this.client.options.touchscreen || hovered)) {
				this.client.getTextureManager().bindTexture(ResourcePackListWidget.RESOURCE_PACKS_LOCATION);
				DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int i = mouseX - x;
				int j = mouseY - y;
				if (!resourcePackCompatibility.isCompatible()) {
					text = ResourcePackListWidget.INCOMPATIBLE;
					text2 = resourcePackCompatibility.getNotification();
				}

				if (this.isSelectable()) {
					if (i < 32) {
						DrawableHelper.drawTexture(matrices, x, y, 0.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 32, 32, 256, 256);
					}
				} else {
					if (this.isRemovable()) {
						if (i < 16) {
							DrawableHelper.drawTexture(matrices, x, y, 32.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.drawTexture(matrices, x, y, 32.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.canMoveUp()) {
						if (i < 32 && i > 16 && j < 16) {
							DrawableHelper.drawTexture(matrices, x, y, 96.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.drawTexture(matrices, x, y, 96.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.canMoveDown()) {
						if (i < 32 && i > 16 && j > 16) {
							DrawableHelper.drawTexture(matrices, x, y, 64.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.drawTexture(matrices, x, y, 64.0F, 0.0F, 32, 32, 256, 256);
						}
					}
				}
			}

			int ix = this.client.textRenderer.getWidth(text);
			if (ix > 157) {
				class_5348 lv = class_5348.method_29433(
					this.client.textRenderer.trimToWidth(text, 157 - this.client.textRenderer.getWidth("...")), class_5348.method_29430("...")
				);
				this.client.textRenderer.drawWithShadow(matrices, lv, (float)(x + 32 + 2), (float)(y + 1), 16777215);
			} else {
				this.client.textRenderer.drawWithShadow(matrices, text, (float)(x + 32 + 2), (float)(y + 1), 16777215);
			}

			this.client.textRenderer.drawWithShadow(matrices, text, (float)(x + 32 + 2), (float)(y + 1), 16777215);
			List<class_5348> list = this.client.textRenderer.wrapLines(text2, 157);

			for (int k = 0; k < 2 && k < list.size(); k++) {
				this.client.textRenderer.drawWithShadow(matrices, (class_5348)list.get(k), (float)(x + 32 + 2), (float)(y + 12 + 10 * k), 8421504);
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
