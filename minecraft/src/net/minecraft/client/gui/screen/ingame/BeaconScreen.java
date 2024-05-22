package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BeaconScreen extends HandledScreen<BeaconScreenHandler> {
	private static final Identifier TEXTURE = Identifier.method_60656("textures/gui/container/beacon.png");
	static final Identifier BUTTON_DISABLED_TEXTURE = Identifier.method_60656("container/beacon/button_disabled");
	static final Identifier BUTTON_SELECTED_TEXTURE = Identifier.method_60656("container/beacon/button_selected");
	static final Identifier BUTTON_HIGHLIGHTED_TEXTURE = Identifier.method_60656("container/beacon/button_highlighted");
	static final Identifier BUTTON_TEXTURE = Identifier.method_60656("container/beacon/button");
	static final Identifier CONFIRM_TEXTURE = Identifier.method_60656("container/beacon/confirm");
	static final Identifier CANCEL_TEXTURE = Identifier.method_60656("container/beacon/cancel");
	private static final Text PRIMARY_POWER_TEXT = Text.translatable("block.minecraft.beacon.primary");
	private static final Text SECONDARY_POWER_TEXT = Text.translatable("block.minecraft.beacon.secondary");
	private final List<BeaconScreen.BeaconButtonWidget> buttons = Lists.<BeaconScreen.BeaconButtonWidget>newArrayList();
	@Nullable
	RegistryEntry<StatusEffect> primaryEffect;
	@Nullable
	RegistryEntry<StatusEffect> secondaryEffect;

	public BeaconScreen(BeaconScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = 230;
		this.backgroundHeight = 219;
		handler.addListener(new ScreenHandlerListener() {
			@Override
			public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
			}

			@Override
			public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
				BeaconScreen.this.primaryEffect = handler.getPrimaryEffect();
				BeaconScreen.this.secondaryEffect = handler.getSecondaryEffect();
			}
		});
	}

	private <T extends ClickableWidget & BeaconScreen.BeaconButtonWidget> void addButton(T button) {
		this.addDrawableChild(button);
		this.buttons.add(button);
	}

	@Override
	protected void init() {
		super.init();
		this.buttons.clear();
		this.addButton(new BeaconScreen.DoneButtonWidget(this.x + 164, this.y + 107));
		this.addButton(new BeaconScreen.CancelButtonWidget(this.x + 190, this.y + 107));

		for (int i = 0; i <= 2; i++) {
			int j = ((List)BeaconBlockEntity.EFFECTS_BY_LEVEL.get(i)).size();
			int k = j * 22 + (j - 1) * 2;

			for (int l = 0; l < j; l++) {
				RegistryEntry<StatusEffect> registryEntry = (RegistryEntry<StatusEffect>)((List)BeaconBlockEntity.EFFECTS_BY_LEVEL.get(i)).get(l);
				BeaconScreen.EffectButtonWidget effectButtonWidget = new BeaconScreen.EffectButtonWidget(
					this.x + 76 + l * 24 - k / 2, this.y + 22 + i * 25, registryEntry, true, i
				);
				effectButtonWidget.active = false;
				this.addButton(effectButtonWidget);
			}
		}

		int i = 3;
		int j = ((List)BeaconBlockEntity.EFFECTS_BY_LEVEL.get(3)).size() + 1;
		int k = j * 22 + (j - 1) * 2;

		for (int l = 0; l < j - 1; l++) {
			RegistryEntry<StatusEffect> registryEntry = (RegistryEntry<StatusEffect>)((List)BeaconBlockEntity.EFFECTS_BY_LEVEL.get(3)).get(l);
			BeaconScreen.EffectButtonWidget effectButtonWidget = new BeaconScreen.EffectButtonWidget(this.x + 167 + l * 24 - k / 2, this.y + 47, registryEntry, false, 3);
			effectButtonWidget.active = false;
			this.addButton(effectButtonWidget);
		}

		RegistryEntry<StatusEffect> registryEntry2 = (RegistryEntry<StatusEffect>)((List)BeaconBlockEntity.EFFECTS_BY_LEVEL.get(0)).get(0);
		BeaconScreen.EffectButtonWidget effectButtonWidget2 = new BeaconScreen.LevelTwoEffectButtonWidget(
			this.x + 167 + (j - 1) * 24 - k / 2, this.y + 47, registryEntry2
		);
		effectButtonWidget2.visible = false;
		this.addButton(effectButtonWidget2);
	}

	@Override
	public void handledScreenTick() {
		super.handledScreenTick();
		this.tickButtons();
	}

	void tickButtons() {
		int i = this.handler.getProperties();
		this.buttons.forEach(button -> button.tick(i));
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		context.drawCenteredTextWithShadow(this.textRenderer, PRIMARY_POWER_TEXT, 62, 10, 14737632);
		context.drawCenteredTextWithShadow(this.textRenderer, SECONDARY_POWER_TEXT, 169, 10, 14737632);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		context.getMatrices().push();
		context.getMatrices().translate(0.0F, 0.0F, 100.0F);
		context.drawItem(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
		context.drawItem(new ItemStack(Items.EMERALD), i + 41, j + 109);
		context.drawItem(new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109);
		context.drawItem(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
		context.drawItem(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
		context.getMatrices().pop();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Environment(EnvType.CLIENT)
	abstract static class BaseButtonWidget extends PressableWidget implements BeaconScreen.BeaconButtonWidget {
		private boolean disabled;

		protected BaseButtonWidget(int x, int y) {
			super(x, y, 22, 22, ScreenTexts.EMPTY);
		}

		protected BaseButtonWidget(int x, int y, Text message) {
			super(x, y, 22, 22, message);
		}

		@Override
		public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
			Identifier identifier;
			if (!this.active) {
				identifier = BeaconScreen.BUTTON_DISABLED_TEXTURE;
			} else if (this.disabled) {
				identifier = BeaconScreen.BUTTON_SELECTED_TEXTURE;
			} else if (this.isSelected()) {
				identifier = BeaconScreen.BUTTON_HIGHLIGHTED_TEXTURE;
			} else {
				identifier = BeaconScreen.BUTTON_TEXTURE;
			}

			context.drawGuiTexture(identifier, this.getX(), this.getY(), this.width, this.height);
			this.renderExtra(context);
		}

		protected abstract void renderExtra(DrawContext context);

		public boolean isDisabled() {
			return this.disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}

		@Override
		public void appendClickableNarrations(NarrationMessageBuilder builder) {
			this.appendDefaultNarrations(builder);
		}
	}

	@Environment(EnvType.CLIENT)
	interface BeaconButtonWidget {
		void tick(int level);
	}

	@Environment(EnvType.CLIENT)
	class CancelButtonWidget extends BeaconScreen.IconButtonWidget {
		public CancelButtonWidget(final int x, final int y) {
			super(x, y, BeaconScreen.CANCEL_TEXTURE, ScreenTexts.CANCEL);
		}

		@Override
		public void onPress() {
			BeaconScreen.this.client.player.closeHandledScreen();
		}

		@Override
		public void tick(int level) {
		}
	}

	@Environment(EnvType.CLIENT)
	class DoneButtonWidget extends BeaconScreen.IconButtonWidget {
		public DoneButtonWidget(final int x, final int y) {
			super(x, y, BeaconScreen.CONFIRM_TEXTURE, ScreenTexts.DONE);
		}

		@Override
		public void onPress() {
			BeaconScreen.this.client
				.getNetworkHandler()
				.sendPacket(new UpdateBeaconC2SPacket(Optional.ofNullable(BeaconScreen.this.primaryEffect), Optional.ofNullable(BeaconScreen.this.secondaryEffect)));
			BeaconScreen.this.client.player.closeHandledScreen();
		}

		@Override
		public void tick(int level) {
			this.active = BeaconScreen.this.handler.hasPayment() && BeaconScreen.this.primaryEffect != null;
		}
	}

	@Environment(EnvType.CLIENT)
	class EffectButtonWidget extends BeaconScreen.BaseButtonWidget {
		private final boolean primary;
		protected final int level;
		private RegistryEntry<StatusEffect> effect;
		private Sprite sprite;

		public EffectButtonWidget(final int x, final int y, final RegistryEntry<StatusEffect> effect, final boolean primary, final int level) {
			super(x, y);
			this.primary = primary;
			this.level = level;
			this.init(effect);
		}

		protected void init(RegistryEntry<StatusEffect> effect) {
			this.effect = effect;
			this.sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(effect);
			this.setTooltip(Tooltip.of(this.getEffectName(effect), null));
		}

		protected MutableText getEffectName(RegistryEntry<StatusEffect> effect) {
			return Text.translatable(effect.value().getTranslationKey());
		}

		@Override
		public void onPress() {
			if (!this.isDisabled()) {
				if (this.primary) {
					BeaconScreen.this.primaryEffect = this.effect;
				} else {
					BeaconScreen.this.secondaryEffect = this.effect;
				}

				BeaconScreen.this.tickButtons();
			}
		}

		@Override
		protected void renderExtra(DrawContext context) {
			context.drawSprite(this.getX() + 2, this.getY() + 2, 0, 18, 18, this.sprite);
		}

		@Override
		public void tick(int level) {
			this.active = this.level < level;
			this.setDisabled(this.effect.equals(this.primary ? BeaconScreen.this.primaryEffect : BeaconScreen.this.secondaryEffect));
		}

		@Override
		protected MutableText getNarrationMessage() {
			return this.getEffectName(this.effect);
		}
	}

	@Environment(EnvType.CLIENT)
	abstract static class IconButtonWidget extends BeaconScreen.BaseButtonWidget {
		private final Identifier texture;

		protected IconButtonWidget(int x, int y, Identifier texture, Text message) {
			super(x, y, message);
			this.texture = texture;
		}

		@Override
		protected void renderExtra(DrawContext context) {
			context.drawGuiTexture(this.texture, this.getX() + 2, this.getY() + 2, 18, 18);
		}
	}

	@Environment(EnvType.CLIENT)
	class LevelTwoEffectButtonWidget extends BeaconScreen.EffectButtonWidget {
		public LevelTwoEffectButtonWidget(final int x, final int y, final RegistryEntry<StatusEffect> effect) {
			super(x, y, effect, false, 3);
		}

		@Override
		protected MutableText getEffectName(RegistryEntry<StatusEffect> effect) {
			return Text.translatable(effect.value().getTranslationKey()).append(" II");
		}

		@Override
		public void tick(int level) {
			if (BeaconScreen.this.primaryEffect != null) {
				this.visible = true;
				this.init(BeaconScreen.this.primaryEffect);
				super.tick(level);
			} else {
				this.visible = false;
			}
		}
	}
}
