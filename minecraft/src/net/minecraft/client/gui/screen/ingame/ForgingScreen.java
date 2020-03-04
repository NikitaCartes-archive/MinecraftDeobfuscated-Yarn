package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ForgingScreen<T extends ForgingScreenHandler> extends ScreenWithHandler<T> implements ScreenHandlerListener {
	private Identifier texture;

	public ForgingScreen(T forgingScreenHandler, PlayerInventory playerInventory, Text text, Identifier identifier) {
		super(forgingScreenHandler, playerInventory, text);
		this.texture = identifier;
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
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		super.render(mouseX, mouseY, delta);
		RenderSystem.disableBlend();
		this.renderForeground(mouseX, mouseY, delta);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	protected void renderForeground(int mouseX, int mouseY, float delta) {
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(this.texture);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.blit(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		this.blit(i + 59, j + 20, 0, this.backgroundHeight + (this.handler.getSlot(0).hasStack() ? 0 : 16), 110, 16);
		if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(2).hasStack()) {
			this.blit(i + 99, j + 45, this.backgroundWidth, 0, 28, 21);
		}
	}

	@Override
	public void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> defaultedList) {
		this.onSlotUpdate(handler, 0, handler.getSlot(0).getStack());
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int propertyId, int i) {
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack itemStack) {
	}
}
