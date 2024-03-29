package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HorseScreen extends HandledScreen<HorseScreenHandler> {
	private static final Identifier CHEST_SLOTS_TEXTURE = new Identifier("container/horse/chest_slots");
	private static final Identifier SADDLE_SLOT_TEXTURE = new Identifier("container/horse/saddle_slot");
	private static final Identifier LLAMA_ARMOR_SLOT_TEXTURE = new Identifier("container/horse/llama_armor_slot");
	private static final Identifier ARMOR_SLOT_TEXTURE = new Identifier("container/horse/armor_slot");
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
	private final AbstractHorseEntity entity;
	private float mouseX;
	private float mouseY;

	public HorseScreen(HorseScreenHandler handler, PlayerInventory inventory, AbstractHorseEntity entity) {
		super(handler, inventory, entity.getDisplayName());
		this.entity = entity;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		AbstractHorseEntity var8 = this.entity;
		if (var8 instanceof AbstractDonkeyEntity abstractDonkeyEntity && abstractDonkeyEntity.hasChest()) {
			context.drawGuiTexture(CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, i + 79, j + 17, abstractDonkeyEntity.getInventoryColumns() * 18, 54);
		}

		if (this.entity.canBeSaddled()) {
			context.drawGuiTexture(SADDLE_SLOT_TEXTURE, i + 7, j + 35 - 18, 18, 18);
		}

		if (this.entity.hasArmorSlot()) {
			if (this.entity instanceof LlamaEntity) {
				context.drawGuiTexture(LLAMA_ARMOR_SLOT_TEXTURE, i + 7, j + 35, 18, 18);
			} else {
				context.drawGuiTexture(ARMOR_SLOT_TEXTURE, i + 7, j + 35, 18, 18);
			}
		}

		InventoryScreen.drawEntity(context, i + 26, j + 18, i + 78, j + 70, 17, 0.25F, this.mouseX, this.mouseY, this.entity);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.mouseX = (float)mouseX;
		this.mouseY = (float)mouseY;
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}
}
