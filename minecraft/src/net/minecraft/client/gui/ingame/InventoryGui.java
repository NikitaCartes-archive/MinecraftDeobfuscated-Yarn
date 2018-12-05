package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_308;
import net.minecraft.class_344;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.container.RecipeBookGui;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.ActionTypeSlot;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class InventoryGui extends AbstractGuiInventory implements RecipeBookProvider {
	private static final Identifier TEXTURE = new Identifier("textures/gui/recipe_button.png");
	private float field_2935;
	private float field_2934;
	private final RecipeBookGui recipeBook = new RecipeBookGui();
	private boolean field_2932;
	private boolean field_2931;
	private boolean field_2930;

	public InventoryGui(PlayerEntity playerEntity) {
		super(playerEntity.containerPlayer);
		this.field_2558 = true;
	}

	@Override
	public void update() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client.openGui(new CreativeInventoryGui(this.client.player));
		} else {
			this.recipeBook.update();
		}
	}

	@Override
	protected void onInitialized() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client.openGui(new CreativeInventoryGui(this.client.player));
		} else {
			super.onInitialized();
			this.field_2931 = this.width < 379;
			this.recipeBook.method_2597(this.width, this.height, this.client, this.field_2931, (CraftingContainer)this.container);
			this.field_2932 = true;
			this.left = this.recipeBook.method_2595(this.field_2931, this.width, this.containerWidth);
			this.listeners.add(this.recipeBook);
			this.addButton(new class_344(10, this.left + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, TEXTURE) {
				@Override
				public void onPressed(double d, double e) {
					InventoryGui.this.recipeBook.method_2579(InventoryGui.this.field_2931);
					InventoryGui.this.recipeBook.method_2591();
					InventoryGui.this.left = InventoryGui.this.recipeBook.method_2595(InventoryGui.this.field_2931, InventoryGui.this.width, InventoryGui.this.containerWidth);
					this.getPos(InventoryGui.this.left + 104, InventoryGui.this.height / 2 - 22);
					InventoryGui.this.field_2930 = true;
				}
			});
		}
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.recipeBook;
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(I18n.translate("container.crafting"), 97.0F, 8.0F, 4210752);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.offsetGuiForEffects = !this.recipeBook.isOpen();
		if (this.recipeBook.isOpen() && this.field_2931) {
			this.drawBackground(f, i, j);
			this.recipeBook.method_2578(i, j, f);
		} else {
			this.recipeBook.method_2578(i, j, f);
			super.draw(i, j, f);
			this.recipeBook.method_2581(this.left, this.top, false, f);
		}

		this.drawMousoverTooltip(i, j);
		this.recipeBook.method_2601(this.left, this.top, i, j);
		this.field_2935 = (float)i;
		this.field_2934 = (float)j;
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		drawEntity(k + 51, l + 75, 30, (float)(k + 51) - this.field_2935, (float)(l + 75 - 50) - this.field_2934, this.client.player);
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
		float n = livingEntity.prevHeadPitch;
		float o = livingEntity.headPitch;
		GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
		class_308.method_1452();
		GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-((float)Math.atan((double)(g / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		livingEntity.field_6283 = (float)Math.atan((double)(f / 40.0F)) * 20.0F;
		livingEntity.yaw = (float)Math.atan((double)(f / 40.0F)) * 40.0F;
		livingEntity.pitch = -((float)Math.atan((double)(g / 40.0F))) * 20.0F;
		livingEntity.headPitch = livingEntity.yaw;
		livingEntity.prevHeadPitch = livingEntity.yaw;
		GlStateManager.translatef(0.0F, 0.0F, 0.0F);
		EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
		entityRenderDispatcher.method_3945(180.0F);
		entityRenderDispatcher.method_3948(false);
		entityRenderDispatcher.method_3954(livingEntity, 0.0, 0.0, 0.0, 0.0F, 1.0F, false);
		entityRenderDispatcher.method_3948(true);
		livingEntity.field_6283 = h;
		livingEntity.yaw = l;
		livingEntity.pitch = m;
		livingEntity.prevHeadPitch = n;
		livingEntity.headPitch = o;
		GlStateManager.popMatrix();
		class_308.method_1450();
		GlStateManager.disableRescaleNormal();
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	@Override
	protected boolean isPointWithinBounds(int i, int j, int k, int l, double d, double e) {
		return (!this.field_2931 || !this.recipeBook.isOpen()) && super.isPointWithinBounds(i, j, k, l, d, e);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.recipeBook.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.field_2931 && this.recipeBook.isOpen() ? false : super.mouseClicked(d, e, i);
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (this.field_2930) {
			this.field_2930 = false;
			return true;
		} else {
			return super.mouseReleased(d, e, i);
		}
	}

	@Override
	protected boolean isClickInContainerBounds(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
		return this.recipeBook.method_2598(d, e, this.left, this.top, this.containerWidth, this.containerHeight, k) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int i, int j, ActionTypeSlot actionTypeSlot) {
		super.onMouseClick(slot, i, j, actionTypeSlot);
		this.recipeBook.method_2600(slot);
	}

	@Override
	public void method_16891() {
		this.recipeBook.method_2592();
	}

	@Override
	public void onClosed() {
		if (this.field_2932) {
			this.recipeBook.close();
		}

		super.onClosed();
	}

	@Override
	public RecipeBookGui getRecipeBookGui() {
		return this.recipeBook;
	}
}
