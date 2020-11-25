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

@Environment(EnvType.CLIENT)
public class CraftPlanksTutorialStepHandler implements TutorialStepHandler {
	private static final Text TITLE = new TranslatableText("tutorial.craft_planks.title");
	private static final Text DESCRIPTION = new TranslatableText("tutorial.craft_planks.description");
	private final TutorialManager manager;
	private TutorialToast toast;
	private int ticks;

	public CraftPlanksTutorialStepHandler(TutorialManager manager) {
		this.manager = manager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (!this.manager.isInSurvival()) {
			this.manager.setStep(TutorialStep.NONE);
		} else {
			if (this.ticks == 1) {
				ClientPlayerEntity clientPlayerEntity = this.manager.getClient().player;
				if (clientPlayerEntity != null) {
					if (clientPlayerEntity.getInventory().contains(ItemTags.PLANKS)) {
						this.manager.setStep(TutorialStep.NONE);
						return;
					}

					if (hasCrafted(clientPlayerEntity, ItemTags.PLANKS)) {
						this.manager.setStep(TutorialStep.NONE);
						return;
					}
				}
			}

			if (this.ticks >= 1200 && this.toast == null) {
				this.toast = new TutorialToast(TutorialToast.Type.WOODEN_PLANKS, TITLE, DESCRIPTION, false);
				this.manager.getClient().getToastManager().add(this.toast);
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
	public void onSlotUpdate(ItemStack stack) {
		if (stack.isIn(ItemTags.PLANKS)) {
			this.manager.setStep(TutorialStep.NONE);
		}
	}

	public static boolean hasCrafted(ClientPlayerEntity player, Tag<Item> tag) {
		for (Item item : tag.values()) {
			if (player.getStatHandler().getStat(Stats.CRAFTED.getOrCreateStat(item)) > 0) {
				return true;
			}
		}

		return false;
	}
}
