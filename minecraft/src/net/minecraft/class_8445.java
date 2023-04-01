package net.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.vote.PendingVotesScreen;
import net.minecraft.client.gui.screen.ingame.vote.VotingScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;

@Environment(EnvType.CLIENT)
public class class_8445 extends AlwaysSelectedEntryListWidget.Entry<class_8445> {
	public static final Comparator<class_8445> field_44319 = Comparator.comparing(arg -> arg.field_44326);
	public static final Comparator<class_8445> field_44320 = Comparator.comparing(class_8445::method_50965);
	public static final Comparator<class_8445> field_44321 = Comparator.comparing(arg -> arg.field_44332);
	public static final Comparator<class_8445> field_44322 = Comparator.comparing(arg -> arg.field_44329 ? 0 : 1);
	public static final Comparator<class_8445> field_44323 = field_44322.thenComparing(field_44321).thenComparing(field_44319);
	private final MinecraftClient field_44324;
	private final PendingVotesScreen field_44325;
	private final UUID field_44326;
	private final class_8471 field_44327;
	@Nullable
	private class_8471.class_8474 field_44328;
	private final boolean field_44329;
	@Nullable
	private final Tooltip field_44330;
	private final MultilineText field_44331;
	private final String field_44332;

	public class_8445(
		MinecraftClient minecraftClient,
		PendingVotesScreen pendingVotesScreen,
		boolean bl,
		int i,
		UUID uUID,
		class_8471 arg,
		class_8471.class_8474 arg2,
		@Nullable Tooltip tooltip
	) {
		this.field_44324 = minecraftClient;
		this.field_44325 = pendingVotesScreen;
		this.field_44326 = uUID;
		this.field_44327 = arg;
		this.field_44328 = arg2;
		this.field_44329 = bl;
		this.field_44330 = tooltip;
		this.field_44332 = arg2.method_51076().getString();
		String string = this.method_50964();
		int j = minecraftClient.textRenderer.getWidth(string);
		int k = i - j - 8;
		this.field_44331 = MultilineText.create(minecraftClient.textRenderer, arg2.method_51076(), k);
	}

	@Override
	public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		RenderSystem.setShaderTexture(0, PendingVotesScreen.TEXTURE);
		int i;
		if (!this.field_44329 || this.field_44328 == null) {
			i = 66;
		} else if (!this.isFocused() && !hovered) {
			i = 0;
		} else {
			i = 33;
		}

		DrawableHelper.drawTexture(matrices, x, y, 0.0F, (float)(36 + i), 220, 33, 256, 256);
		int j = x + 4;
		int k = y + 4;
		this.field_44331.drawWithShadow(matrices, j, k, 9, -1);
		String string = this.method_50964();
		int l = this.field_44324.textRenderer.getWidth(string);
		this.field_44324.textRenderer.draw(matrices, string, (float)(j + (entryWidth - l - 8)), (float)k, -1);
		if (hovered && this.field_44330 != null) {
			this.field_44325.setTooltip(this.field_44330.getLines(this.field_44324));
		}
	}

	private String method_50964() {
		return StringHelper.formatTicks(this.method_50965());
	}

	private long method_50965() {
		if (this.field_44328 == null) {
			return 0L;
		} else {
			long l = this.field_44324.world != null ? this.field_44324.world.getTime() : 0L;
			return Math.max(0L, this.field_44328.method_51079(l));
		}
	}

	@Override
	public Text getNarration() {
		return Text.literal("blah");
	}

	public boolean method_50961() {
		this.field_44328 = this.field_44327.method_51074(this.field_44326);
		return this.field_44328 != null;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			if (this.field_44328 != null) {
				this.field_44324
					.setScreen(
						new VotingScreen(
							this.field_44324.player.playerScreenHandler, this.field_44324.player.getInventory(), this.field_44326, this.field_44327, this.field_44328
						)
					);
			}

			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}
}
