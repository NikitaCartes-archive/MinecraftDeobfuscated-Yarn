package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class SmithingScreen extends ForgingScreen<SmithingScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/smithing.png");
	private static final Identifier EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM_TEXTURE = new Identifier("item/empty_slot_smithing_template_armor_trim");
	private static final Identifier EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE_TEXTURE = new Identifier("item/empty_slot_smithing_template_netherite_upgrade");
	private static final Text MISSING_TEMPLATE_TOOLTIP = Text.translatable("container.upgrade.missing_template_tooltip");
	private static final Text ERROR_TOOLTIP = Text.translatable("container.upgrade.error_tooltip");
	private static final List<Identifier> EMPTY_SLOT_TEXTURES = List.of(
		EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM_TEXTURE, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE_TEXTURE
	);
	private static final int field_42057 = 44;
	private static final int field_42058 = 22;
	private static final int field_42059 = 28;
	private static final int field_42060 = 21;
	private static final int field_42061 = 95;
	private static final int field_42062 = 45;
	private static final int field_42063 = 115;
	public static final int field_42068 = 210;
	public static final int field_42047 = 25;
	public static final Quaternionf ARMOR_STAND_ROTATION = new Quaternionf().rotationXYZ(0.43633232F, 0.0F, (float) Math.PI);
	public static final int field_42049 = 25;
	public static final int field_42050 = 65;
	public static final int field_42051 = 141;
	private final CyclingSlotIcon templateSlotIcon = new CyclingSlotIcon(0);
	private final CyclingSlotIcon baseSlotIcon = new CyclingSlotIcon(1);
	private final CyclingSlotIcon additionsSlotIcon = new CyclingSlotIcon(2);
	@Nullable
	private ArmorStandEntity armorStand;

	public SmithingScreen(SmithingScreenHandler handler, PlayerInventory playerInventory, Text title) {
		super(handler, playerInventory, title, TEXTURE);
		this.titleX = 44;
		this.titleY = 22;
	}

	@Override
	protected void setup() {
		this.armorStand = new ArmorStandEntity(this.client.world, 0.0, 0.0, 0.0);
		this.armorStand.setHideBasePlate(true);
		this.armorStand.setShowArms(true);
		this.armorStand.bodyYaw = 210.0F;
		this.armorStand.setPitch(25.0F);
		this.armorStand.headYaw = this.armorStand.getYaw();
		this.armorStand.prevHeadYaw = this.armorStand.getYaw();
	}

	@Override
	public void handledScreenTick() {
		super.handledScreenTick();
		Optional<SmithingTemplateItem> optional = this.getSmithingTemplate();
		this.templateSlotIcon.updateTexture(EMPTY_SLOT_TEXTURES);
		this.baseSlotIcon.updateTexture((List<Identifier>)optional.map(SmithingTemplateItem::getEmptyBaseSlotTextures).orElse(List.of()));
		this.additionsSlotIcon.updateTexture((List<Identifier>)optional.map(SmithingTemplateItem::getEmptyAdditionsSlotTextures).orElse(List.of()));
	}

	private Optional<SmithingTemplateItem> getSmithingTemplate() {
		ItemStack itemStack = this.handler.getSlot(0).getStack();
		return !itemStack.isEmpty() && itemStack.getItem() instanceof SmithingTemplateItem smithingTemplateItem
			? Optional.of(smithingTemplateItem)
			: Optional.empty();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		this.renderSlotTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		super.drawBackground(matrices, delta, mouseX, mouseY);
		this.templateSlotIcon.render(this.handler, matrices, delta, this.x, this.y);
		this.baseSlotIcon.render(this.handler, matrices, delta, this.x, this.y);
		this.additionsSlotIcon.render(this.handler, matrices, delta, this.x, this.y);
		InventoryScreen.drawEntity(this.x + 141, this.y + 65, 25, ARMOR_STAND_ROTATION, null, this.armorStand);
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
		if (slotId == 3 && this.armorStand != null) {
			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				this.armorStand.equipStack(equipmentSlot, ItemStack.EMPTY);
			}

			if (!stack.isEmpty()) {
				ItemStack itemStack = stack.copy();
				if (stack.getItem() instanceof ArmorItem armorItem) {
					this.armorStand.equipStack(armorItem.getSlotType(), itemStack);
				} else {
					this.armorStand.equipStack(EquipmentSlot.OFFHAND, itemStack);
				}
			}
		}
	}

	@Override
	protected void drawInvalidRecipeArrow(MatrixStack matrices, int x, int y) {
		if (this.hasInvalidRecipe()) {
			this.drawTexture(matrices, x + 95, y + 45, this.backgroundWidth, 0, 28, 21);
		}
	}

	private void renderSlotTooltip(MatrixStack matrices, int mouseX, int mouseY) {
		Optional<Text> optional = Optional.empty();
		if (this.hasInvalidRecipe() && this.isPointWithinBounds(95, 45, 28, 21, (double)mouseX, (double)mouseY)) {
			optional = Optional.of(ERROR_TOOLTIP);
		}

		if (this.focusedSlot != null) {
			ItemStack itemStack = this.handler.getSlot(0).getStack();
			ItemStack itemStack2 = this.focusedSlot.getStack();
			if (itemStack.isEmpty()) {
				if (this.focusedSlot.id == 0) {
					optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
				}
			} else if (itemStack.getItem() instanceof SmithingTemplateItem smithingTemplateItem && itemStack2.isEmpty()) {
				if (this.focusedSlot.id == 1) {
					optional = Optional.of(smithingTemplateItem.getBaseSlotDescription());
				} else if (this.focusedSlot.id == 2) {
					optional = Optional.of(smithingTemplateItem.getAdditionsSlotDescription());
				}
			}
		}

		optional.ifPresent(text -> this.renderOrderedTooltip(matrices, this.textRenderer.wrapLines(text, 115), mouseX, mouseY));
	}

	private boolean hasInvalidRecipe() {
		return this.handler.getSlot(0).hasStack()
			&& this.handler.getSlot(1).hasStack()
			&& this.handler.getSlot(2).hasStack()
			&& !this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack();
	}
}
