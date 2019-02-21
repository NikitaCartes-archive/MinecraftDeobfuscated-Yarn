package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.text.TextFormat;

@Environment(EnvType.CLIENT)
public abstract class ResourcePackListWidget extends EntryListWidget<ResourcePackListEntry> {
	protected final MinecraftClient field_3166;

	public ResourcePackListWidget(MinecraftClient minecraftClient, int i, int j) {
		super(minecraftClient, i, j, 32, j - 55 + 4, 36);
		this.field_3166 = minecraftClient;
		this.field_2173 = false;
		this.method_1927(true, (int)(9.0F * 1.5F));
	}

	@Override
	protected void method_1940(int i, int j, Tessellator tessellator) {
		String string = TextFormat.field_1073 + "" + TextFormat.field_1067 + this.getTitle();
		this.field_3166
			.textRenderer
			.draw(string, (float)(i + this.width / 2 - this.field_3166.textRenderer.getStringWidth(string) / 2), (float)Math.min(this.y1 + 3, j), 16777215);
	}

	protected abstract String getTitle();

	@Override
	public int getEntryWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.x2 - 6;
	}

	public void addEntry(ResourcePackListEntry resourcePackListEntry) {
		super.addEntry(resourcePackListEntry);
	}
}
