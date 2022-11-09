package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class CraftPlanksTutorialStepHandler implements TutorialStepHandler {
	private static final int DELAY = 1200;
	private static final Text TITLE = Text.translatable("tutorial.craft_planks.title");
	private static final Text DESCRIPTION = Text.translatable("tutorial.craft_planks.description");
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

	public static boolean hasCrafted(ClientPlayerEntity player, TagKey<Item> tag) {
		for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(tag)) {
			if (player.getStatHandler().getStat(Stats.CRAFTED.getOrCreateStat(registryEntry.value())) > 0) {
				return true;
			}
		}

		return false;
	}
}
