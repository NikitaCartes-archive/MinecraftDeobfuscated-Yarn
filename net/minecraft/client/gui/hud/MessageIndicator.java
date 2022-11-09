/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record MessageIndicator(int indicatorColor, @Nullable Icon icon, @Nullable Text text, @Nullable String loggedName) {
    private static final Text SYSTEM_TEXT = Text.translatable("chat.tag.system");
    private static final Text field_41092 = Text.translatable("chat.tag.system_single_player");
    private static final Text NOT_SECURE_TEXT = Text.translatable("chat.tag.not_secure");
    private static final Text MODIFIED_TEXT = Text.translatable("chat.tag.modified");
    private static final int NOT_SECURE_COLOR = 0xD0D0D0;
    private static final int MODIFIED_COLOR = 0x606060;
    private static final MessageIndicator SYSTEM = new MessageIndicator(0xD0D0D0, null, SYSTEM_TEXT, "System");
    private static final MessageIndicator field_41093 = new MessageIndicator(0xD0D0D0, null, field_41092, "System");
    private static final MessageIndicator NOT_SECURE = new MessageIndicator(0xD0D0D0, null, NOT_SECURE_TEXT, "Not Secure");
    static final Identifier CHAT_TAGS_TEXTURE = new Identifier("textures/gui/chat_tags.png");

    public static MessageIndicator system() {
        return SYSTEM;
    }

    public static MessageIndicator method_47391() {
        return field_41093;
    }

    public static MessageIndicator notSecure() {
        return NOT_SECURE;
    }

    public static MessageIndicator modified(String originalText) {
        MutableText text = Text.literal(originalText).formatted(Formatting.GRAY);
        MutableText text2 = Text.empty().append(MODIFIED_TEXT).append(ScreenTexts.LINE_BREAK).append(text);
        return new MessageIndicator(0x606060, Icon.CHAT_MODIFIED, text2, "Modified");
    }

    @Nullable
    public Icon icon() {
        return this.icon;
    }

    @Nullable
    public Text text() {
        return this.text;
    }

    @Nullable
    public String loggedName() {
        return this.loggedName;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Icon {
        CHAT_MODIFIED(0, 0, 9, 9);

        public final int u;
        public final int v;
        public final int width;
        public final int height;

        private Icon(int u, int v, int width, int height) {
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
        }

        public void draw(MatrixStack matrices, int x, int y) {
            RenderSystem.setShaderTexture(0, CHAT_TAGS_TEXTURE);
            DrawableHelper.drawTexture(matrices, x, y, this.u, this.v, this.width, this.height, 32, 32);
        }
    }
}

