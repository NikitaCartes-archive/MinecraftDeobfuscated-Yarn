package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class PunchTreeTutorialStepHandler implements TutorialStepHandler {
	private static final Text TITLE = new TranslatableText("tutorial.punch_tree.title");
	private static final Text DESCRIPTION = new TranslatableText("tutorial.punch_tree.description", TutorialManager.getKeybindName("attack"));
	private final TutorialManager manager;
	private TutorialToast toast;
	private int ticks;
	private int field_5635;

	public PunchTreeTutorialStepHandler(TutorialManager manager) {
		this.manager = manager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.manager.getGameMode() != GameMode.SURVIVAL) {
			this.manager.setStep(TutorialStep.NONE);
		} else {
			if (this.ticks == 1) {
				ClientPlayerEntity clientPlayerEntity = this.manager.getClient().player;
				if (clientPlayerEntity != null) {
					if (clientPlayerEntity.inventory.contains(ItemTags.LOGS)) {
						this.manager.setStep(TutorialStep.CRAFT_PLANKS);
						return;
					}

					if (FindTreeTutorialStepHandler.hasBrokenTreeBlocks(clientPlayerEntity)) {
						this.manager.setStep(TutorialStep.CRAFT_PLANKS);
						return;
					}
				}
			}

			if ((this.ticks >= 600 || this.field_5635 > 3) && this.toast == null) {
				this.toast = new TutorialToast(TutorialToast.Type.TREE, TITLE, DESCRIPTION, true);
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
	public void onBlockAttacked(ClientWorld client, BlockPos pos, BlockState state, float f) {
		boolean bl = state.isIn(BlockTags.LOGS);
		if (bl && f > 0.0F) {
			if (this.toast != null) {
				this.toast.setProgress(f);
			}

			if (f >= 1.0F) {
				this.manager.setStep(TutorialStep.OPEN_INVENTORY);
			}
		} else if (this.toast != null) {
			this.toast.setProgress(0.0F);
		} else if (bl) {
			this.field_5635++;
		}
	}

	@Override
	public void onSlotUpdate(ItemStack stack) {
		if (ItemTags.LOGS.contains(stack.getItem())) {
			this.manager.setStep(TutorialStep.CRAFT_PLANKS);
		}
	}
}
