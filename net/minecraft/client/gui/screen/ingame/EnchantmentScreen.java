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
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class EnchantmentScreen
extends HandledScreen<EnchantmentScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/enchanting_table.png");
    private static final Identifier BOOK_TEXURE = new Identifier("textures/entity/enchanting_table_book.png");
    private static final BookModel BOOK_MODEL = new BookModel();
    private final Random random = new Random();
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack stack = ItemStack.EMPTY;

    public EnchantmentScreen(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int i, int j) {
        this.textRenderer.draw(matrixStack, this.title, 12.0f, 5.0f, 0x404040);
        this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 8.0f, (float)(this.backgroundHeight - 96 + 2), 0x404040);
    }

    @Override
    public void tick() {
        super.tick();
        this.doTick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        for (int k = 0; k < 3; ++k) {
            double d = mouseX - (double)(i + 60);
            double e = mouseY - (double)(j + 14 + 19 * k);
            if (!(d >= 0.0) || !(e >= 0.0) || !(d < 108.0) || !(e < 19.0) || !((EnchantmentScreenHandler)this.handler).onButtonClick(this.client.player, k)) continue;
            this.client.interactionManager.clickButton(((EnchantmentScreenHandler)this.handler).syncId, k);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float f, int mouseY, int i) {
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrixStack, j, k, 0, 0, this.backgroundWidth, this.backgroundHeight);
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        int l = (int)this.client.getWindow().getScaleFactor();
        RenderSystem.viewport((this.width - 320) / 2 * l, (this.height - 240) / 2 * l, 320 * l, 240 * l);
        RenderSystem.translatef(-0.34f, 0.23f, 0.0f);
        RenderSystem.multMatrix(Matrix4f.viewboxMatrix(90.0, 1.3333334f, 9.0f, 80.0f));
        RenderSystem.matrixMode(5888);
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
        float m = -(1.0f - h) * 90.0f - 90.0f;
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(m));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f));
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
        BOOK_MODEL.setPageAngles(0.0f, n, o, h);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        VertexConsumer vertexConsumer = immediate.getBuffer(BOOK_MODEL.getLayer(BOOK_TEXURE));
        BOOK_MODEL.render(matrixStack, vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        immediate.draw();
        matrixStack.pop();
        RenderSystem.matrixMode(5889);
        RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        DiffuseLighting.enableGuiDepthLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantingPhrases.getInstance().setSeed(((EnchantmentScreenHandler)this.handler).getSeed());
        int p = ((EnchantmentScreenHandler)this.handler).getLapisCount();
        for (int q = 0; q < 3; ++q) {
            int r = j + 60;
            int s = r + 20;
            this.setZOffset(0);
            this.client.getTextureManager().bindTexture(TEXTURE);
            int t = ((EnchantmentScreenHandler)this.handler).enchantmentPower[q];
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (t == 0) {
                this.drawTexture(matrixStack, r, k + 14 + 19 * q, 0, 185, 108, 19);
                continue;
            }
            String string = "" + t;
            int u = 86 - this.textRenderer.getWidth(string);
            MutableText text = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, u);
            int v = 6839882;
            if (!(p >= q + 1 && this.client.player.experienceLevel >= t || this.client.player.abilities.creativeMode)) {
                this.drawTexture(matrixStack, r, k + 14 + 19 * q, 0, 185, 108, 19);
                this.drawTexture(matrixStack, r + 1, k + 15 + 19 * q, 16 * q, 239, 16, 16);
                this.textRenderer.drawTrimmed(text, s, k + 16 + 19 * q, u, (v & 0xFEFEFE) >> 1);
                v = 4226832;
            } else {
                int w = mouseY - (j + 60);
                int x = i - (k + 14 + 19 * q);
                if (w >= 0 && x >= 0 && w < 108 && x < 19) {
                    this.drawTexture(matrixStack, r, k + 14 + 19 * q, 0, 204, 108, 19);
                    v = 0xFFFF80;
                } else {
                    this.drawTexture(matrixStack, r, k + 14 + 19 * q, 0, 166, 108, 19);
                }
                this.drawTexture(matrixStack, r + 1, k + 15 + 19 * q, 16 * q, 223, 16, 16);
                this.textRenderer.drawTrimmed(text, s, k + 16 + 19 * q, u, v);
                v = 8453920;
            }
            this.textRenderer.drawWithShadow(matrixStack, string, (float)(s + 86 - this.textRenderer.getWidth(string)), (float)(k + 16 + 19 * q + 7), v);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        delta = this.client.getTickDelta();
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        boolean bl = this.client.player.abilities.creativeMode;
        int i = ((EnchantmentScreenHandler)this.handler).getLapisCount();
        for (int j = 0; j < 3; ++j) {
            int k = ((EnchantmentScreenHandler)this.handler).enchantmentPower[j];
            Enchantment enchantment = Enchantment.byRawId(((EnchantmentScreenHandler)this.handler).enchantmentId[j]);
            int l = ((EnchantmentScreenHandler)this.handler).enchantmentLevel[j];
            int m = j + 1;
            if (!this.isPointWithinBounds(60, 14 + 19 * j, 108, 17, mouseX, mouseY) || k <= 0 || l < 0 || enchantment == null) continue;
            ArrayList<Text> list = Lists.newArrayList();
            list.add(new TranslatableText("container.enchant.clue", enchantment.getName(l)).formatted(Formatting.WHITE, Formatting.ITALIC));
            if (!bl) {
                list.add(LiteralText.EMPTY);
                if (this.client.player.experienceLevel < k) {
                    list.add(new TranslatableText("container.enchant.level.requirement", ((EnchantmentScreenHandler)this.handler).enchantmentPower[j]).formatted(Formatting.RED));
                } else {
                    TranslatableText mutableText = m == 1 ? new TranslatableText("container.enchant.lapis.one") : new TranslatableText("container.enchant.lapis.many", m);
                    list.add(mutableText.formatted(i >= m ? Formatting.GRAY : Formatting.RED));
                    TranslatableText mutableText2 = m == 1 ? new TranslatableText("container.enchant.level.one") : new TranslatableText("container.enchant.level.many", m);
                    list.add(mutableText2.formatted(Formatting.GRAY));
                }
            }
            this.renderTooltip(matrices, list, mouseX, mouseY);
            break;
        }
    }

    public void doTick() {
        ItemStack itemStack = ((EnchantmentScreenHandler)this.handler).getSlot(0).getStack();
        if (!ItemStack.areEqual(itemStack, this.stack)) {
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
            if (((EnchantmentScreenHandler)this.handler).enchantmentPower[i] == 0) continue;
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

