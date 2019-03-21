package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.menu.settings.ResourcePackSettingsScreen;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ResourcePackListEntry extends EntryListWidget.Entry<ResourcePackListEntry> {
	private static final Identifier field_3160 = new Identifier("textures/gui/resource_packs.png");
	private static final TextComponent field_3162 = new TranslatableTextComponent("resourcePack.incompatible");
	private static final TextComponent field_3163 = new TranslatableTextComponent("resourcePack.incompatible.confirm.title");
	protected final MinecraftClient client;
	protected final ResourcePackSettingsScreen field_3164;
	private final ClientResourcePackContainer resourcePack;

	public ResourcePackListEntry(ResourcePackSettingsScreen resourcePackSettingsScreen, ClientResourcePackContainer clientResourcePackContainer) {
		this.field_3164 = resourcePackSettingsScreen;
		this.client = MinecraftClient.getInstance();
		this.resourcePack = clientResourcePackContainer;
	}

	public void addTo(SelectedResourcePackListWidget selectedResourcePackListWidget) {
		this.getResourcePack().getSortingDirection().locate(selectedResourcePackListWidget.getInputListeners(), this, ResourcePackListEntry::getResourcePack, true);
	}

	protected void drawIcon() {
		this.resourcePack.drawIcon(this.client.getTextureManager());
	}

	protected ResourcePackCompatibility getCompatibility() {
		return this.resourcePack.getCompatibility();
	}

	protected String getDescription() {
		return this.resourcePack.getDescription().getFormattedText();
	}

	protected String getDisplayName() {
		return this.resourcePack.getDisplayName().getFormattedText();
	}

	public ClientResourcePackContainer getResourcePack() {
		return this.resourcePack;
	}

	@Override
	public void draw(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.getY();
		int n = this.getX();
		ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
		if (!resourcePackCompatibility.isCompatible()) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawRect(n - 1, m - 1, n + i - 9, m + j + 1, -8978432);
		}

		this.drawIcon();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		DrawableHelper.drawTexturedRect(n, m, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		String string = this.getDisplayName();
		String string2 = this.getDescription();
		if (this.method_2687() && (this.client.options.touchscreen || bl)) {
			this.client.getTextureManager().bindTexture(field_3160);
			DrawableHelper.drawRect(n, m, n + 32, m + 32, -1601138544);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int o = k - n;
			int p = l - m;
			if (!resourcePackCompatibility.isCompatible()) {
				string = field_3162.getFormattedText();
				string2 = resourcePackCompatibility.getNotification().getFormattedText();
			}

			if (this.method_2688()) {
				if (o < 32) {
					DrawableHelper.drawTexturedRect(n, m, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexturedRect(n, m, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			} else {
				if (this.method_2685()) {
					if (o < 16) {
						DrawableHelper.drawTexturedRect(n, m, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexturedRect(n, m, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.method_2682()) {
					if (o < 32 && o > 16 && p < 16) {
						DrawableHelper.drawTexturedRect(n, m, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexturedRect(n, m, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.method_2683()) {
					if (o < 32 && o > 16 && p > 16) {
						DrawableHelper.drawTexturedRect(n, m, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						DrawableHelper.drawTexturedRect(n, m, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}
			}
		}

		int ox = this.client.textRenderer.getStringWidth(string);
		if (ox > 157) {
			string = this.client.textRenderer.trimToWidth(string, 157 - this.client.textRenderer.getStringWidth("...")) + "...";
		}

		this.client.textRenderer.drawWithShadow(string, (float)(n + 32 + 2), (float)(m + 1), 16777215);
		List<String> list = this.client.textRenderer.wrapStringToWidthAsList(string2, 157);

		for (int q = 0; q < 2 && q < list.size(); q++) {
			this.client.textRenderer.drawWithShadow((String)list.get(q), (float)(n + 32 + 2), (float)(m + 12 + 10 * q), 8421504);
		}
	}

	protected boolean method_2687() {
		return !this.resourcePack.sortsTillEnd() || !this.resourcePack.canBeSorted();
	}

	protected boolean method_2688() {
		return !this.field_3164.method_2669(this);
	}

	protected boolean method_2685() {
		return this.field_3164.method_2669(this) && !this.resourcePack.canBeSorted();
	}

	protected boolean method_2682() {
		List<ResourcePackListEntry> list = this.getParent().getInputListeners();
		int i = list.indexOf(this);
		return i > 0 && !((ResourcePackListEntry)list.get(i - 1)).resourcePack.sortsTillEnd();
	}

	protected boolean method_2683() {
		List<ResourcePackListEntry> list = this.getParent().getInputListeners();
		int i = list.indexOf(this);
		return i >= 0 && i < list.size() - 1 && !((ResourcePackListEntry)list.get(i + 1)).resourcePack.sortsTillEnd();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		double f = d - (double)this.getX();
		double g = e - (double)this.getY();
		if (this.method_2687() && f <= 32.0) {
			if (this.method_2688()) {
				this.method_2680().method_2660();
				ResourcePackCompatibility resourcePackCompatibility = this.getCompatibility();
				if (resourcePackCompatibility.isCompatible()) {
					this.method_2680().select(this);
				} else {
					TextComponent textComponent = resourcePackCompatibility.getConfirmMessage();
					this.client.openScreen(new YesNoScreen((bl, ix) -> {
						this.client.openScreen(this.method_2680());
						if (bl) {
							this.method_2680().select(this);
						}
					}, field_3163, textComponent, 0));
				}

				return true;
			}

			if (f < 16.0 && this.method_2685()) {
				this.method_2680().remove(this);
				return true;
			}

			if (f > 16.0 && g < 16.0 && this.method_2682()) {
				List<ResourcePackListEntry> list = this.getParent().getInputListeners();
				int j = list.indexOf(this);
				list.remove(this);
				list.add(j - 1, this);
				this.method_2680().method_2660();
				return true;
			}

			if (f > 16.0 && g > 16.0 && this.method_2683()) {
				List<ResourcePackListEntry> list = this.getParent().getInputListeners();
				int j = list.indexOf(this);
				list.remove(this);
				list.add(j + 1, this);
				this.method_2680().method_2660();
				return true;
			}
		}

		return false;
	}

	public ResourcePackSettingsScreen method_2680() {
		return this.field_3164;
	}
}
