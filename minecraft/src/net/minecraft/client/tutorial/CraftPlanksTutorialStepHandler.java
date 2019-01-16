package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class CraftPlanksTutorialStepHandler implements TutorialStepHandler {
	private static final TextComponent TITLE = new TranslatableTextComponent("tutorial.craft_planks.title");
	private static final TextComponent DESCRIPTION = new TranslatableTextComponent("tutorial.craft_planks.description");
	private final TutorialManager manager;
	private TutorialToast field_5610;
	private int ticks;

	public CraftPlanksTutorialStepHandler(TutorialManager tutorialManager) {
		this.manager = tutorialManager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.manager.getGameMode() != GameMode.field_9215) {
			this.manager.setStep(TutorialStep.NONE);
		} else {
			if (this.ticks == 1) {
				ClientPlayerEntity clientPlayerEntity = this.manager.getClient().player;
				if (clientPlayerEntity != null) {
					if (clientPlayerEntity.inventory.method_7382(ItemTags.field_15537)) {
						this.manager.setStep(TutorialStep.NONE);
						return;
					}

					if (method_4895(clientPlayerEntity, ItemTags.field_15537)) {
						this.manager.setStep(TutorialStep.NONE);
						return;
					}
				}
			}

			if (this.ticks >= 1200 && this.field_5610 == null) {
				this.field_5610 = new TutorialToast(TutorialToast.class_373.field_2236, TITLE, DESCRIPTION, false);
				this.manager.getClient().getToastManager().add(this.field_5610);
			}
		}
	}

	@Override
	public void destroy() {
		if (this.field_5610 != null) {
			this.field_5610.method_1993();
			this.field_5610 = null;
		}
	}

	@Override
	public void onSlotUpdate(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (ItemTags.field_15537.contains(item)) {
			this.manager.setStep(TutorialStep.NONE);
		}
	}

	public static boolean method_4895(ClientPlayerEntity clientPlayerEntity, Tag<Item> tag) {
		for (Item item : tag.values()) {
			if (clientPlayerEntity.getStats().getStat(Stats.field_15370.method_14956(item)) > 0) {
				return true;
			}
		}

		return false;
	}
}
