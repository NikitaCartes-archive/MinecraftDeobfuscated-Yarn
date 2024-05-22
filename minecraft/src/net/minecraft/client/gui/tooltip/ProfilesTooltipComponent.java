package net.minecraft.client.gui.tooltip;

import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.util.Colors;

@Environment(EnvType.CLIENT)
public class ProfilesTooltipComponent implements TooltipComponent {
	private static final int field_52140 = 10;
	private static final int field_52141 = 2;
	private final List<ProfileResult> profiles;

	public ProfilesTooltipComponent(ProfilesTooltipComponent.ProfilesData data) {
		this.profiles = data.profiles();
	}

	@Override
	public int getHeight() {
		return this.profiles.size() * 12 + 2;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		int i = 0;

		for (ProfileResult profileResult : this.profiles) {
			int j = textRenderer.getWidth(profileResult.profile().getName());
			if (j > i) {
				i = j;
			}
		}

		return i + 10 + 6;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
		for (int i = 0; i < this.profiles.size(); i++) {
			ProfileResult profileResult = (ProfileResult)this.profiles.get(i);
			int j = y + 2 + i * 12;
			PlayerSkinDrawer.draw(context, MinecraftClient.getInstance().getSkinProvider().getSkinTextures(profileResult.profile()), x + 2, j, 10);
			context.drawTextWithShadow(textRenderer, profileResult.profile().getName(), x + 10 + 4, j + 2, Colors.WHITE);
		}
	}

	@Environment(EnvType.CLIENT)
	public static record ProfilesData(List<ProfileResult> profiles) implements TooltipData {
	}
}
