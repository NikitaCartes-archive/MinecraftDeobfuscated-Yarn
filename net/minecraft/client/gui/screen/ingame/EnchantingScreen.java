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
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

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
        DiffuseLighting.disableGuiDepthLighting();
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
        RenderSystem.multMatrix(Matrix4f.viewboxMatrix(90.0, 1.3333334f, 9.0f, 80.0f));
        RenderSystem.matrixMode(5888);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        MatrixStack.Entry entry = matrixStack.peek();
        entry.getModel().loadIdentity();
        entry.getNormal().loadIdentity();
        matrixStack.translate(0.0, 3.3f, 1984.0);
        float g = 5.0f;
        matrixStack.scale(5.0f, 5.0f, 5.0f);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(20.0f));
        float h = MathHelper.lerp(f, this.pageTurningSpeed, this.nextPageTurningSpeed);
        matrixStack.translate((1.0f - h) * 0.2f, (1.0f - h) * 0.1f, (1.0f - h) * 0.25f);
        float n = -(1.0f - h) * 90.0f - 90.0f;
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(n));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f));
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
        RenderSystem.enableRescaleNormal();
        bookModel.setPageAngles(0.0f, o, p, h);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        VertexConsumer vertexConsumer = immediate.getBuffer(bookModel.getLayer(BOOK_TEXURE));
        bookModel.render(matrixStack, vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        immediate.draw();
        matrixStack.pop();
        RenderSystem.matrixMode(5889);
        RenderSystem.viewport(0, 0, this.minecraft.getWindow().getFramebufferWidth(), this.minecraft.getWindow().getFramebufferHeight());
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        DiffuseLighting.enableGuiDepthLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantingPhrases.getInstance().setSeed(((EnchantingTableContainer)this.container).getSeed());
        int q = ((EnchantingTableContainer)this.container).getLapisCount();
        for (int r = 0; r < 3; ++r) {
            int s = k + 60;
            int t = s + 20;
            this.setBlitOffset(0);
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            int u = ((EnchantingTableContainer)this.container).enchantmentPower[r];
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
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
                textRenderer.drawTrimmed(string2, t, l + 16 + 19 * r, v, (w & 0xFEFEFE) >> 1);
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
                textRenderer.drawTrimmed(string2, t, l + 16 + 19 * r, v, w);
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

