package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;

@Environment(EnvType.CLIENT)
public abstract class ResourcePackListWidget extends EntryListWidget<ResourcePackListEntry> {
	private final MinecraftClient field_3166;
	private final TextComponent field_18978;

	public ResourcePackListWidget(MinecraftClient minecraftClient, int i, int j, TextComponent textComponent) {
		super(minecraftClient, i, j, 32, j - 55 + 4, 36);
		this.field_3166 = minecraftClient;
		this.centerListVertically = false;
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
				(float)Math.min(this.y + 3, j),
				16777215
			);
	}

	@Override
	public int getEntryWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.right - 6;
	}

	public void addEntry(ResourcePackListEntry resourcePackListEntry) {
		super.addEntry(resourcePackListEntry);
	}
}
