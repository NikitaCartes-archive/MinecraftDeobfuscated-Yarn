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
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class PunchTreeTutorialStepHandler implements TutorialStepHandler {
	private static final TextComponent field_5638 = new TranslatableTextComponent("tutorial.punch_tree.title");
	private static final TextComponent field_5639 = new TranslatableTextComponent("tutorial.punch_tree.description", TutorialManager.method_4913("attack"));
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
			this.field_5634.method_4910(TutorialStep.NONE);
		} else {
			if (this.ticks == 1) {
				ClientPlayerEntity clientPlayerEntity = this.field_5634.getClient().field_1724;
				if (clientPlayerEntity != null) {
					if (clientPlayerEntity.inventory.method_7382(ItemTags.field_15539)) {
						this.field_5634.method_4910(TutorialStep.CRAFT_PLANKS);
						return;
					}

					if (FindTreeTutorialStepHandler.method_4896(clientPlayerEntity)) {
						this.field_5634.method_4910(TutorialStep.CRAFT_PLANKS);
						return;
					}
				}
			}

			if ((this.ticks >= 600 || this.field_5635 > 3) && this.field_5637 == null) {
				this.field_5637 = new TutorialToast(TutorialToast.Type.field_2235, field_5638, field_5639, true);
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
	public void method_4900(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, float f) {
		boolean bl = blockState.method_11602(BlockTags.field_15475);
		if (bl && f > 0.0F) {
			if (this.field_5637 != null) {
				this.field_5637.method_1992(f);
			}

			if (f >= 1.0F) {
				this.field_5634.method_4910(TutorialStep.OPEN_INVENTORY);
			}
		} else if (this.field_5637 != null) {
			this.field_5637.method_1992(0.0F);
		} else if (bl) {
			this.field_5635++;
		}
	}

	@Override
	public void onSlotUpdate(ItemStack itemStack) {
		if (ItemTags.field_15539.contains(itemStack.getItem())) {
			this.field_5634.method_4910(TutorialStep.CRAFT_PLANKS);
		}
	}
}
