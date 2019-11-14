package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
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
	private final RecipeBookWidget recipeBook = new RecipeBookWidget();
	private boolean isOpen;
	private boolean isNarrow;
	private boolean isMouseDown;

	public InventoryScreen(PlayerEntity player) {
		super(player.playerContainer, player.inventory, new TranslatableText("container.crafting"));
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
			this.addButton(new TexturedButtonWidget(this.left + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEX, buttonWidget -> {
				this.recipeBook.reset(this.isNarrow);
				this.recipeBook.toggleOpen();
				this.left = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
				((TexturedButtonWidget)buttonWidget).setPos(this.left + 104, this.height / 2 - 22);
				this.isMouseDown = true;
			}));
		}
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.font.draw(this.title.asFormattedString(), 97.0F, 8.0F, 4210752);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.offsetGuiForEffects = !this.recipeBook.isOpen();
		if (this.recipeBook.isOpen() && this.isNarrow) {
			this.drawBackground(delta, mouseX, mouseY);
			this.recipeBook.render(mouseX, mouseY, delta);
		} else {
			this.recipeBook.render(mouseX, mouseY, delta);
			super.render(mouseX, mouseY, delta);
			this.recipeBook.drawGhostSlots(this.left, this.top, false, delta);
		}

		this.drawMouseoverTooltip(mouseX, mouseY);
		this.recipeBook.drawTooltip(this.left, this.top, mouseX, mouseY);
		this.mouseX = (float)mouseX;
		this.mouseY = (float)mouseY;
		this.focusOn(this.recipeBook);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int i = this.left;
		int j = this.top;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
		drawEntity(i + 51, j + 75, 30, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, this.minecraft.player);
	}

	public static void drawEntity(int i, int j, int k, float f, float g, LivingEntity entity) {
		RenderSystem.pushMatrix();
		RenderSystem.scalef(-1.0F, 1.0F, 1.0F);
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.translate((double)(-i), (double)j, 50.0);
		matrixStack.scale((float)k, (float)k, (float)k);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-((float)Math.atan((double)(g / 40.0F))) * 20.0F));
		float h = entity.bodyYaw;
		float l = entity.yaw;
		float m = entity.pitch;
		float n = entity.prevHeadYaw;
		float o = entity.headYaw;
		entity.bodyYaw = (float)Math.atan((double)(f / 40.0F)) * 20.0F;
		entity.yaw = (float)Math.atan((double)(f / 40.0F)) * 40.0F;
		entity.pitch = -((float)Math.atan((double)(g / 40.0F))) * 20.0F;
		entity.headYaw = entity.yaw;
		entity.prevHeadYaw = entity.yaw;
		EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
		entityRenderDispatcher.setRenderShadows(false);
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack, immediate, 15728880);
		immediate.method_23796(-i, j, 1000);
		entityRenderDispatcher.setRenderShadows(true);
		entity.bodyYaw = h;
		entity.yaw = l;
		entity.pitch = m;
		entity.prevHeadYaw = n;
		entity.headYaw = o;
		RenderSystem.popMatrix();
	}

	@Override
	protected boolean isPointWithinBounds(int xPosition, int yPosition, int width, int height, double pointX, double pointY) {
		return (!this.isNarrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(xPosition, yPosition, width, height, pointX, pointY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
			return true;
		} else {
			return this.isNarrow && this.recipeBook.isOpen() ? false : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.isMouseDown) {
			this.isMouseDown = false;
			return true;
		} else {
			return super.mouseReleased(mouseX, mouseY, button);
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < (double)left
			|| mouseY < (double)top
			|| mouseX >= (double)(left + this.containerWidth)
			|| mouseY >= (double)(top + this.containerHeight);
		return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.left, this.top, this.containerWidth, this.containerHeight, button) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
		super.onMouseClick(slot, invSlot, button, slotActionType);
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
	public RecipeBookWidget getRecipeBookGui() {
		return this.recipeBook;
	}
}
