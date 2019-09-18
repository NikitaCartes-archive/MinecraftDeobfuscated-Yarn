/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class InventoryScreen
extends AbstractInventoryScreen<PlayerContainer>
implements RecipeBookProvider {
    private static final Identifier RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
    private float mouseX;
    private float mouseY;
    private final RecipeBookWidget recipeBook = new RecipeBookWidget();
    private boolean isOpen;
    private boolean isNarrow;
    private boolean isMouseDown;

    public InventoryScreen(PlayerEntity playerEntity) {
        super(playerEntity.playerContainer, playerEntity.inventory, new TranslatableText("container.crafting", new Object[0]));
        this.passEvents = true;
    }

    @Override
    public void tick() {
        if (this.minecraft.interactionManager.hasCreativeInventory()) {
            this.minecraft.openScreen(new CreativeInventoryScreen(this.minecraft.player));
            return;
        }
        this.recipeBook.update();
    }

    @Override
    protected void init() {
        if (this.minecraft.interactionManager.hasCreativeInventory()) {
            this.minecraft.openScreen(new CreativeInventoryScreen(this.minecraft.player));
            return;
        }
        super.init();
        this.isNarrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.minecraft, this.isNarrow, (CraftingContainer)this.container);
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

    @Override
    protected void drawForeground(int i, int j) {
        this.font.draw(this.title.asFormattedString(), 97.0f, 8.0f, 0x404040);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        boolean bl = this.offsetGuiForEffects = !this.recipeBook.isOpen();
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
        this.mouseX = i;
        this.mouseY = j;
        this.focusOn(this.recipeBook);
    }

    @Override
    protected void drawBackground(float f, int i, int j) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int k = this.left;
        int l = this.top;
        this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
        InventoryScreen.drawEntity(k + 51, l + 75, 30, (float)(k + 51) - this.mouseX, (float)(l + 75 - 50) - this.mouseY, this.minecraft.player);
    }

    public static void drawEntity(int i, int j, int k, float f, float g, LivingEntity livingEntity) {
        RenderSystem.enableColorMaterial();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(i, j, 50.0f);
        RenderSystem.scalef(-k, k, k);
        RenderSystem.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        float h = livingEntity.bodyYaw;
        float l = livingEntity.yaw;
        float m = livingEntity.pitch;
        float n = livingEntity.prevHeadYaw;
        float o = livingEntity.headYaw;
        RenderSystem.rotatef(135.0f, 0.0f, 1.0f, 0.0f);
        GuiLighting.enable();
        RenderSystem.rotatef(-135.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(-((float)Math.atan(g / 40.0f)) * 20.0f, 1.0f, 0.0f, 0.0f);
        livingEntity.bodyYaw = (float)Math.atan(f / 40.0f) * 20.0f;
        livingEntity.yaw = (float)Math.atan(f / 40.0f) * 40.0f;
        livingEntity.pitch = -((float)Math.atan(g / 40.0f)) * 20.0f;
        livingEntity.headYaw = livingEntity.yaw;
        livingEntity.prevHeadYaw = livingEntity.yaw;
        RenderSystem.translatef(0.0f, 0.0f, 0.0f);
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
        entityRenderDispatcher.method_3945(180.0f);
        entityRenderDispatcher.setRenderShadows(false);
        entityRenderDispatcher.render(livingEntity, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        entityRenderDispatcher.setRenderShadows(true);
        livingEntity.bodyYaw = h;
        livingEntity.yaw = l;
        livingEntity.pitch = m;
        livingEntity.prevHeadYaw = n;
        livingEntity.headYaw = o;
        RenderSystem.popMatrix();
        GuiLighting.disable();
        RenderSystem.disableRescaleNormal();
        RenderSystem.activeTexture(33985);
        RenderSystem.disableTexture();
        RenderSystem.activeTexture(33984);
    }

    @Override
    protected boolean isPointWithinBounds(int i, int j, int k, int l, double d, double e) {
        return (!this.isNarrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(i, j, k, l, d, e);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.recipeBook.mouseClicked(d, e, i)) {
            return true;
        }
        if (this.isNarrow && this.recipeBook.isOpen()) {
            return false;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (this.isMouseDown) {
            this.isMouseDown = false;
            return true;
        }
        return super.mouseReleased(d, e, i);
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
    public RecipeBookWidget getRecipeBookGui() {
        return this.recipeBook;
    }
}

