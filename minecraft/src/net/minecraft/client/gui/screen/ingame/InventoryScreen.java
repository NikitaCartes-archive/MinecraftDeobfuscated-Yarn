package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookScreen;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class InventoryScreen extends AbstractInventoryScreen<PlayerContainer> implements RecipeBookProvider {
	private static final Identifier RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
	private float mouseX;
	private float mouseY;
	private final RecipeBookScreen recipeBook = new RecipeBookScreen();
	private boolean isOpen;
	private boolean isNarrow;
	private boolean isMouseDown;

	public InventoryScreen(PlayerEntity playerEntity) {
		super(playerEntity.playerContainer, playerEntity.inventory, new TranslatableComponent("container.crafting"));
		this.passEvents = true;
	}

	@Override
	public void tick() {
		if (this.minecraft.interactionManager.hasCreativeInventory()) {
			this.minecraft.openScreen(new CreativeInventoryScreen(this.minecraft.player));
		} else {
			this.recipeBook.update();
		}
	}

	@Override
	protected void init() {
		if (this.minecraft.interactionManager.hasCreativeInventory()) {
			this.minecraft.openScreen(new CreativeInventoryScreen(this.minecraft.player));
		} else {
			super.init();
			this.isNarrow = this.width < 379;
			this.recipeBook.initialize(this.width, this.height, this.minecraft, this.isNarrow, this.container);
			this.isOpen = true;
			this.left = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
			this.children.add(this.recipeBook);
			this.setInitialFocus(this.recipeBook);
			this.addButton(new RecipeBookButtonWidget(this.left + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEX, buttonWidget -> {
				this.recipeBook.reset(this.isNarrow);
				this.recipeBook.toggleOpen();
				this.left = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
				((RecipeBookButtonWidget)buttonWidget).setPos(this.left + 104, this.height / 2 - 22);
				this.isMouseDown = true;
			}));
		}
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.font.draw(this.title.getFormattedText(), 97.0F, 8.0F, 4210752);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.offsetGuiForEffects = !this.recipeBook.isOpen();
		if (this.recipeBook.isOpen() && this.isNarrow) {
			this.drawBackground(f, i, j);
			this.recipeBook.render(i, j, f);
		} else {
			this.recipeBook.render(i, j, f);
			super.render(i, j, f);
			this.recipeBook.drawGhostSlots(this.left, this.top, false, f);
		}

		this.drawMouseoverTooltip(i, j);
		this.recipeBook.drawTooltip(this.left, this.top, i, j);
		this.mouseX = (float)i;
		this.mouseY = (float)j;
		this.focusOn(this.recipeBook);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int k = this.left;
		int l = this.top;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
		drawEntity(k + 51, l + 75, 30, (float)(k + 51) - this.mouseX, (float)(l + 75 - 50) - this.mouseY, this.minecraft.player);
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
		EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
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
		return (!this.isNarrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(i, j, k, l, d, e);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.recipeBook.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.isNarrow && this.recipeBook.isOpen() ? false : super.mouseClicked(d, e, i);
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
		return this.recipeBook.isClickOutsideBounds(d, e, this.left, this.top, this.containerWidth, this.containerHeight, k) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int i, int j, SlotActionType slotActionType) {
		super.onMouseClick(slot, i, j, slotActionType);
		this.recipeBook.slotClicked(slot);
	}

	@Override
	public void refreshRecipeBook() {
		this.recipeBook.refresh();
	}

	@Override
	public void removed() {
		if (this.isOpen) {
			this.recipeBook.close();
		}

		super.removed();
	}

	@Override
	public RecipeBookScreen getRecipeBookGui() {
		return this.recipeBook;
	}
}
