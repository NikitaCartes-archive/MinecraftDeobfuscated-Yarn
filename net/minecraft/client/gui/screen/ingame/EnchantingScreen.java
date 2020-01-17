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
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
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
extends ContainerScreen<EnchantingTableContainer> {
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

    public EnchantingScreen(EnchantingTableContainer container, PlayerInventory inventory, Text title) {
        super(container, inventory, title);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        this.font.draw(this.title.asFormattedString(), 12.0f, 5.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    public void tick() {
        super.tick();
        this.doTick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        for (int k = 0; k < 3; ++k) {
            double d = mouseX - (double)(i + 60);
            double e = mouseY - (double)(j + 14 + 19 * k);
            if (!(d >= 0.0) || !(e >= 0.0) || !(d < 108.0) || !(e < 19.0) || !((EnchantingTableContainer)this.container).onButtonClick(this.minecraft.player, k)) continue;
            this.minecraft.interactionManager.clickButton(((EnchantingTableContainer)this.container).syncId, k);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        int k = (int)this.minecraft.getWindow().getScaleFactor();
        RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
        RenderSystem.translatef(-0.34f, 0.23f, 0.0f);
        RenderSystem.multMatrix(Matrix4f.viewboxMatrix(90.0, 1.3333334f, 9.0f, 80.0f));
        RenderSystem.matrixMode(5888);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        MatrixStack.Entry entry = matrixStack.peek();
        entry.getModel().loadIdentity();
        entry.getNormal().loadIdentity();
        matrixStack.translate(0.0, 3.3f, 1984.0);
        float f = 5.0f;
        matrixStack.scale(5.0f, 5.0f, 5.0f);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(20.0f));
        float g = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
        matrixStack.translate((1.0f - g) * 0.2f, (1.0f - g) * 0.1f, (1.0f - g) * 0.25f);
        float h = -(1.0f - g) * 90.0f - 90.0f;
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f));
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
        bookModel.setPageAngles(0.0f, l, m, g);
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
        int n = ((EnchantingTableContainer)this.container).getLapisCount();
        for (int o = 0; o < 3; ++o) {
            int p = i + 60;
            int q = p + 20;
            this.setBlitOffset(0);
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            int r = ((EnchantingTableContainer)this.container).enchantmentPower[o];
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (r == 0) {
                this.blit(p, j + 14 + 19 * o, 0, 185, 108, 19);
                continue;
            }
            String string = "" + r;
            int s = 86 - this.font.getStringWidth(string);
            String string2 = EnchantingPhrases.getInstance().generatePhrase(this.font, s);
            TextRenderer textRenderer = this.minecraft.getFontManager().getTextRenderer(MinecraftClient.ALT_TEXT_RENDERER_ID);
            int t = 6839882;
            if (!(n >= o + 1 && this.minecraft.player.experienceLevel >= r || this.minecraft.player.abilities.creativeMode)) {
                this.blit(p, j + 14 + 19 * o, 0, 185, 108, 19);
                this.blit(p + 1, j + 15 + 19 * o, 16 * o, 239, 16, 16);
                textRenderer.drawTrimmed(string2, q, j + 16 + 19 * o, s, (t & 0xFEFEFE) >> 1);
                t = 4226832;
            } else {
                int u = mouseX - (i + 60);
                int v = mouseY - (j + 14 + 19 * o);
                if (u >= 0 && v >= 0 && u < 108 && v < 19) {
                    this.blit(p, j + 14 + 19 * o, 0, 204, 108, 19);
                    t = 0xFFFF80;
                } else {
                    this.blit(p, j + 14 + 19 * o, 0, 166, 108, 19);
                }
                this.blit(p + 1, j + 15 + 19 * o, 16 * o, 223, 16, 16);
                textRenderer.drawTrimmed(string2, q, j + 16 + 19 * o, s, t);
                t = 8453920;
            }
            textRenderer = this.minecraft.textRenderer;
            textRenderer.drawWithShadow(string, q + 86 - textRenderer.getStringWidth(string), j + 16 + 19 * o + 7, t);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        delta = this.minecraft.getTickDelta();
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
        boolean bl = this.minecraft.player.abilities.creativeMode;
        int i = ((EnchantingTableContainer)this.container).getLapisCount();
        for (int j = 0; j < 3; ++j) {
            int k = ((EnchantingTableContainer)this.container).enchantmentPower[j];
            Enchantment enchantment = Enchantment.byRawId(((EnchantingTableContainer)this.container).enchantmentId[j]);
            int l = ((EnchantingTableContainer)this.container).enchantmentLevel[j];
            int m = j + 1;
            if (!this.isPointWithinBounds(60, 14 + 19 * j, 108, 17, mouseX, mouseY) || k <= 0 || l < 0 || enchantment == null) continue;
            ArrayList<String> list = Lists.newArrayList();
            list.add("" + (Object)((Object)Formatting.WHITE) + (Object)((Object)Formatting.ITALIC) + I18n.translate("container.enchant.clue", enchantment.getName(l).asFormattedString()));
            if (!bl) {
                list.add("");
                if (this.minecraft.player.experienceLevel < k) {
                    list.add((Object)((Object)Formatting.RED) + I18n.translate("container.enchant.level.requirement", ((EnchantingTableContainer)this.container).enchantmentPower[j]));
                } else {
                    String string = m == 1 ? I18n.translate("container.enchant.lapis.one", new Object[0]) : I18n.translate("container.enchant.lapis.many", m);
                    Formatting formatting = i >= m ? Formatting.GRAY : Formatting.RED;
                    list.add((Object)((Object)formatting) + "" + string);
                    string = m == 1 ? I18n.translate("container.enchant.level.one", new Object[0]) : I18n.translate("container.enchant.level.many", m);
                    list.add((Object)((Object)Formatting.GRAY) + "" + string);
                }
            }
            this.renderTooltip(list, mouseX, mouseY);
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

