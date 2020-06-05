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
import net.minecraft.text.StringRenderable;
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
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        int k = (int)this.client.getWindow().getScaleFactor();
        RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
        RenderSystem.translatef(-0.34f, 0.23f, 0.0f);
        RenderSystem.multMatrix(Matrix4f.viewboxMatrix(90.0, 1.3333334f, 9.0f, 80.0f));
        RenderSystem.matrixMode(5888);
        matrices.push();
        MatrixStack.Entry entry = matrices.peek();
        entry.getModel().loadIdentity();
        entry.getNormal().loadIdentity();
        matrices.translate(0.0, 3.3f, 1984.0);
        float f = 5.0f;
        matrices.scale(5.0f, 5.0f, 5.0f);
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(20.0f));
        float g = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
        matrices.translate((1.0f - g) * 0.2f, (1.0f - g) * 0.1f, (1.0f - g) * 0.25f);
        float h = -(1.0f - g) * 90.0f - 90.0f;
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f));
        float l = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle) + 0.25f;
        float m = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle) + 0.75f;
        l = (l - (float)MathHelper.fastFloor(l)) * 1.6f - 0.3f;
        m = (m - (float)MathHelper.fastFloor(m)) * 1.6f - 0.3f;
        if (l < 0.0f) {
            l = 0.0f;
        }
        if (m < 0.0f) {
            m = 0.0f;
        }
        if (l > 1.0f) {
            l = 1.0f;
        }
        if (m > 1.0f) {
            m = 1.0f;
        }
        RenderSystem.enableRescaleNormal();
        BOOK_MODEL.setPageAngles(0.0f, l, m, g);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        VertexConsumer vertexConsumer = immediate.getBuffer(BOOK_MODEL.getLayer(BOOK_TEXURE));
        BOOK_MODEL.render(matrices, vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        immediate.draw();
        matrices.pop();
        RenderSystem.matrixMode(5889);
        RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        DiffuseLighting.enableGuiDepthLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantingPhrases.getInstance().setSeed(((EnchantmentScreenHandler)this.handler).getSeed());
        int n = ((EnchantmentScreenHandler)this.handler).getLapisCount();
        for (int o = 0; o < 3; ++o) {
            int p = i + 60;
            int q = p + 20;
            this.setZOffset(0);
            this.client.getTextureManager().bindTexture(TEXTURE);
            int r = ((EnchantmentScreenHandler)this.handler).enchantmentPower[o];
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (r == 0) {
                this.drawTexture(matrices, p, j + 14 + 19 * o, 0, 185, 108, 19);
                continue;
            }
            String string = "" + r;
            int s = 86 - this.textRenderer.getWidth(string);
            StringRenderable stringRenderable = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, s);
            int t = 6839882;
            if (!(n >= o + 1 && this.client.player.experienceLevel >= r || this.client.player.abilities.creativeMode)) {
                this.drawTexture(matrices, p, j + 14 + 19 * o, 0, 185, 108, 19);
                this.drawTexture(matrices, p + 1, j + 15 + 19 * o, 16 * o, 239, 16, 16);
                this.textRenderer.drawTrimmed(stringRenderable, q, j + 16 + 19 * o, s, (t & 0xFEFEFE) >> 1);
                t = 4226832;
            } else {
                int u = mouseX - (i + 60);
                int v = mouseY - (j + 14 + 19 * o);
                if (u >= 0 && v >= 0 && u < 108 && v < 19) {
                    this.drawTexture(matrices, p, j + 14 + 19 * o, 0, 204, 108, 19);
                    t = 0xFFFF80;
                } else {
                    this.drawTexture(matrices, p, j + 14 + 19 * o, 0, 166, 108, 19);
                }
                this.drawTexture(matrices, p + 1, j + 15 + 19 * o, 16 * o, 223, 16, 16);
                this.textRenderer.drawTrimmed(stringRenderable, q, j + 16 + 19 * o, s, t);
                t = 8453920;
            }
            this.textRenderer.drawWithShadow(matrices, string, (float)(q + 86 - this.textRenderer.getWidth(string)), (float)(j + 16 + 19 * o + 7), t);
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

