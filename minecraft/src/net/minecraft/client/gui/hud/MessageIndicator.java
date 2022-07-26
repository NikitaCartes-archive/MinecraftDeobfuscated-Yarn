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
	private static final Text NOT_SECURE_TEXT = Text.translatable("chat.tag.not_secure").formatted(Formatting.UNDERLINE);
	private static final Text MODIFIED_TEXT = Text.translatable("chat.tag.modified").formatted(Formatting.UNDERLINE);
	private static final Text field_39922 = Text.translatable("chat.tag.filtered").formatted(Formatting.UNDERLINE);
	private static final int SYSTEM_COLOR = 10526880;
	private static final int NOT_SECURE_COLOR = 15224664;
	private static final int MODIFIED_COLOR = 15386724;
	private static final MessageIndicator SYSTEM = new MessageIndicator(10526880, null, null, "System");
	private static final MessageIndicator NOT_SECURE = new MessageIndicator(15224664, MessageIndicator.Icon.CHAT_NOT_SECURE, NOT_SECURE_TEXT, "Not Secure");
	private static final MessageIndicator FILTERED = new MessageIndicator(15386724, MessageIndicator.Icon.CHAT_MODIFIED, field_39922, "Filtered");
	static final Identifier CHAT_TAGS_TEXTURE = new Identifier("textures/gui/chat_tags.png");

	public static MessageIndicator system() {
		return SYSTEM;
	}

	public static MessageIndicator notSecure() {
		return NOT_SECURE;
	}

	public static MessageIndicator modified(String originalText) {
		Text text = Text.translatable("chat.tag.modified.original", originalText);
		Text text2 = Text.empty().append(MODIFIED_TEXT).append(ScreenTexts.LINE_BREAK).append(text);
		return new MessageIndicator(15386724, MessageIndicator.Icon.CHAT_MODIFIED, text2, "Modified");
	}

	public static MessageIndicator method_45071() {
		return FILTERED;
	}

	@Environment(EnvType.CLIENT)
	public static enum Icon {
		CHAT_NOT_SECURE(0, 0, 9, 9),
		CHAT_MODIFIED(9, 0, 9, 9);

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
