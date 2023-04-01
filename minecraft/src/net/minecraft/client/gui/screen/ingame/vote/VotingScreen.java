package net.minecraft.client.gui.screen.ingame.vote;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8367;
import net.minecraft.class_8373;
import net.minecraft.class_8390;
import net.minecraft.class_8444;
import net.minecraft.class_8471;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class VotingScreen extends HandledScreen<VotingScreen.Handler> implements class_8444 {
	protected static final Identifier TEXTURE = new Identifier("textures/gui/voting.png");
	private static final int field_44348 = 205;
	private static final int field_44349 = 68;
	public static final Text field_44346 = Text.translatable("vote.voted").formatted(Formatting.GREEN);
	public static final Text field_44347 = Text.translatable("vote.no_more_votes");
	private final UUID field_44350;
	private final class_8471 field_44351;
	private class_8471.class_8474 field_44352;
	final List<VotingScreen.class_8450> field_44353;
	private final UUID field_44354;
	int field_44355;
	private Text field_44356 = Text.empty();
	Text field_44357 = Text.empty();
	private MultilineText field_44358 = MultilineText.EMPTY;
	@Nullable
	VotingScreen.class_8450 field_44359;
	@Nullable
	private class_8471.class_8472 field_44360;
	@Nullable
	private class_8471.class_8472 field_44361;
	private VotingScreen.class_8451 field_44362;
	private VotingScreen.class_8451 field_44363;
	private VotingScreen.class_8452 field_44364;

	public VotingScreen(PlayerScreenHandler playerScreenHandler, PlayerInventory playerInventory, UUID uUID, class_8471 arg, class_8471.class_8474 arg2) {
		super(new VotingScreen.Handler(playerScreenHandler), playerInventory, Text.translatable("gui.voting.title"));
		this.field_44350 = uUID;
		this.field_44351 = arg;
		this.field_44352 = arg2;
		this.field_44354 = playerInventory.player.getUuid();
		this.backgroundWidth = 231;
		this.backgroundHeight = 219;
		class_8367 lv = arg2.method_51082();
		int i = lv.options().size();
		this.field_44353 = Streams.<Entry, VotingScreen.class_8450>mapWithIndex(
				lv.options().entrySet().stream().sorted(Entry.comparingByKey(class_8373.field_43985)), (entry, l) -> {
					Text text = Text.translatable("vote.option_vote_title", lv.header().displayName(), l + 1L, i);
					return new VotingScreen.class_8450((class_8373)entry.getKey(), text, (class_8367.class_8368)entry.getValue());
				}
			)
			.toList();
	}

	@Override
	protected void init() {
		super.init();
		this.field_44362 = this.addDrawableChild(new VotingScreen.class_8451(this.x + 9, this.y + 4, Text.translatable("gui.voting.prev"), 0) {
			@Override
			public void onPress() {
				if (VotingScreen.this.field_44355 > 0) {
					VotingScreen.this.field_44355--;
				}

				VotingScreen.this.method_50977();
			}
		});
		this.field_44363 = this.addDrawableChild(new VotingScreen.class_8451(this.x + 205, this.y + 4, Text.translatable("gui.voting.next"), 32) {
			@Override
			public void onPress() {
				if (VotingScreen.this.field_44355 < VotingScreen.this.field_44353.size() - 1) {
					VotingScreen.this.field_44355++;
				}

				VotingScreen.this.method_50977();
			}
		});
		this.field_44364 = this.addDrawableChild(new VotingScreen.class_8452(this.x + 26, this.y + 106, Text.translatable("gui.voting.do_it"), this.textRenderer) {
			int field_44367;

			@Override
			public void onPress() {
				if (VotingScreen.this.field_44359 != null) {
					class_8373 lv = VotingScreen.this.field_44359.id;
					this.field_44367 = VotingScreen.this.client.player.networkHandler.method_51006(lv, (i, optional) -> {
						if (i == this.field_44367) {
							VotingScreen.this.field_44357 = DataFixUtils.orElse(optional.map(text -> text.copy().formatted(Formatting.RED)), VotingScreen.field_44346);
						}
					});
				}
			}
		});
		this.method_50977();
	}

	void method_50977() {
		this.field_44359 = (VotingScreen.class_8450)this.field_44353.get(this.field_44355);
		this.field_44362.active = this.field_44355 > 0;
		this.field_44363.active = this.field_44355 < this.field_44353.size() - 1;
		boolean bl = this.method_50979(this.field_44359.id);
		this.field_44357 = (Text)(bl ? Text.empty() : field_44347);
		this.field_44356 = this.field_44359.display;
		this.field_44358 = MultilineText.create(this.textRenderer, this.field_44359.data().displayName(), 205);
	}

	@Override
	public void method_50959() {
		class_8471.class_8474 lv = this.field_44351.method_51074(this.field_44350);
		if (lv == null) {
			this.close();
		} else {
			this.field_44352 = lv;
			if (this.field_44359 != null) {
				this.method_50979(this.field_44359.id);
			}
		}
	}

	private boolean method_50979(class_8373 arg) {
		this.field_44360 = this.field_44352.method_51080(this.field_44354);
		this.field_44361 = this.field_44352.method_51081(this.field_44354, arg);
		boolean bl = this.field_44360.method_51075() && this.field_44361.method_51075();
		this.field_44364.active = bl;
		this.field_44364.setTooltip(this.method_50983(this.field_44352.method_51082().header().cost()));
		return bl;
	}

	private Tooltip method_50983(List<class_8390.class_8391> list) {
		List<Text> list2 = new ArrayList();
		this.method_50981(list2, this.field_44361, "vote.count_per_option.limit", "vote.count_per_option.no_limit");
		this.method_50981(list2, this.field_44360, "vote.count_per_proposal.limit", "vote.count_per_proposal.no_limit");
		List<Text> list3 = new ArrayList();

		for (class_8390.class_8391 lv : list) {
			switch (lv.material().method_50601()) {
				case PER_OPTION:
				case PER_PROPOSAL:
					break;
				default:
					list3.add(Text.literal("- ").append(lv.method_50607(false)));
			}
		}

		if (!list3.isEmpty()) {
			list2.add(Text.translatable("vote.cost"));
			list2.addAll(list3);
		}

		return Tooltip.of(ScreenTexts.joinLines(list2));
	}

	private void method_50981(List<Text> list, @Nullable class_8471.class_8472 arg, String string, String string2) {
		if (arg != null) {
			if (arg.limit().isPresent()) {
				list.add(Text.translatable(string, arg.count(), arg.limit().getAsInt()));
			} else {
				list.add(Text.translatable(string2, arg.count()));
			}
		}
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		int i = this.textRenderer.getWidth(this.field_44356);
		this.textRenderer.drawWithShadow(matrices, this.field_44356, 26.0F + (float)(180 - i) / 2.0F, 8.0F, -1);
		this.textRenderer.drawWithShadow(matrices, this.field_44357, 118.0F, 110.0F, -1);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		int i = this.field_44358.count() * 9;
		this.field_44358.drawCenterWithShadow(matrices, this.x + 13 + 102, this.y + 27 + (68 - i) / 2);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	public void close() {
		this.client.setScreen(new PendingVotesScreen());
	}

	@Nullable
	@Override
	protected Slot getSlotAt(double x, double y) {
		Slot slot = super.getSlotAt(x, y);
		return this.method_50982(slot);
	}

	@Override
	protected void method_50956(@Nullable Slot slot) {
		super.method_50956(this.method_50982(slot));
	}

	@Nullable
	private Slot method_50982(@Nullable Slot slot) {
		return slot instanceof CreativeInventoryScreen.CreativeSlot creativeSlot ? creativeSlot.method_50958() : slot;
	}

	@Override
	public boolean shouldPause() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public static class Handler extends ScreenHandler {
		private static final int field_44371 = 9;
		public final DefaultedList<ItemStack> field_44370 = DefaultedList.of();
		private final ScreenHandler field_44372;

		public Handler(PlayerScreenHandler playerScreenHandler) {
			super(null, 0);
			this.field_44372 = playerScreenHandler;

			for (int i = 0; i < playerScreenHandler.slots.size(); i++) {
				int j;
				int k;
				if ((i < 5 || i >= 9) && (i < 0 || i >= 5) && i != 45) {
					int l = i - 9;
					int m = l % 9;
					int n = l / 9;
					j = 36 + m * 18;
					if (i >= 36) {
						k = 195;
					} else {
						k = 137 + n * 18;
					}
				} else {
					j = -2000;
					k = -2000;
				}

				Slot slot = new CreativeInventoryScreen.CreativeSlot(playerScreenHandler.slots.get(i), i, j, k);
				this.slots.add(slot);
			}
		}

		@Override
		public boolean canUse(PlayerEntity player) {
			return true;
		}

		@Override
		public ItemStack quickMove(PlayerEntity player, int slot) {
			if (slot >= this.slots.size() - 9 && slot < this.slots.size()) {
				Slot slot2 = this.slots.get(slot);
				if (slot2 != null && slot2.hasStack()) {
					slot2.setStack(ItemStack.EMPTY);
				}
			}

			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack getCursorStack() {
			return this.field_44372.getCursorStack();
		}

		@Override
		public void setCursorStack(ItemStack stack) {
			this.field_44372.setCursorStack(stack);
		}
	}

	@Environment(EnvType.CLIENT)
	static record class_8450(class_8373 id, Text display, class_8367.class_8368 data) {
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_8451 extends PressableWidget {
		private final int field_44373;

		protected class_8451(int i, int j, Text text, int k) {
			super(i, j, 16, 16, text);
			this.field_44373 = k;
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RenderSystem.setShaderTexture(0, VotingScreen.TEXTURE);
			int i = 231;
			if (!this.active) {
				drawTexture(matrices, this.getX(), this.getY(), 231, this.field_44373 + this.width, this.width, this.height);
			} else if (this.isSelected()) {
				drawTexture(matrices, this.getX(), this.getY(), 231, this.field_44373, this.width, this.height);
			}
		}

		@Override
		public void appendClickableNarrations(NarrationMessageBuilder builder) {
			this.appendDefaultNarrations(builder);
		}
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_8452 extends PressableWidget {
		protected class_8452(int i, int j, Text text, TextRenderer textRenderer) {
			super(i, j, 89, 22, text);
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RenderSystem.setShaderTexture(0, VotingScreen.TEXTURE);
			int i = 219;
			if (!this.active) {
				int j = 0;
				drawTexture(matrices, this.getX(), this.getY(), 0, i, this.width, this.height);
			} else if (this.isSelected()) {
				int j = 89;
				drawTexture(matrices, this.getX(), this.getY(), 89, i, this.width, this.height);
			}
		}

		@Override
		public void appendClickableNarrations(NarrationMessageBuilder builder) {
			this.appendDefaultNarrations(builder);
		}
	}
}
