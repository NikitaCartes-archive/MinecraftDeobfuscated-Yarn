package net.minecraft.client.gui.screen.resourcepack;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.class_5369;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ResourcePackListWidget extends AlwaysSelectedEntryListWidget<ResourcePackListWidget.ResourcePackEntry> {
	private static final Identifier RESOURCE_PACKS_LOCATION = new Identifier("textures/gui/resource_packs.png");
	private static final Text INCOMPATIBLE = new TranslatableText("pack.incompatible");
	private static final Text INCOMPATIBLE_CONFIRM = new TranslatableText("pack.incompatible.confirm.title");
	private final Text title;

	public ResourcePackListWidget(MinecraftClient client, int width, int height, Text title) {
		super(client, width, height, 32, height - 55 + 4, 36);
		this.title = title;
		this.centerListVertically = false;
		this.setRenderHeader(true, (int)(9.0F * 1.5F));
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

	@Environment(EnvType.CLIENT)
	public static class ResourcePackEntry extends AlwaysSelectedEntryListWidget.Entry<ResourcePackListWidget.ResourcePackEntry> {
		private ResourcePackListWidget resourcePackList;
		protected final MinecraftClient client;
		protected final Screen field_25476;
		private final class_5369.class_5371 screen;

		public ResourcePackEntry(MinecraftClient minecraftClient, ResourcePackListWidget resourcePackListWidget, Screen screen, class_5369.class_5371 arg) {
			this.client = minecraftClient;
			this.field_25476 = screen;
			this.screen = arg;
			this.resourcePackList = resourcePackListWidget;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			ResourcePackCompatibility resourcePackCompatibility = this.screen.method_29648();
			if (!resourcePackCompatibility.isCompatible()) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				DrawableHelper.fill(matrices, x - 1, y - 1, x + entryWidth - 9, y + entryHeight + 1, -8978432);
			}

			this.screen.method_29649(this.client.getTextureManager());
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
			Text text = this.screen.method_29650();
			class_5348 lv = this.screen.method_29653();
			if (this.isSelectable() && (this.client.options.touchscreen || hovered)) {
				this.client.getTextureManager().bindTexture(ResourcePackListWidget.RESOURCE_PACKS_LOCATION);
				DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int i = mouseX - x;
				int j = mouseY - y;
				if (!resourcePackCompatibility.isCompatible()) {
					text = ResourcePackListWidget.INCOMPATIBLE;
					lv = resourcePackCompatibility.getNotification();
				}

				if (this.screen.method_29661()) {
					if (i < 32) {
						DrawableHelper.drawTexture(matrices, x, y, 0.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 32, 32, 256, 256);
					}
				} else {
					if (this.screen.method_29662()) {
						if (i < 16) {
							DrawableHelper.drawTexture(matrices, x, y, 32.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.drawTexture(matrices, x, y, 32.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.screen.method_29663()) {
						if (i < 32 && i > 16 && j < 16) {
							DrawableHelper.drawTexture(matrices, x, y, 96.0F, 32.0F, 32, 32, 256, 256);
						} else {
							DrawableHelper.drawTexture(matrices, x, y, 96.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.screen.method_29664()) {
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
				class_5348 lv2 = class_5348.method_29433(
					this.client.textRenderer.trimToWidth(text, 157 - this.client.textRenderer.getWidth("...")), class_5348.method_29430("...")
				);
				this.client.textRenderer.drawWithShadow(matrices, lv2, (float)(x + 32 + 2), (float)(y + 1), 16777215);
			} else {
				this.client.textRenderer.drawWithShadow(matrices, text, (float)(x + 32 + 2), (float)(y + 1), 16777215);
			}

			this.client.textRenderer.drawWithShadow(matrices, text, (float)(x + 32 + 2), (float)(y + 1), 16777215);
			List<class_5348> list = this.client.textRenderer.wrapLines(lv, 157);

			for (int k = 0; k < 2 && k < list.size(); k++) {
				this.client.textRenderer.drawWithShadow(matrices, (class_5348)list.get(k), (float)(x + 32 + 2), (float)(y + 12 + 10 * k), 8421504);
			}
		}

		private boolean isSelectable() {
			return !this.screen.method_29654() || !this.screen.method_29655();
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d = mouseX - (double)this.resourcePackList.getRowLeft();
			double e = mouseY - (double)this.resourcePackList.getRowTop(this.resourcePackList.children().indexOf(this));
			if (this.isSelectable() && d <= 32.0) {
				if (this.screen.method_29661()) {
					ResourcePackCompatibility resourcePackCompatibility = this.screen.method_29648();
					if (resourcePackCompatibility.isCompatible()) {
						this.screen.method_29656();
					} else {
						Text text = resourcePackCompatibility.getConfirmMessage();
						this.client.openScreen(new ConfirmScreen(bl -> {
							this.client.openScreen(this.field_25476);
							if (bl) {
								this.screen.method_29656();
							}
						}, ResourcePackListWidget.INCOMPATIBLE_CONFIRM, text));
					}

					return true;
				}

				if (d < 16.0 && this.screen.method_29662()) {
					this.screen.method_29657();
					return true;
				}

				if (d > 16.0 && e < 16.0 && this.screen.method_29663()) {
					this.screen.method_29658();
					return true;
				}

				if (d > 16.0 && e > 16.0 && this.screen.method_29664()) {
					this.screen.method_29659();
					return true;
				}
			}

			return false;
		}
	}
}
