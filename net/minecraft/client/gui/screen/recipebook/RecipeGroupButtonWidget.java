/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.recipe.book.RecipeBookGroup;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

@Environment(value=EnvType.CLIENT)
public class RecipeGroupButtonWidget
extends ToggleButtonWidget {
    private final RecipeBookGroup category;
    private float bounce;

    public RecipeGroupButtonWidget(RecipeBookGroup recipeBookGroup) {
        super(0, 0, 35, 27, false);
        this.category = recipeBookGroup;
        this.setTextureUV(153, 2, 35, 0, RecipeBookWidget.TEXTURE);
    }

    public void checkForNewRecipes(MinecraftClient minecraftClient) {
        ClientRecipeBook clientRecipeBook = minecraftClient.player.getRecipeBook();
        List<RecipeResultCollection> list = clientRecipeBook.getResultsForGroup(this.category);
        if (!(minecraftClient.player.container instanceof CraftingContainer)) {
            return;
        }
        for (RecipeResultCollection recipeResultCollection : list) {
            for (Recipe<?> recipe : recipeResultCollection.getResults(clientRecipeBook.isFilteringCraftable((CraftingContainer)minecraftClient.player.container))) {
                if (!clientRecipeBook.shouldDisplay(recipe)) continue;
                this.bounce = 15.0f;
                return;
            }
        }
    }

    @Override
    public void renderButton(int i, int j, float f) {
        if (this.bounce > 0.0f) {
            float g = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.x + 8, this.y + 12, 0.0f);
            GlStateManager.scalef(1.0f, g, 1.0f);
            GlStateManager.translatef(-(this.x + 8), -(this.y + 12), 0.0f);
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(this.texture);
        GlStateManager.disableDepthTest();
        int k = this.u;
        int l = this.v;
        if (this.toggled) {
            k += this.pressedUOffset;
        }
        if (this.isHovered()) {
            l += this.hoverVOffset;
        }
        int m = this.x;
        if (this.toggled) {
            m -= 2;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.blit(m, this.y, k, l, this.width, this.height);
        GlStateManager.enableDepthTest();
        GuiLighting.enableForItems();
        GlStateManager.disableLighting();
        this.method_2621(minecraftClient.getItemRenderer());
        GlStateManager.enableLighting();
        GuiLighting.disable();
        if (this.bounce > 0.0f) {
            GlStateManager.popMatrix();
            this.bounce -= f;
        }
    }

    private void method_2621(ItemRenderer itemRenderer) {
        int i;
        List<ItemStack> list = this.category.getIcons();
        int n = i = this.toggled ? -2 : 0;
        if (list.size() == 1) {
            itemRenderer.renderGuiItem(list.get(0), this.x + 9 + i, this.y + 5);
        } else if (list.size() == 2) {
            itemRenderer.renderGuiItem(list.get(0), this.x + 3 + i, this.y + 5);
            itemRenderer.renderGuiItem(list.get(1), this.x + 14 + i, this.y + 5);
        }
    }

    public RecipeBookGroup getCategory() {
        return this.category;
    }

    public boolean hasKnownRecipes(ClientRecipeBook clientRecipeBook) {
        List<RecipeResultCollection> list = clientRecipeBook.getResultsForGroup(this.category);
        this.visible = false;
        if (list != null) {
            for (RecipeResultCollection recipeResultCollection : list) {
                if (!recipeResultCollection.isInitialized() || !recipeResultCollection.hasFittableResults()) continue;
                this.visible = true;
                break;
            }
        }
        return this.visible;
    }
}

