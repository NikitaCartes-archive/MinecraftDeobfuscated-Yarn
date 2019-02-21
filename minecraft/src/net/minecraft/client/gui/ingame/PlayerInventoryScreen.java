package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.InputListener;
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
	private static final Identifier RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
	private float mouseX;
	private float mouseY;
	private final RecipeBookGui recipeBook = new RecipeBookGui();
	private boolean isOpen;
	private boolean isNarrow;
	private boolean isMouseDown;

	public PlayerInventoryScreen(PlayerEntity playerEntity) {
		super(playerEntity.containerPlayer, playerEntity.inventory, new TranslatableTextComponent("container.crafting"));
		this.field_2558 = true;
	}

	@Override
	public void update() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client.openScreen(new CreativePlayerInventoryScreen(this.client.player));
		} else {
			this.recipeBook.update();
		}
	}

	@Override
	protected void onInitialized() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client.openScreen(new CreativePlayerInventoryScreen(this.client.player));
		} else {
			super.onInitialized();
			this.isNarrow = this.screenWidth < 379;
			this.recipeBook.initialize(this.screenWidth, this.screenHeight, this.client, this.isNarrow, this.container);
			this.isOpen = true;
			this.left = this.recipeBook.findLeftEdge(this.isNarrow, this.screenWidth, this.width);
			this.listeners.add(this.recipeBook);
			this.addButton(
				new RecipeBookButtonWidget(this.left + 104, this.screenHeight / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEX) {
					@Override
					public void onPressed(double d, double e) {
						PlayerInventoryScreen.this.recipeBook.reset(PlayerInventoryScreen.this.isNarrow);
						PlayerInventoryScreen.this.recipeBook.toggleOpen();
						PlayerInventoryScreen.this.left = PlayerInventoryScreen.this.recipeBook
							.findLeftEdge(PlayerInventoryScreen.this.isNarrow, PlayerInventoryScreen.this.screenWidth, PlayerInventoryScreen.this.width);
						this.setPos(PlayerInventoryScreen.this.left + 104, PlayerInventoryScreen.this.screenHeight / 2 - 22);
						PlayerInventoryScreen.this.isMouseDown = true;
					}
				}
			);
		}
	}

	@Nullable
	@Override
	public InputListener getFocused() {
		return this.recipeBook;
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.name.getFormattedText(), 97.0F, 8.0F, 4210752);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.offsetGuiForEffects = !this.recipeBook.isOpen();
		if (this.recipeBook.isOpen() && this.isNarrow) {
			this.drawBackground(f, i, j);
			this.recipeBook.draw(i, j, f);
		} else {
			this.recipeBook.draw(i, j, f);
			super.draw(i, j, f);
			this.recipeBook.drawGhostSlots(this.left, this.top, false, f);
		}

		this.drawMouseoverTooltip(i, j);
		this.recipeBook.drawTooltip(this.left, this.top, i, j);
		this.mouseX = (float)i;
		this.mouseY = (float)j;
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.height);
		drawEntity(k + 51, l + 75, 30, (float)(k + 51) - this.mouseX, (float)(l + 75 - 50) - this.mouseY, this.client.player);
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
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.width) || e >= (double)(j + this.height);
		return this.recipeBook.isClickOutsideBounds(d, e, this.left, this.top, this.width, this.height, k) && bl;
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
	public void onClosed() {
		if (this.isOpen) {
			this.recipeBook.close();
		}

		super.onClosed();
	}

	@Override
	public RecipeBookGui getRecipeBookGui() {
		return this.recipeBook;
	}
}
