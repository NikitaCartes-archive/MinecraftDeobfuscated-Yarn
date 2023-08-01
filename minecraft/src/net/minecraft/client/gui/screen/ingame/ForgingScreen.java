package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ForgingScreen<T extends ForgingScreenHandler> extends HandledScreen<T> implements ScreenHandlerListener {
	private final Identifier texture;

	public ForgingScreen(T handler, PlayerInventory playerInventory, Text title, Identifier texture) {
		super(handler, playerInventory, title);
		this.texture = texture;
	}

	protected void setup() {
	}

	@Override
	protected void init() {
		super.init();
		this.setup();
		this.handler.addListener(this);
	}

	@Override
	public void removed() {
		super.removed();
		this.handler.removeListener(this);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.renderForeground(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	protected void renderForeground(DrawContext context, int mouseX, int mouseY, float delta) {
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		context.drawTexture(this.texture, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
		this.drawInvalidRecipeArrow(context, this.x, this.y);
	}

	protected abstract void drawInvalidRecipeArrow(DrawContext context, int x, int y);

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
	}
}
