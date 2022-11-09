package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record MessageIndicator(int indicatorColor, @Nullable MessageIndicator.Icon icon, @Nullable Text text, @Nullable String loggedName) {
	private static final Text SYSTEM_TEXT = Text.translatable("chat.tag.system");
	private static final Text field_41092 = Text.translatable("chat.tag.system_single_player");
	private static final Text NOT_SECURE_TEXT = Text.translatable("chat.tag.not_secure");
	private static final Text MODIFIED_TEXT = Text.translatable("chat.tag.modified");
	private static final int NOT_SECURE_COLOR = 13684944;
	private static final int MODIFIED_COLOR = 6316128;
	private static final MessageIndicator SYSTEM = new MessageIndicator(13684944, null, SYSTEM_TEXT, "System");
	private static final MessageIndicator field_41093 = new MessageIndicator(13684944, null, field_41092, "System");
	private static final MessageIndicator NOT_SECURE = new MessageIndicator(13684944, null, NOT_SECURE_TEXT, "Not Secure");
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
		Text text = Text.literal(originalText).formatted(Formatting.GRAY);
		Text text2 = Text.empty().append(MODIFIED_TEXT).append(ScreenTexts.LINE_BREAK).append(text);
		return new MessageIndicator(6316128, MessageIndicator.Icon.CHAT_MODIFIED, text2, "Modified");
	}

	@Environment(EnvType.CLIENT)
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
			RenderSystem.setShaderTexture(0, MessageIndicator.CHAT_TAGS_TEXTURE);
			DrawableHelper.drawTexture(matrices, x, y, (float)this.u, (float)this.v, this.width, this.height, 32, 32);
		}
	}
}
