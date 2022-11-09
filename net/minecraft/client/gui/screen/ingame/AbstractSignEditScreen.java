/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.SignType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractSignEditScreen
extends Screen {
    protected final SignBlockEntity blockEntity;
    protected final String[] text;
    protected final SignType signType;
    private int ticksSinceOpened;
    private int currentRow;
    private SelectionManager selectionManager;

    public AbstractSignEditScreen(SignBlockEntity blockEntity, boolean filtered) {
        this(blockEntity, filtered, Text.translatable("sign.edit"));
    }

    public AbstractSignEditScreen(SignBlockEntity blockEntity, boolean filtered, Text title) {
        super(title);
        this.signType = AbstractSignBlock.getSignType(blockEntity.getCachedState().getBlock());
        this.text = (String[])IntStream.range(0, 4).mapToObj(row -> blockEntity.getTextOnRow(row, filtered)).map(Text::getString).toArray(String[]::new);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.addDrawableChild(ButtonWidget.createBuilder(ScreenTexts.DONE, button -> this.finishEditing()).setPositionAndSize(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
        this.blockEntity.setEditable(false);
        this.selectionManager = new SelectionManager(() -> this.text[this.currentRow], rowText -> {
            this.text[this.currentRow] = rowText;
            this.blockEntity.setTextOnRow(this.currentRow, Text.literal(rowText));
        }, SelectionManager.makeClipboardGetter(this.client), SelectionManager.makeClipboardSetter(this.client), string -> this.client.textRenderer.getWidth((String)string) <= this.blockEntity.getMaxTextWidth());
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(new UpdateSignC2SPacket(this.blockEntity.getPos(), this.text[0], this.text[1], this.text[2], this.text[3]));
        }
        this.blockEntity.setEditable(true);
    }

    @Override
    public void tick() {
        ++this.ticksSinceOpened;
        if (!this.blockEntity.getType().supports(this.blockEntity.getCachedState())) {
            this.finishEditing();
        }
    }

    private void finishEditing() {
        this.blockEntity.markDirty();
        this.client.setScreen(null);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        this.selectionManager.insert(chr);
        return true;
    }

    @Override
    public void close() {
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
        DiffuseLighting.disableGuiDepthLighting();
        this.renderBackground(matrices);
        AbstractSignEditScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);
        this.renderSign(matrices);
        DiffuseLighting.enableGuiDepthLighting();
        super.render(matrices, mouseX, mouseY, delta);
    }

    protected abstract void renderSignBackground(MatrixStack var1, VertexConsumerProvider.Immediate var2, BlockState var3);

    protected abstract Vector3f getTextScale();

    protected void translateForRender(MatrixStack matrices, BlockState state) {
        matrices.translate((float)this.width / 2.0f, 90.0f, 50.0f);
    }

    private void renderSign(MatrixStack matrices) {
        VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
        BlockState blockState = this.blockEntity.getCachedState();
        matrices.push();
        this.translateForRender(matrices, blockState);
        matrices.push();
        this.renderSignBackground(matrices, immediate, blockState);
        matrices.pop();
        this.renderSignText(matrices, immediate);
        matrices.pop();
    }

    private void renderSignText(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers) {
        int p;
        int o;
        String string;
        int n;
        matrices.translate(0.0f, 0.0f, 4.0f);
        Vector3f vector3f = this.getTextScale();
        matrices.scale(vector3f.x(), vector3f.y(), vector3f.z());
        int i = this.blockEntity.getTextColor().getSignColor();
        boolean bl = this.ticksSinceOpened / 6 % 2 == 0;
        int j = this.selectionManager.getSelectionStart();
        int k = this.selectionManager.getSelectionEnd();
        int l = 4 * this.blockEntity.getTextLineHeight() / 2;
        int m = this.currentRow * this.blockEntity.getTextLineHeight() - l;
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        for (n = 0; n < this.text.length; ++n) {
            string = this.text[n];
            if (string == null) continue;
            if (this.textRenderer.isRightToLeft()) {
                string = this.textRenderer.mirror(string);
            }
            float f = -this.client.textRenderer.getWidth(string) / 2;
            this.client.textRenderer.draw(string, f, n * this.blockEntity.getTextLineHeight() - l, i, false, matrix4f, vertexConsumers, false, 0, 0xF000F0, false);
            if (n != this.currentRow || j < 0 || !bl) continue;
            o = this.client.textRenderer.getWidth(string.substring(0, Math.max(Math.min(j, string.length()), 0)));
            p = o - this.client.textRenderer.getWidth(string) / 2;
            if (j < string.length()) continue;
            this.client.textRenderer.draw("_", p, m, i, false, matrix4f, vertexConsumers, false, 0, 0xF000F0, false);
        }
        vertexConsumers.draw();
        for (n = 0; n < this.text.length; ++n) {
            string = this.text[n];
            if (string == null || n != this.currentRow || j < 0) continue;
            int q = this.client.textRenderer.getWidth(string.substring(0, Math.max(Math.min(j, string.length()), 0)));
            o = q - this.client.textRenderer.getWidth(string) / 2;
            if (bl && j < string.length()) {
                AbstractSignEditScreen.fill(matrices, o, m - 1, o + 1, m + this.blockEntity.getTextLineHeight(), 0xFF000000 | i);
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
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.disableTexture();
            RenderSystem.enableColorLogicOp();
            RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, u, m + this.blockEntity.getTextLineHeight(), 0.0f).color(0, 0, 255, 255).next();
            bufferBuilder.vertex(matrix4f, v, m + this.blockEntity.getTextLineHeight(), 0.0f).color(0, 0, 255, 255).next();
            bufferBuilder.vertex(matrix4f, v, m, 0.0f).color(0, 0, 255, 255).next();
            bufferBuilder.vertex(matrix4f, u, m, 0.0f).color(0, 0, 255, 255).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            RenderSystem.disableColorLogicOp();
            RenderSystem.enableTexture();
        }
    }
}

