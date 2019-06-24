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
	private TutorialToast field_5637;
	private int ticks;
	private int field_5635;

	public PunchTreeTutorialStepHandler(TutorialManager tutorialManager) {
		this.manager = tutorialManager;
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

					if (FindTreeTutorialStepHandler.method_4896(clientPlayerEntity)) {
						this.manager.setStep(TutorialStep.CRAFT_PLANKS);
						return;
					}
				}
			}

			if ((this.ticks >= 600 || this.field_5635 > 3) && this.field_5637 == null) {
				this.field_5637 = new TutorialToast(TutorialToast.Type.TREE, TITLE, DESCRIPTION, true);
				this.manager.getClient().getToastManager().add(this.field_5637);
			}
		}
	}

	@Override
	public void destroy() {
		if (this.field_5637 != null) {
			this.field_5637.hide();
			this.field_5637 = null;
		}
	}

	@Override
	public void onBlockAttacked(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, float f) {
		boolean bl = blockState.matches(BlockTags.LOGS);
		if (bl && f > 0.0F) {
			if (this.field_5637 != null) {
				this.field_5637.setProgress(f);
			}

			if (f >= 1.0F) {
				this.manager.setStep(TutorialStep.OPEN_INVENTORY);
			}
		} else if (this.field_5637 != null) {
			this.field_5637.setProgress(0.0F);
		} else if (bl) {
			this.field_5635++;
		}
	}

	@Override
	public void onSlotUpdate(ItemStack itemStack) {
		if (ItemTags.LOGS.contains(itemStack.getItem())) {
			this.manager.setStep(TutorialStep.CRAFT_PLANKS);
		}
	}
}
