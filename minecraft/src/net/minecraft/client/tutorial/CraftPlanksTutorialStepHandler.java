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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class CraftPlanksTutorialStepHandler implements TutorialStepHandler {
	private static final Text TITLE = new TranslatableText("tutorial.craft_planks.title");
	private static final Text DESCRIPTION = new TranslatableText("tutorial.craft_planks.description");
	private final TutorialManager field_5608;
	private TutorialToast toast;
	private int ticks;

	public CraftPlanksTutorialStepHandler(TutorialManager tutorialManager) {
		this.field_5608 = tutorialManager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.field_5608.getGameMode() != GameMode.field_9215) {
			this.field_5608.setStep(TutorialStep.field_5653);
		} else {
			if (this.ticks == 1) {
				ClientPlayerEntity clientPlayerEntity = this.field_5608.getClient().field_1724;
				if (clientPlayerEntity != null) {
					if (clientPlayerEntity.inventory.contains(ItemTags.field_15537)) {
						this.field_5608.setStep(TutorialStep.field_5653);
						return;
					}

					if (hasCrafted(clientPlayerEntity, ItemTags.field_15537)) {
						this.field_5608.setStep(TutorialStep.field_5653);
						return;
					}
				}
			}

			if (this.ticks >= 1200 && this.toast == null) {
				this.toast = new TutorialToast(TutorialToast.Type.field_2236, TITLE, DESCRIPTION, false);
				this.field_5608.getClient().method_1566().add(this.toast);
			}
		}
	}

	@Override
	public void destroy() {
		if (this.toast != null) {
			this.toast.hide();
			this.toast = null;
		}
	}

	@Override
	public void onSlotUpdate(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (ItemTags.field_15537.contains(item)) {
			this.field_5608.setStep(TutorialStep.field_5653);
		}
	}

	public static boolean hasCrafted(ClientPlayerEntity clientPlayerEntity, Tag<Item> tag) {
		for (Item item : tag.values()) {
			if (clientPlayerEntity.getStats().getStat(Stats.field_15370.getOrCreateStat(item)) > 0) {
				return true;
			}
		}

		return false;
	}
}
