package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PlayerInventoryScreen extends AbstractPlayerInventoryScreen<PlayerContainer> implements RecipeBookProvider {
	private static final Identifier field_2933 = new Identifier("textures/gui/recipe_button.png");
	private float mouseX;
	private float mouseY;
	private final RecipeBookGui field_2929 = new RecipeBookGui();
	private boolean isOpen;
	private boolean isNarrow;
	private boolean isMouseDown;

	public PlayerInventoryScreen(PlayerEntity playerEntity) {
		super(playerEntity.field_7498, playerEntity.inventory, new TranslatableTextComponent("container.crafting"));
		this.field_2558 = true;
	}

	@Override
	public void update() {
		if (this.client.field_1761.hasCreativeInventory()) {
			this.client.method_1507(new CreativePlayerInventoryScreen(this.client.field_1724));
		} else {
			this.field_2929.update();
		}
	}

	@Override
	protected void onInitialized() {
		if (this.client.field_1761.hasCreativeInventory()) {
			this.client.method_1507(new CreativePlayerInventoryScreen(this.client.field_1724));
		} else {
			super.onInitialized();
			this.isNarrow = this.screenWidth < 379;
			this.field_2929.initialize(this.screenWidth, this.screenHeight, this.client, this.isNarrow, this.container);
			this.isOpen = true;
			this.left = this.field_2929.findLeftEdge(this.isNarrow, this.screenWidth, this.width);
			this.listeners.add(this.field_2929);
			this.method_18624(this.field_2929);
			this.addButton(
				new RecipeBookButtonWidget(this.left + 104, this.screenHeight / 2 - 22, 20, 18, 0, 0, 19, field_2933) {
					@Override
					public void method_1826() {
						PlayerInventoryScreen.this.field_2929.reset(PlayerInventoryScreen.this.isNarrow);
						PlayerInventoryScreen.this.field_2929.toggleOpen();
						PlayerInventoryScreen.this.left = PlayerInventoryScreen.this.field_2929
							.findLeftEdge(PlayerInventoryScreen.this.isNarrow, PlayerInventoryScreen.this.screenWidth, PlayerInventoryScreen.this.width);
						this.setPos(PlayerInventoryScreen.this.left + 104, PlayerInventoryScreen.this.screenHeight / 2 - 22);
						PlayerInventoryScreen.this.isMouseDown = true;
					}
				}
			);
		}
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.field_17411.getFormattedText(), 97.0F, 8.0F, 4210752);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.offsetGuiForEffects = !this.field_2929.isOpen();
		if (this.field_2929.isOpen() && this.isNarrow) {
			this.drawBackground(f, i, j);
			this.field_2929.draw(i, j, f);
		} else {
			this.field_2929.draw(i, j, f);
			super.draw(i, j, f);
			this.field_2929.drawGhostSlots(this.left, this.top, false, f);
		}

		this.drawMouseoverTooltip(i, j);
		this.field_2929.drawTooltip(this.left, this.top, i, j);
		this.mouseX = (float)i;
		this.mouseY = (float)j;
		this.method_18624(this.field_2929);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.method_1531().method_4618(field_2801);
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.height);
		drawEntity(k + 51, l + 75, 30, (float)(k + 51) - this.mouseX, (float)(l + 75 - 50) - this.mouseY, this.client.field_1724);
	}

	public static void drawEntity(int i, int j, int k, float f, float g, LivingEntity livingEntity) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)i, (float)j, 50.0F);
		GlStateManager.scalef((float)(-k), (float)k, (float)k);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float h = livingEntity.field_6283;
		float l = livingEntity.yaw;
		float m = livingEntity.pitch;
		float n = livingEntity.prevHeadYaw;
		float o = livingEntity.headYaw;
		GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
		GuiLighting.enable();
		GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-((float)Math.atan((double)(g / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		livingEntity.field_6283 = (float)Math.atan((double)(f / 40.0F)) * 20.0F;
		livingEntity.yaw = (float)Math.atan((double)(f / 40.0F)) * 40.0F;
		livingEntity.pitch = -((float)Math.atan((double)(g / 40.0F))) * 20.0F;
		livingEntity.headYaw = livingEntity.yaw;
		livingEntity.prevHeadYaw = livingEntity.yaw;
		GlStateManager.translatef(0.0F, 0.0F, 0.0F);
		EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().method_1561();
		entityRenderDispatcher.method_3945(180.0F);
		entityRenderDispatcher.method_3948(false);
		entityRenderDispatcher.render(livingEntity, 0.0, 0.0, 0.0, 0.0F, 1.0F, false);
		entityRenderDispatcher.method_3948(true);
		livingEntity.field_6283 = h;
		livingEntity.yaw = l;
		livingEntity.pitch = m;
		livingEntity.prevHeadYaw = n;
		livingEntity.headYaw = o;
		GlStateManager.popMatrix();
		GuiLighting.disable();
		GlStateManager.disableRescaleNormal();
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	@Override
	protected boolean isPointWithinBounds(int i, int j, int k, int l, double d, double e) {
		return (!this.isNarrow || !this.field_2929.isOpen()) && super.isPointWithinBounds(i, j, k, l, d, e);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2929.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.isNarrow && this.field_2929.isOpen() ? false : super.mouseClicked(d, e, i);
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (this.isMouseDown) {
			this.isMouseDown = false;
			return true;
		} else {
			return super.mouseReleased(d, e, i);
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.width) || e >= (double)(j + this.height);
		return this.field_2929.isClickOutsideBounds(d, e, this.left, this.top, this.width, this.height, k) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int i, int j, SlotActionType slotActionType) {
		super.onMouseClick(slot, i, j, slotActionType);
		this.field_2929.slotClicked(slot);
	}

	@Override
	public void refreshRecipeBook() {
		this.field_2929.refresh();
	}

	@Override
	public void onClosed() {
		if (this.isOpen) {
			this.field_2929.close();
		}

		super.onClosed();
	}

	@Override
	public RecipeBookGui getRecipeBookGui() {
		return this.field_2929;
	}
}
