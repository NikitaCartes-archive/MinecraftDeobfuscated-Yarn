package net.minecraft.client.toast;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class VoteToast implements Toast {
	private static final int field_44288 = 200;
	private static final int field_44289 = 12;
	private static final int field_44290 = 10;
	private static final Text TITLE = Text.literal("New proposal received!").formatted(Formatting.LIGHT_PURPLE);
	private final VoteToast.ConcernLevel concernLevel;
	private final Text title;
	private final List<OrderedText> message;
	private final int width;
	public static final Text VOTING_KEY = Text.keybind("key.voting").formatted(Formatting.BOLD);
	private static final Map<VoteToast.ConcernLevel, List<Text>> MESSAGES = Map.of(
		VoteToast.ConcernLevel.LOW,
		List.of(
			Text.translatable("Press %s to open voting screen", VOTING_KEY),
			Text.translatable("To open voting screen, press %s", VOTING_KEY),
			Text.translatable("New vote started, press %s to cast your vote", VOTING_KEY)
		),
		VoteToast.ConcernLevel.MEDIUM,
		List.of(
			Text.translatable("A new proposal is waiting for your vote, press %s", VOTING_KEY),
			Text.translatable("Others are having fun while you are not pressing %s", VOTING_KEY),
			Text.translatable("Time to change some rules, press %s", VOTING_KEY),
			Text.translatable("You have new vote proposals to review, press %s to access!", VOTING_KEY)
		),
		VoteToast.ConcernLevel.CONCERNING,
		List.of(
			Text.translatable("Ok, so the whole idea of this release is to vote, so press %s", VOTING_KEY),
			Text.translatable("Somebody wants to tell you what you can and can not do, press %s to prevent that", VOTING_KEY),
			Text.translatable("At this point you are probably just waiting to see what happens next. Fine! But you can avoid that by pressing %s.", VOTING_KEY),
			Text.translatable("If you can't find %s, it's probably on your keyboard", VOTING_KEY),
			Text.translatable("Not pressing %s has been proven less fun that pressing %s", VOTING_KEY, VOTING_KEY),
			Text.translatable("Do you want more phantoms? That's how you get more phantoms! Press %s", VOTING_KEY),
			Text.translatable("Please, just press %s and be done with it!", VOTING_KEY),
			Text.translatable("Hot votes in your area! Press %s", VOTING_KEY)
		),
		VoteToast.ConcernLevel.WHY_ARE_YOU_NOT_DOING_IT,
		List.of(
			Text.translatable("PRESS %s ", VOTING_KEY)
				.append(Text.translatable("PRESS %s ", VOTING_KEY))
				.append(Text.translatable("PRESS %s ", VOTING_KEY))
				.append(Text.translatable("PRESS %s ", VOTING_KEY))
				.append(Text.translatable("PRESS %s ", VOTING_KEY))
				.append(Text.translatable("PRESS %s ", VOTING_KEY))
				.append(Text.translatable("PRESS %s ", VOTING_KEY))
				.append(Text.translatable("PRESS %s ", VOTING_KEY))
				.append(Text.translatable("PRESS %s ", VOTING_KEY))
				.append(Text.translatable("PRESS %s ", VOTING_KEY)),
			Text.translatable("WHYYYYYYYYYYYYYYYYYYYYYYYYY NO %s", VOTING_KEY),
			Text.translatable("DO YOU HAVE NO IDEA WHERE %s IS!?", VOTING_KEY),
			Text.translatable("gfdgh bbtvsvtfgfsgb a %sjhrst ujs  t 452423 r", VOTING_KEY),
			Text.translatable("Press %s to open voting screen", VOTING_KEY),
			Text.translatable(
				"%s %s %s",
				Text.literal("AAAAAAA").formatted(Formatting.RED, Formatting.OBFUSCATED),
				VOTING_KEY,
				Text.literal("AAAAAAA!").formatted(Formatting.BLUE, Formatting.OBFUSCATED)
			)
		)
	);

	public static Optional<VoteToast> createToast(MinecraftClient client, Random random, VoteToast.ConcernLevel concernLevel) {
		TextRenderer textRenderer = client.textRenderer;
		return Util.getRandomOrEmpty((List)MESSAGES.getOrDefault(concernLevel, List.of()), random).map(text -> {
			List<OrderedText> list = textRenderer.wrapLines(text, 200);
			int i = Math.max(200, list.stream().mapToInt(textRenderer::getWidth).max().orElse(200));
			return new VoteToast(concernLevel, TITLE, list, i + 30);
		});
	}

	private VoteToast(VoteToast.ConcernLevel concernLevel, Text title, List<OrderedText> message, int width) {
		this.concernLevel = concernLevel;
		this.title = title;
		this.message = message;
		this.width = width;
	}

	private static ImmutableList<OrderedText> asOrderedTextList(@Nullable Text text) {
		return text == null ? ImmutableList.of() : ImmutableList.of(text.asOrderedText());
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return 20 + Math.max(this.message.size(), 1) * 12;
	}

	@Override
	public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = this.getWidth();
		if (i == 160 && this.message.size() <= 1) {
			DrawableHelper.drawTexture(matrices, 0, 0, 0, 64, i, this.getHeight());
		} else {
			int j = this.getHeight();
			int k = 28;
			int l = Math.min(4, j - 28);
			this.method_50938(matrices, manager, i, 0, 0, 28);

			for (int m = 28; m < j - l; m += 10) {
				this.method_50938(matrices, manager, i, 16, m, Math.min(16, j - m - l));
			}

			this.method_50938(matrices, manager, i, 32 - l, j - l, l);
		}

		manager.getClient().textRenderer.draw(matrices, this.title, 18.0F, 7.0F, -256);

		for (int j = 0; j < this.message.size(); j++) {
			manager.getClient().textRenderer.draw(matrices, (OrderedText)this.message.get(j), 18.0F, (float)(18 + j * 12), -1);
		}

		return startTime > (long)(50 * this.concernLevel.toastDuration) ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
	}

	private void method_50938(MatrixStack matrices, ToastManager manager, int i, int j, int y, int height) {
		int k = j == 0 ? 20 : 5;
		int l = Math.min(60, i - k);
		DrawableHelper.drawTexture(matrices, 0, y, 0, 64 + j, k, height);

		for (int m = k; m < i - l; m += 64) {
			DrawableHelper.drawTexture(matrices, m, y, 32, 64 + j, Math.min(64, i - m - l), height);
		}

		DrawableHelper.drawTexture(matrices, i - l, y, 160 - l, 64 + j, l, height);
	}

	public VoteToast.ConcernLevel getType() {
		return this.concernLevel;
	}

	@Environment(EnvType.CLIENT)
	public static enum ConcernLevel {
		LOW(300, 2400, 100),
		MEDIUM(200, 2400, 60),
		CONCERNING(100, 1200, 40),
		WHY_ARE_YOU_NOT_DOING_IT(10, 99999, 20);

		private final int cooldown;
		private final int levelTickLength;
		final int toastDuration;

		private ConcernLevel(int cooldown, int levelTickLength, int toastDuration) {
			this.cooldown = cooldown;
			this.levelTickLength = levelTickLength;
			this.toastDuration = toastDuration;
		}

		public static Optional<VoteToast.ConcernLevel> getOptionalConcernLevel(int ticksSinceOpenedVoteScreen) {
			VoteToast.ConcernLevel concernLevel = getConcernLevel(ticksSinceOpenedVoteScreen);
			return ticksSinceOpenedVoteScreen % concernLevel.cooldown == 0 ? Optional.of(concernLevel) : Optional.empty();
		}

		private static VoteToast.ConcernLevel getConcernLevel(int ticksSinceOpenedVoteScreen) {
			for (VoteToast.ConcernLevel concernLevel : values()) {
				ticksSinceOpenedVoteScreen -= concernLevel.levelTickLength;
				if (ticksSinceOpenedVoteScreen < 0) {
					return concernLevel;
				}
			}

			return WHY_ARE_YOU_NOT_DOING_IT;
		}
	}
}
