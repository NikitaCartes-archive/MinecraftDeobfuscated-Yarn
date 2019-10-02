/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class EnchantingScreen
extends AbstractContainerScreen<EnchantingTableContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/enchanting_table.png");
    private static final Identifier BOOK_TEXURE = new Identifier("textures/entity/enchanting_table_book.png");
    private static final BookModel bookModel = new BookModel();
    private final Random random = new Random();
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack stack = ItemStack.EMPTY;

    public EnchantingScreen(EnchantingTableContainer enchantingTableContainer, PlayerInventory playerInventory, Text text) {
        super(enchantingTableContainer, playerInventory, text);
    }

    @Override
    protected void drawForeground(int i, int j) {
        this.font.draw(this.title.asFormattedString(), 12.0f, 5.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    public void tick() {
        super.tick();
        this.doTick();
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
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.containerWidth) / 2;
        int l = (this.height - this.containerHeight) / 2;
        this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        int m = (int)this.minecraft.getWindow().getScaleFactor();
        RenderSystem.viewport((this.width - 320) / 2 * m, (this.height - 240) / 2 * m, 320 * m, 240 * m);
        RenderSystem.translatef(-0.34f, 0.23f, 0.0f);
        RenderSystem.multMatrix(Matrix4f.method_4929(90.0, 1.3333334f, 9.0f, 80.0f));
        RenderSystem.matrixMode(5888);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.peek().loadIdentity();
        matrixStack.translate(0.0, 3.3f, 1984.0);
        float g = 5.0f;
        matrixStack.scale(5.0f, 5.0f, 5.0f);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(20.0f, true));
        float h = MathHelper.lerp(f, this.pageTurningSpeed, this.nextPageTurningSpeed);
        matrixStack.translate((1.0f - h) * 0.2f, (1.0f - h) * 0.1f, (1.0f - h) * 0.25f);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-(1.0f - h) * 90.0f - 90.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(180.0f, true));
        float n = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle) + 0.25f;
        float o = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle) + 0.75f;
        n = (n - (float)MathHelper.fastFloor(n)) * 1.6f - 0.3f;
        o = (o - (float)MathHelper.fastFloor(o)) * 1.6f - 0.3f;
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (o < 0.0f) {
            o = 0.0f;
        }
        if (n > 1.0f) {
            n = 1.0f;
        }
        if (o > 1.0f) {
            o = 1.0f;
        }
        RenderSystem.enableRescaleNormal();
        bookModel.setPageAngles(0.0f, n, o, h);
        LayeredVertexConsumerStorage.class_4598 lv = LayeredVertexConsumerStorage.method_22991(Tessellator.getInstance().getBufferBuilder());
        VertexConsumer vertexConsumer = lv.getBuffer(RenderLayer.method_23017(BOOK_TEXURE));
        OverlayTexture.clearDefaultOverlay(vertexConsumer);
        bookModel.render(matrixStack, vertexConsumer, 0.0625f, 0xF000F0, null);
        vertexConsumer.clearDefaultOverlay();
        lv.method_22993();
        matrixStack.pop();
        RenderSystem.matrixMode(5889);
        RenderSystem.viewport(0, 0, this.minecraft.getWindow().getFramebufferWidth(), this.minecraft.getWindow().getFramebufferHeight());
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantingPhrases.getInstance().setSeed(((EnchantingTableContainer)this.container).getSeed());
        int p = ((EnchantingTableContainer)this.container).getLapisCount();
        for (int q = 0; q < 3; ++q) {
            int r = k + 60;
            int s = r + 20;
            this.setBlitOffset(0);
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            int t = ((EnchantingTableContainer)this.container).enchantmentPower[q];
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (t == 0) {
                this.blit(r, l + 14 + 19 * q, 0, 185, 108, 19);
                continue;
            }
            String string = "" + t;
            int u = 86 - this.font.getStringWidth(string);
            String string2 = EnchantingPhrases.getInstance().generatePhrase(this.font, u);
            TextRenderer textRenderer = this.minecraft.getFontManager().getTextRenderer(MinecraftClient.ALT_TEXT_RENDERER_ID);
            int v = 6839882;
            if (!(p >= q + 1 && this.minecraft.player.experienceLevel >= t || this.minecraft.player.abilities.creativeMode)) {
                this.blit(r, l + 14 + 19 * q, 0, 185, 108, 19);
                this.blit(r + 1, l + 15 + 19 * q, 16 * q, 239, 16, 16);
                textRenderer.drawStringBounded(string2, s, l + 16 + 19 * q, u, (v & 0xFEFEFE) >> 1);
                v = 4226832;
            } else {
                int w = i - (k + 60);
                int x = j - (l + 14 + 19 * q);
                if (w >= 0 && x >= 0 && w < 108 && x < 19) {
                    this.blit(r, l + 14 + 19 * q, 0, 204, 108, 19);
                    v = 0xFFFF80;
                } else {
                    this.blit(r, l + 14 + 19 * q, 0, 166, 108, 19);
                }
                this.blit(r + 1, l + 15 + 19 * q, 16 * q, 223, 16, 16);
                textRenderer.drawStringBounded(string2, s, l + 16 + 19 * q, u, v);
                v = 8453920;
            }
            textRenderer = this.minecraft.textRenderer;
            textRenderer.drawWithShadow(string, s + 86 - textRenderer.getStringWidth(string), l + 16 + 19 * q + 7, v);
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
            list.add("" + (Object)((Object)Formatting.WHITE) + (Object)((Object)Formatting.ITALIC) + I18n.translate("container.enchant.clue", enchantment.getName(n).asFormattedString()));
            if (!bl) {
                list.add("");
                if (this.minecraft.player.experienceLevel < m) {
                    list.add((Object)((Object)Formatting.RED) + I18n.translate("container.enchant.level.requirement", ((EnchantingTableContainer)this.container).enchantmentPower[l]));
                } else {
                    String string = o == 1 ? I18n.translate("container.enchant.lapis.one", new Object[0]) : I18n.translate("container.enchant.lapis.many", o);
                    Formatting formatting = k >= o ? Formatting.GRAY : Formatting.RED;
                    list.add((Object)((Object)formatting) + "" + string);
                    string = o == 1 ? I18n.translate("container.enchant.level.one", new Object[0]) : I18n.translate("container.enchant.level.many", o);
                    list.add((Object)((Object)Formatting.GRAY) + "" + string);
                }
            }
            this.renderTooltip(list, i, j);
            break;
        }
    }

    public void doTick() {
        ItemStack itemStack = ((EnchantingTableContainer)this.container).getSlot(0).getStack();
        if (!ItemStack.areEqualIgnoreDamage(itemStack, this.stack)) {
            this.stack = itemStack;
            do {
                this.approximatePageAngle += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.nextPageAngle <= this.approximatePageAngle + 1.0f && this.nextPageAngle >= this.approximatePageAngle - 1.0f);
        }
        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = false;
        for (int i = 0; i < 3; ++i) {
            if (((EnchantingTableContainer)this.container).enchantmentPower[i] == 0) continue;
            bl = true;
        }
        this.nextPageTurningSpeed = bl ? (this.nextPageTurningSpeed += 0.2f) : (this.nextPageTurningSpeed -= 0.2f);
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0f, 1.0f);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4f;
        float g = 0.2f;
        f = MathHelper.clamp(f, -0.2f, 0.2f);
        this.pageRotationSpeed += (f - this.pageRotationSpeed) * 0.9f;
        this.nextPageAngle += this.pageRotationSpeed;
    }
}

