package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.AbstractCraftingRecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class InventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
	private float mouseX;
	private float mouseY;
	private final RecipeBookWidget<?> recipeBook;
	private boolean narrow;
	private boolean mouseDown;

	public InventoryScreen(PlayerEntity player) {
		super(player.playerScreenHandler, player.getInventory(), Text.translatable("container.crafting"));
		this.titleX = 97;
		this.recipeBook = new AbstractCraftingRecipeBookWidget(player.playerScreenHandler);
	}

	@Override
	public void handledScreenTick() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client
				.setScreen(
					new CreativeInventoryScreen(
						this.client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue()
					)
				);
		} else {
			this.recipeBook.update();
		}
	}

	@Override
	protected void init() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			this.client
				.setScreen(
					new CreativeInventoryScreen(
						this.client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue()
					)
				);
		} else {
			super.init();
			this.narrow = this.width < 379;
			this.recipeBook.initialize(this.width, this.height, this.client, this.narrow);
			this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
			this.addDrawableChild(new TexturedButtonWidget(this.x + 104, this.height / 2 - 22, 20, 18, RecipeBookWidget.BUTTON_TEXTURES, button -> {
				this.recipeBook.toggleOpen();
				this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
				button.setPosition(this.x + 104, this.height / 2 - 22);
				this.mouseDown = true;
			}));
			this.addSelectableChild(this.recipeBook);
		}
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.recipeBook.isOpen() && this.narrow) {
			this.renderBackground(context, mouseX, mouseY, delta);
			this.recipeBook.render(context, mouseX, mouseY, delta);
		} else {
			super.render(context, mouseX, mouseY, delta);
			this.recipeBook.render(context, mouseX, mouseY, delta);
			this.recipeBook.drawGhostSlots(context, this.x, this.y, false);
		}

		this.drawMouseoverTooltip(context, mouseX, mouseY);
		this.recipeBook.drawTooltip(context, mouseX, mouseY, this.focusedSlot);
		this.mouseX = (float)mouseX;
		this.mouseY = (float)mouseY;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
		drawEntity(context, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F, this.mouseX, this.mouseY, this.client.player);
	}

	public static void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity) {
		float g = (float)(x1 + x2) / 2.0F;
		float h = (float)(y1 + y2) / 2.0F;
		context.enableScissor(x1, y1, x2, y2);
		float i = (float)Math.atan((double)((g - mouseX) / 40.0F));
		float j = (float)Math.atan((double)((h - mouseY) / 40.0F));
		Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
		Quaternionf quaternionf2 = new Quaternionf().rotateX(j * 20.0F * (float) (Math.PI / 180.0));
		quaternionf.mul(quaternionf2);
		float k = entity.bodyYaw;
		float l = entity.getYaw();
		float m = entity.getPitch();
		float n = entity.prevHeadYaw;
		float o = entity.headYaw;
		entity.bodyYaw = 180.0F + i * 20.0F;
		entity.setYaw(180.0F + i * 40.0F);
		entity.setPitch(-j * 20.0F);
		entity.headYaw = entity.getYaw();
		entity.prevHeadYaw = entity.getYaw();
		float p = entity.getScale();
		Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + f * p, 0.0F);
		float q = (float)size / p;
		drawEntity(context, g, h, q, vector3f, quaternionf, quaternionf2, entity);
		entity.bodyYaw = k;
		entity.setYaw(l);
		entity.setPitch(m);
		entity.prevHeadYaw = n;
		entity.headYaw = o;
		context.disableScissor();
	}

	public static void drawEntity(
		DrawContext context, float x, float y, float size, Vector3f vector3f, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity
	) {
		context.getMatrices().push();
		context.getMatrices().translate((double)x, (double)y, 50.0);
		context.getMatrices().scale(size, size, -size);
		context.getMatrices().translate(vector3f.x, vector3f.y, vector3f.z);
		context.getMatrices().multiply(quaternionf);
		context.draw();
		DiffuseLighting.method_34742();
		EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
		if (quaternionf2 != null) {
			entityRenderDispatcher.setRotation(quaternionf2.conjugate(new Quaternionf()).rotateY((float) Math.PI));
		}

		entityRenderDispatcher.setRenderShadows(false);
		RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 1.0F, context.getMatrices(), context.getVertexConsumers(), 15728880));
		context.draw();
		entityRenderDispatcher.setRenderShadows(true);
		context.getMatrices().pop();
		DiffuseLighting.enableGuiDepthLighting();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.recipeBook.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return this.recipeBook.charTyped(chr, modifiers) ? true : super.charTyped(chr, modifiers);
	}

	@Override
	protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
			this.setFocused(this.recipeBook);
			return true;
		} else {
			return this.narrow && this.recipeBook.isOpen() ? false : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.mouseDown) {
			this.mouseDown = false;
			return true;
		} else {
			return super.mouseReleased(mouseX, mouseY, button);
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < (double)left
			|| mouseY < (double)top
			|| mouseX >= (double)(left + this.backgroundWidth)
			|| mouseY >= (double)(top + this.backgroundHeight);
		return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		super.onMouseClick(slot, slotId, button, actionType);
		this.recipeBook.onMouseClick(slot);
	}

	@Override
	public void refreshRecipeBook() {
		this.recipeBook.refresh();
	}

	@Override
	public RecipeBookWidget getRecipeBookWidget() {
		return this.recipeBook;
	}
}
