/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EnchantingScreen
extends AbstractContainerScreen<EnchantingTableContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/enchanting_table.png");
    private static final Identifier BOOK_TEXURE = new Identifier("textures/entity/enchanting_table_book.png");
    private static final BookModel bookModel = new BookModel();
    private final Random random = new Random();
    public int field_2915;
    public float nextPageAngle;
    public float pageAngle;
    public float field_2909;
    public float field_2906;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack field_2913 = ItemStack.EMPTY;

    public EnchantingScreen(EnchantingTableContainer enchantingTableContainer, PlayerInventory playerInventory, Component component) {
        super(enchantingTableContainer, playerInventory, component);
    }

    @Override
    protected void drawForeground(int i, int j) {
        this.font.draw(this.title.getFormattedText(), 12.0f, 5.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    public void tick() {
        super.tick();
        this.method_2478();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        int j = (this.width - this.containerWidth) / 2;
        int k = (this.height - this.containerHeight) / 2;
        for (int l = 0; l < 3; ++l) {
            double f = d - (double)(j + 60);
            double g = e - (double)(k + 14 + 19 * l);
            if (!(f >= 0.0) || !(g >= 0.0) || !(f < 108.0) || !(g < 19.0) || !((EnchantingTableContainer)this.container).onButtonClick(this.minecraft.player, l)) continue;
            this.minecraft.interactionManager.clickButton(((EnchantingTableContainer)this.container).syncId, l);
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    protected void drawBackground(float f, int i, int j) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.containerWidth) / 2;
        int l = (this.height - this.containerHeight) / 2;
        this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        int m = (int)this.minecraft.window.getScaleFactor();
        GlStateManager.viewport((this.width - 320) / 2 * m, (this.height - 240) / 2 * m, 320 * m, 240 * m);
        GlStateManager.translatef(-0.34f, 0.23f, 0.0f);
        GlStateManager.multMatrix(Matrix4f.method_4929(90.0, 1.3333334f, 9.0f, 80.0f));
        float g = 1.0f;
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GuiLighting.enable();
        GlStateManager.translatef(0.0f, 3.3f, -16.0f);
        GlStateManager.scalef(1.0f, 1.0f, 1.0f);
        float h = 5.0f;
        GlStateManager.scalef(5.0f, 5.0f, 5.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BOOK_TEXURE);
        GlStateManager.rotatef(20.0f, 1.0f, 0.0f, 0.0f);
        float n = MathHelper.lerp(f, this.pageTurningSpeed, this.nextPageTurningSpeed);
        GlStateManager.translatef((1.0f - n) * 0.2f, (1.0f - n) * 0.1f, (1.0f - n) * 0.25f);
        GlStateManager.rotatef(-(1.0f - n) * 90.0f - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
        float o = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle) + 0.25f;
        float p = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle) + 0.75f;
        o = (o - (float)MathHelper.fastFloor(o)) * 1.6f - 0.3f;
        p = (p - (float)MathHelper.fastFloor(p)) * 1.6f - 0.3f;
        if (o < 0.0f) {
            o = 0.0f;
        }
        if (p < 0.0f) {
            p = 0.0f;
        }
        if (o > 1.0f) {
            o = 1.0f;
        }
        if (p > 1.0f) {
            p = 1.0f;
        }
        GlStateManager.enableRescaleNormal();
        bookModel.render(0.0f, o, p, n, 0.0f, 0.0625f);
        GlStateManager.disableRescaleNormal();
        GuiLighting.disable();
        GlStateManager.matrixMode(5889);
        GlStateManager.viewport(0, 0, this.minecraft.window.getFramebufferWidth(), this.minecraft.window.getFramebufferHeight());
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GuiLighting.disable();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantingPhrases.getInstance().setSeed(((EnchantingTableContainer)this.container).getSeed());
        int q = ((EnchantingTableContainer)this.container).getLapisCount();
        for (int r = 0; r < 3; ++r) {
            int s = k + 60;
            int t = s + 20;
            this.blitOffset = 0;
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            int u = ((EnchantingTableContainer)this.container).enchantmentPower[r];
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (u == 0) {
                this.blit(s, l + 14 + 19 * r, 0, 185, 108, 19);
                continue;
            }
            String string = "" + u;
            int v = 86 - this.font.getStringWidth(string);
            String string2 = EnchantingPhrases.getInstance().generatePhrase(this.font, v);
            TextRenderer textRenderer = this.minecraft.getFontManager().getTextRenderer(MinecraftClient.ALT_TEXT_RENDERER_ID);
            int w = 6839882;
            if (!(q >= r + 1 && this.minecraft.player.experienceLevel >= u || this.minecraft.player.abilities.creativeMode)) {
                this.blit(s, l + 14 + 19 * r, 0, 185, 108, 19);
                this.blit(s + 1, l + 15 + 19 * r, 16 * r, 239, 16, 16);
                textRenderer.drawStringBounded(string2, t, l + 16 + 19 * r, v, (w & 0xFEFEFE) >> 1);
                w = 4226832;
            } else {
                int x = i - (k + 60);
                int y = j - (l + 14 + 19 * r);
                if (x >= 0 && y >= 0 && x < 108 && y < 19) {
                    this.blit(s, l + 14 + 19 * r, 0, 204, 108, 19);
                    w = 0xFFFF80;
                } else {
                    this.blit(s, l + 14 + 19 * r, 0, 166, 108, 19);
                }
                this.blit(s + 1, l + 15 + 19 * r, 16 * r, 223, 16, 16);
                textRenderer.drawStringBounded(string2, t, l + 16 + 19 * r, v, w);
                w = 8453920;
            }
            textRenderer = this.minecraft.textRenderer;
            textRenderer.drawWithShadow(string, t + 86 - textRenderer.getStringWidth(string), l + 16 + 19 * r + 7, w);
        }
    }

    @Override
    public void render(int i, int j, float f) {
        f = this.minecraft.getTickDelta();
        this.renderBackground();
        super.render(i, j, f);
        this.drawMouseoverTooltip(i, j);
        boolean bl = this.minecraft.player.abilities.creativeMode;
        int k = ((EnchantingTableContainer)this.container).getLapisCount();
        for (int l = 0; l < 3; ++l) {
            int m = ((EnchantingTableContainer)this.container).enchantmentPower[l];
            Enchantment enchantment = Enchantment.byRawId(((EnchantingTableContainer)this.container).enchantmentId[l]);
            int n = ((EnchantingTableContainer)this.container).enchantmentLevel[l];
            int o = l + 1;
            if (!this.isPointWithinBounds(60, 14 + 19 * l, 108, 17, i, j) || m <= 0 || n < 0 || enchantment == null) continue;
            ArrayList<String> list = Lists.newArrayList();
            list.add("" + (Object)((Object)ChatFormat.WHITE) + (Object)((Object)ChatFormat.ITALIC) + I18n.translate("container.enchant.clue", enchantment.getTextComponent(n).getFormattedText()));
            if (!bl) {
                list.add("");
                if (this.minecraft.player.experienceLevel < m) {
                    list.add((Object)((Object)ChatFormat.RED) + I18n.translate("container.enchant.level.requirement", ((EnchantingTableContainer)this.container).enchantmentPower[l]));
                } else {
                    String string = o == 1 ? I18n.translate("container.enchant.lapis.one", new Object[0]) : I18n.translate("container.enchant.lapis.many", o);
                    ChatFormat chatFormat = k >= o ? ChatFormat.GRAY : ChatFormat.RED;
                    list.add((Object)((Object)chatFormat) + "" + string);
                    string = o == 1 ? I18n.translate("container.enchant.level.one", new Object[0]) : I18n.translate("container.enchant.level.many", o);
                    list.add((Object)((Object)ChatFormat.GRAY) + "" + string);
                }
            }
            this.renderTooltip(list, i, j);
            break;
        }
    }

    public void method_2478() {
        ItemStack itemStack = ((EnchantingTableContainer)this.container).getSlot(0).getStack();
        if (!ItemStack.areEqualIgnoreDamage(itemStack, this.field_2913)) {
            this.field_2913 = itemStack;
            do {
                this.field_2909 += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.nextPageAngle <= this.field_2909 + 1.0f && this.nextPageAngle >= this.field_2909 - 1.0f);
        }
        ++this.field_2915;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = false;
        for (int i = 0; i < 3; ++i) {
            if (((EnchantingTableContainer)this.container).enchantmentPower[i] == 0) continue;
            bl = true;
        }
        this.nextPageTurningSpeed = bl ? (this.nextPageTurningSpeed += 0.2f) : (this.nextPageTurningSpeed -= 0.2f);
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0f, 1.0f);
        float f = (this.field_2909 - this.nextPageAngle) * 0.4f;
        float g = 0.2f;
        f = MathHelper.clamp(f, -0.2f, 0.2f);
        this.field_2906 += (f - this.field_2906) * 0.9f;
        this.nextPageAngle += this.field_2906;
    }
}

