package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AnvilScreen extends ForgingScreen<AnvilScreenHandler> {
	private static final Identifier TEXT_FIELD_TEXTURE = Identifier.ofVanilla("container/anvil/text_field");
	private static final Identifier TEXT_FIELD_DISABLED_TEXTURE = Identifier.ofVanilla("container/anvil/text_field_disabled");
	private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/anvil/error");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/anvil.png");
	private static final Text TOO_EXPENSIVE_TEXT = Text.translatable("container.repair.expensive");
	private TextFieldWidget nameField;
	private final PlayerEntity player;

	public AnvilScreen(AnvilScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title, TEXTURE);
		this.player = inventory.player;
		this.titleX = 60;
	}

	@Override
	protected void setup() {
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.nameField = new TextFieldWidget(this.textRenderer, i + 62, j + 24, 103, 12, Text.translatable("container.repair"));
		this.nameField.setFocusUnlocked(false);
		this.nameField.setEditableColor(-1);
		this.nameField.setUneditableColor(-1);
		this.nameField.setDrawsBackground(false);
		this.nameField.setMaxLength(50);
		this.nameField.setChangedListener(this::onRenamed);
		this.nameField.setText("");
		this.addSelectableChild(this.nameField);
		this.nameField.setEditable(this.handler.getSlot(0).hasStack());
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.nameField);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.nameField.getText();
		this.init(client, width, height);
		this.nameField.setText(string);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.player.closeHandledScreen();
		}

		return !this.nameField.keyPressed(keyCode, scanCode, modifiers) && !this.nameField.isActive() ? super.keyPressed(keyCode, scanCode, modifiers) : true;
	}

	private void onRenamed(String name) {
		Slot slot = this.handler.getSlot(0);
		if (slot.hasStack()) {
			String string = name;
			if (!slot.getStack().contains(DataComponentTypes.CUSTOM_NAME) && name.equals(slot.getStack().getName().getString())) {
				string = "";
			}

			if (this.handler.setNewItemName(string)) {
				this.client.player.networkHandler.sendPacket(new RenameItemC2SPacket(string));
			}
		}
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		super.drawForeground(context, mouseX, mouseY);
		int i = this.handler.getLevelCost();
		if (i > 0) {
			int j = 8453920;
			Text text;
			if (i >= 40 && !this.client.player.getAbilities().creativeMode) {
				text = TOO_EXPENSIVE_TEXT;
				j = 16736352;
			} else if (!this.handler.getSlot(2).hasStack()) {
				text = null;
			} else {
				text = Text.translatable("container.repair.cost", i);
				if (!this.handler.getSlot(2).canTakeItems(this.player)) {
					j = 16736352;
				}
			}

			if (text != null) {
				int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
				int l = 69;
				context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
				context.drawTextWithShadow(this.textRenderer, text, k, 69, j);
			}
		}
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		super.drawBackground(context, delta, mouseX, mouseY);
		context.drawGuiTexture(this.handler.getSlot(0).hasStack() ? TEXT_FIELD_TEXTURE : TEXT_FIELD_DISABLED_TEXTURE, this.x + 59, this.y + 20, 110, 16);
	}

	@Override
	public void renderForeground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.nameField.render(context, mouseX, mouseY, delta);
	}

	@Override
	protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
		if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack()) {
			context.drawGuiTexture(ERROR_TEXTURE, x + 99, y + 45, 28, 21);
		}
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
		if (slotId == 0) {
			this.nameField.setText(stack.isEmpty() ? "" : stack.getName().getString());
			this.nameField.setEditable(!stack.isEmpty());
			this.setFocused(this.nameField);
		}
	}
}
