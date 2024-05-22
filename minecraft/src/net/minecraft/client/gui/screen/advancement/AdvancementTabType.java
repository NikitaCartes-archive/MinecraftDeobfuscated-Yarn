package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
enum AdvancementTabType {
	ABOVE(
		new AdvancementTabType.Textures(
			Identifier.ofVanilla("advancements/tab_above_left_selected"),
			Identifier.ofVanilla("advancements/tab_above_middle_selected"),
			Identifier.ofVanilla("advancements/tab_above_right_selected")
		),
		new AdvancementTabType.Textures(
			Identifier.ofVanilla("advancements/tab_above_left"),
			Identifier.ofVanilla("advancements/tab_above_middle"),
			Identifier.ofVanilla("advancements/tab_above_right")
		),
		28,
		32,
		8
	),
	BELOW(
		new AdvancementTabType.Textures(
			Identifier.ofVanilla("advancements/tab_below_left_selected"),
			Identifier.ofVanilla("advancements/tab_below_middle_selected"),
			Identifier.ofVanilla("advancements/tab_below_right_selected")
		),
		new AdvancementTabType.Textures(
			Identifier.ofVanilla("advancements/tab_below_left"),
			Identifier.ofVanilla("advancements/tab_below_middle"),
			Identifier.ofVanilla("advancements/tab_below_right")
		),
		28,
		32,
		8
	),
	LEFT(
		new AdvancementTabType.Textures(
			Identifier.ofVanilla("advancements/tab_left_top_selected"),
			Identifier.ofVanilla("advancements/tab_left_middle_selected"),
			Identifier.ofVanilla("advancements/tab_left_bottom_selected")
		),
		new AdvancementTabType.Textures(
			Identifier.ofVanilla("advancements/tab_left_top"),
			Identifier.ofVanilla("advancements/tab_left_middle"),
			Identifier.ofVanilla("advancements/tab_left_bottom")
		),
		32,
		28,
		5
	),
	RIGHT(
		new AdvancementTabType.Textures(
			Identifier.ofVanilla("advancements/tab_right_top_selected"),
			Identifier.ofVanilla("advancements/tab_right_middle_selected"),
			Identifier.ofVanilla("advancements/tab_right_bottom_selected")
		),
		new AdvancementTabType.Textures(
			Identifier.ofVanilla("advancements/tab_right_top"),
			Identifier.ofVanilla("advancements/tab_right_middle"),
			Identifier.ofVanilla("advancements/tab_right_bottom")
		),
		32,
		28,
		5
	);

	private final AdvancementTabType.Textures selectedTextures;
	private final AdvancementTabType.Textures unselectedTextures;
	private final int width;
	private final int height;
	private final int tabCount;

	private AdvancementTabType(
		final AdvancementTabType.Textures selectedTextures,
		final AdvancementTabType.Textures unselectedTextures,
		final int width,
		final int height,
		final int tabCount
	) {
		this.selectedTextures = selectedTextures;
		this.unselectedTextures = unselectedTextures;
		this.width = width;
		this.height = height;
		this.tabCount = tabCount;
	}

	public int getTabCount() {
		return this.tabCount;
	}

	public void drawBackground(DrawContext context, int x, int y, boolean selected, int index) {
		AdvancementTabType.Textures textures = selected ? this.selectedTextures : this.unselectedTextures;
		Identifier identifier;
		if (index == 0) {
			identifier = textures.first();
		} else if (index == this.tabCount - 1) {
			identifier = textures.last();
		} else {
			identifier = textures.middle();
		}

		context.drawGuiTexture(identifier, x + this.getTabX(index), y + this.getTabY(index), this.width, this.height);
	}

	public void drawIcon(DrawContext context, int x, int y, int index, ItemStack stack) {
		int i = x + this.getTabX(index);
		int j = y + this.getTabY(index);
		switch (this) {
			case ABOVE:
				i += 6;
				j += 9;
				break;
			case BELOW:
				i += 6;
				j += 6;
				break;
			case LEFT:
				i += 10;
				j += 5;
				break;
			case RIGHT:
				i += 6;
				j += 5;
		}

		context.drawItemWithoutEntity(stack, i, j);
	}

	public int getTabX(int index) {
		switch (this) {
			case ABOVE:
				return (this.width + 4) * index;
			case BELOW:
				return (this.width + 4) * index;
			case LEFT:
				return -this.width + 4;
			case RIGHT:
				return 248;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public int getTabY(int index) {
		switch (this) {
			case ABOVE:
				return -this.height + 4;
			case BELOW:
				return 136;
			case LEFT:
				return this.height * index;
			case RIGHT:
				return this.height * index;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public boolean isClickOnTab(int screenX, int screenY, int index, double mouseX, double mouseY) {
		int i = screenX + this.getTabX(index);
		int j = screenY + this.getTabY(index);
		return mouseX > (double)i && mouseX < (double)(i + this.width) && mouseY > (double)j && mouseY < (double)(j + this.height);
	}

	@Environment(EnvType.CLIENT)
	static record Textures(Identifier first, Identifier middle, Identifier last) {
	}
}
