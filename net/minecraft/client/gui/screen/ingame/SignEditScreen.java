/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SignType;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public class SignEditScreen
extends Screen {
    private final SignBlockEntity sign;
    private int ticksSinceOpened;
    private int currentRow;
    private SelectionManager selectionManager;
    private SignType signType;
    private SignBlockEntityRenderer.SignModel model;
    private final String[] text = (String[])IntStream.range(0, 4).mapToObj(row -> sign.getTextOnRow(row, filtered)).map(Text::getString).toArray(String[]::new);

    public SignEditScreen(SignBlockEntity sign, boolean filtered) {
        super(new TranslatableText("sign.edit"));
        this.sign = sign;
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, ScreenTexts.DONE, button -> this.finishEditing()));
        this.sign.setEditable(false);
        this.selectionManager = new SelectionManager(() -> this.text[this.currentRow], text -> {
            this.text[this.currentRow] = text;
            this.sign.setTextOnRow(this.currentRow, new LiteralText((String)text));
        }, SelectionManager.makeClipboardGetter(this.client), SelectionManager.makeClipboardSetter(this.client), text -> this.client.textRenderer.getWidth((String)text) <= 90);
        BlockState blockState = this.sign.getCachedState();
        this.signType = SignBlockEntityRenderer.getSignType(blockState.getBlock());
        this.model = SignBlockEntityRenderer.createSignModel(this.client.getEntityModelLoader(), this.signType);
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(new UpdateSignC2SPacket(this.sign.getPos(), this.text[0], this.text[1], this.text[2], this.text[3]));
        }
        this.sign.setEditable(true);
    }

    @Override
    public void tick() {
        ++this.ticksSinceOpened;
        if (!this.sign.getType().supports(this.sign.getCachedState())) {
            this.finishEditing();
        }
    }

    private void finishEditing() {
        this.sign.markDirty();
        this.client.openScreen(null);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        this.selectionManager.insert(chr);
        return true;
    }

    @Override
    public void onClose() {
        this.finishEditing();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_UP) {
            this.currentRow = this.currentRow - 1 & 3;
            this.selectionManager.putCursorAtEnd();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.currentRow = this.currentRow + 1 & 3;
            this.selectionManager.putCursorAtEnd();
            return true;
        }
        if (this.selectionManager.handleSpecialKey(keyCode)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int p;
        int o;
        String string;
        int m;
        DiffuseLighting.disableGuiDepthLighting();
        this.renderBackground(matrices);
        SignEditScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);
        matrices.push();
        matrices.translate(this.width / 2, 0.0, 50.0);
        float f = 93.75f;
        matrices.scale(93.75f, -93.75f, 93.75f);
        matrices.translate(0.0, -1.3125, 0.0);
        BlockState blockState = this.sign.getCachedState();
        boolean bl = blockState.getBlock() instanceof SignBlock;
        if (!bl) {
            matrices.translate(0.0, -0.3125, 0.0);
        }
        boolean bl2 = this.ticksSinceOpened / 6 % 2 == 0;
        float g = 0.6666667f;
        matrices.push();
        matrices.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getSignTextureId(this.signType);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(immediate, this.model::getLayer);
        this.model.stick.visible = bl;
        this.model.root.render(matrices, vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        float h = 0.010416667f;
        matrices.translate(0.0, 0.3333333432674408, 0.046666666865348816);
        matrices.scale(0.010416667f, -0.010416667f, 0.010416667f);
        int i = this.sign.getTextColor().getSignColor();
        int j = this.selectionManager.getSelectionStart();
        int k = this.selectionManager.getSelectionEnd();
        int l = this.currentRow * 10 - this.text.length * 5;
        Matrix4f matrix4f = matrices.peek().getModel();
        for (m = 0; m < this.text.length; ++m) {
            string = this.text[m];
            if (string == null) continue;
            if (this.textRenderer.isRightToLeft()) {
                string = this.textRenderer.mirror(string);
            }
            float n = -this.client.textRenderer.getWidth(string) / 2;
            this.client.textRenderer.draw(string, n, m * 10 - this.text.length * 5, i, false, matrix4f, immediate, false, 0, 0xF000F0, false);
            if (m != this.currentRow || j < 0 || !bl2) continue;
            o = this.client.textRenderer.getWidth(string.substring(0, Math.max(Math.min(j, string.length()), 0)));
            p = o - this.client.textRenderer.getWidth(string) / 2;
            if (j < string.length()) continue;
            this.client.textRenderer.draw("_", p, l, i, false, matrix4f, immediate, false, 0, 0xF000F0, false);
        }
        immediate.draw();
        for (m = 0; m < this.text.length; ++m) {
            string = this.text[m];
            if (string == null || m != this.currentRow || j < 0) continue;
            int q = this.client.textRenderer.getWidth(string.substring(0, Math.max(Math.min(j, string.length()), 0)));
            o = q - this.client.textRenderer.getWidth(string) / 2;
            if (bl2 && j < string.length()) {
                SignEditScreen.fill(matrices, o, l - 1, o + 1, l + this.client.textRenderer.fontHeight, 0xFF000000 | i);
            }
            if (k == j) continue;
            p = Math.min(j, k);
            int r = Math.max(j, k);
            int s = this.client.textRenderer.getWidth(string.substring(0, p)) - this.client.textRenderer.getWidth(string) / 2;
            int t = this.client.textRenderer.getWidth(string.substring(0, r)) - this.client.textRenderer.getWidth(string) / 2;
            int u = Math.min(s, t);
            int v = Math.max(s, t);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.disableTexture();
            RenderSystem.enableColorLogicOp();
            RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, u, l + this.client.textRenderer.fontHeight, 0.0f).color(0, 0, 255, 255).next();
            bufferBuilder.vertex(matrix4f, v, l + this.client.textRenderer.fontHeight, 0.0f).color(0, 0, 255, 255).next();
            bufferBuilder.vertex(matrix4f, v, l, 0.0f).color(0, 0, 255, 255).next();
            bufferBuilder.vertex(matrix4f, u, l, 0.0f).color(0, 0, 255, 255).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.disableColorLogicOp();
            RenderSystem.enableTexture();
        }
        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();
        super.render(matrices, mouseX, mouseY, delta);
    }
}

