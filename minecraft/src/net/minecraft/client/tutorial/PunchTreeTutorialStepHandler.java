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
	private final TutorialManager field_5634;
	private TutorialToast field_5637;
	private int ticks;
	private int field_5635;

	public PunchTreeTutorialStepHandler(TutorialManager tutorialManager) {
		this.field_5634 = tutorialManager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.field_5634.getGameMode() != GameMode.field_9215) {
			this.field_5634.setStep(TutorialStep.field_5653);
		} else {
			if (this.ticks == 1) {
				ClientPlayerEntity clientPlayerEntity = this.field_5634.getClient().field_1724;
				if (clientPlayerEntity != null) {
					if (clientPlayerEntity.inventory.contains(ItemTags.field_15539)) {
						this.field_5634.setStep(TutorialStep.field_5655);
						return;
					}

					if (FindTreeTutorialStepHandler.method_4896(clientPlayerEntity)) {
						this.field_5634.setStep(TutorialStep.field_5655);
						return;
					}
				}
			}

			if ((this.ticks >= 600 || this.field_5635 > 3) && this.field_5637 == null) {
				this.field_5637 = new TutorialToast(TutorialToast.Type.field_2235, TITLE, DESCRIPTION, true);
				this.field_5634.getClient().method_1566().add(this.field_5637);
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
		boolean bl = blockState.matches(BlockTags.field_15475);
		if (bl && f > 0.0F) {
			if (this.field_5637 != null) {
				this.field_5637.setProgress(f);
			}

			if (f >= 1.0F) {
				this.field_5634.setStep(TutorialStep.field_5652);
			}
		} else if (this.field_5637 != null) {
			this.field_5637.setProgress(0.0F);
		} else if (bl) {
			this.field_5635++;
		}
	}

	@Override
	public void onSlotUpdate(ItemStack itemStack) {
		if (ItemTags.field_15539.contains(itemStack.getItem())) {
			this.field_5634.setStep(TutorialStep.field_5655);
		}
	}
}
