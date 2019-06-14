package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class InventoryScreen extends AbstractInventoryScreen<PlayerContainer> implements RecipeBookProvider {
	private static final Identifier RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
	private float mouseX;
	private float mouseY;
	private final RecipeBookWidget field_2929 = new RecipeBookWidget();
	private boolean isOpen;
	private boolean isNarrow;
	private boolean isMouseDown;

	public InventoryScreen(PlayerEntity playerEntity) {
		super(playerEntity.playerContainer, playerEntity.inventory, new TranslatableText("container.crafting"));
		this.passEvents = true;
	}

	@Override
	public void tick() {
		if (this.minecraft.field_1761.hasCreativeInventory()) {
			this.minecraft.method_1507(new CreativeInventoryScreen(this.minecraft.field_1724));
		} else {
			this.field_2929.update();
		}
	}

	@Override
	protected void init() {
		if (this.minecraft.field_1761.hasCreativeInventory()) {
			this.minecraft.method_1507(new CreativeInventoryScreen(this.minecraft.field_1724));
		} else {
			super.init();
			this.isNarrow = this.width < 379;
			this.field_2929.initialize(this.width, this.height, this.minecraft, this.isNarrow, this.container);
			this.isOpen = true;
			this.left = this.field_2929.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
			this.children.add(this.field_2929);
			this.method_20085(this.field_2929);
			this.addButton(new TexturedButtonWidget(this.left + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEX, buttonWidget -> {
				this.field_2929.reset(this.isNarrow);
				this.field_2929.toggleOpen();
				this.left = this.field_2929.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
				((TexturedButtonWidget)buttonWidget).setPos(this.left + 104, this.height / 2 - 22);
				this.isMouseDown = true;
			}));
		}
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.font.draw(this.title.asFormattedString(), 97.0F, 8.0F, 4210752);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.offsetGuiForEffects = !this.field_2929.isOpen();
		if (this.field_2929.isOpen() && this.isNarrow) {
			this.drawBackground(f, i, j);
			this.field_2929.render(i, j, f);
		} else {
			this.field_2929.render(i, j, f);
			super.render(i, j, f);
			this.field_2929.drawGhostSlots(this.left, this.top, false, f);
		}

		this.drawMouseoverTooltip(i, j);
		this.field_2929.drawTooltip(this.left, this.top, i, j);
		this.mouseX = (float)i;
		this.mouseY = (float)j;
		this.method_20086(this.field_2929);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().bindTexture(BACKGROUND_TEXTURE);
		int k = this.left;
		int l = this.top;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
		drawEntity(k + 51, l + 75, 30, (float)(k + 51) - this.mouseX, (float)(l + 75 - 50) - this.mouseY, this.minecraft.field_1724);
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
		entityRenderDispatcher.setRenderShadows(false);
		entityRenderDispatcher.render(livingEntity, 0.0, 0.0, 0.0, 0.0F, 1.0F, false);
		entityRenderDispatcher.setRenderShadows(true);
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
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
		return this.field_2929.isClickOutsideBounds(d, e, this.left, this.top, this.containerWidth, this.containerHeight, k) && bl;
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
	public void removed() {
		if (this.isOpen) {
			this.field_2929.close();
		}

		super.removed();
	}

	@Override
	public RecipeBookWidget getRecipeBookGui() {
		return this.field_2929;
	}
}
