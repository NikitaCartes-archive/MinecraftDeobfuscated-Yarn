package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PotatoRefineryScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PotatoRefineryScreen extends HandledScreen<PotatoRefineryScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/potato_refinery.png");
	private static final Identifier LIT_PROGRESS_TEXTURE = new Identifier("container/potato_refinery/lit_progress");
	private static final Identifier BURN_PROGRESS_TEXTURE = new Identifier("container/potato_refinery/burn_progress");
	private boolean narrow;
	private final Identifier background;
	private final Identifier litProgressTexture;
	private final Identifier burnProgressTexture;

	public PotatoRefineryScreen(PotatoRefineryScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundHeight += 20;
		this.playerInventoryTitleY += 20;
		this.background = TEXTURE;
		this.litProgressTexture = LIT_PROGRESS_TEXTURE;
		this.burnProgressTexture = BURN_PROGRESS_TEXTURE;
	}

	@Override
	public void init() {
		super.init();
		this.narrow = this.width < 379;
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
	}

	@Override
	public void handledScreenTick() {
		super.handledScreenTick();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		context.drawTexture(this.background, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		if (this.handler.isBurning()) {
			int k = 17;
			int l = 12;
			int m = MathHelper.ceil(this.handler.getFuelProgress() * 11.0F) + 1;
			context.drawGuiTexture(this.litProgressTexture, 17, 12, 0, 12 - m, i + 51, j + 54 + 12 - m, 17, m);
		}

		int k = 46;
		int l = MathHelper.ceil(this.handler.getRefiningProgress() * 46.0F);
		context.drawGuiTexture(this.burnProgressTexture, 46, 16, 0, 0, i + 69, j + 18, l, 16);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		super.onMouseClick(slot, slotId, button, actionType);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return super.charTyped(chr, modifiers);
	}
}
