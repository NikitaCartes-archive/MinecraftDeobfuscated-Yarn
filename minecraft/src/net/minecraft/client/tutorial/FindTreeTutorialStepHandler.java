package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class FindTreeTutorialStepHandler implements TutorialStepHandler {
	private static final Set<Block> MATCHING_BLOCKS = Sets.<Block>newHashSet(
		Blocks.field_10431,
		Blocks.field_10037,
		Blocks.field_10511,
		Blocks.field_10306,
		Blocks.field_10533,
		Blocks.field_10010,
		Blocks.field_10126,
		Blocks.field_10155,
		Blocks.field_10307,
		Blocks.field_10303,
		Blocks.field_9999,
		Blocks.field_10178,
		Blocks.field_10503,
		Blocks.field_9988,
		Blocks.field_10539,
		Blocks.field_10335,
		Blocks.field_10098,
		Blocks.field_10035
	);
	private static final Text TITLE = new TranslatableText("tutorial.find_tree.title");
	private static final Text DESCRIPTION = new TranslatableText("tutorial.find_tree.description");
	private final TutorialManager field_5630;
	private TutorialToast toast;
	private int ticks;

	public FindTreeTutorialStepHandler(TutorialManager tutorialManager) {
		this.field_5630 = tutorialManager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.field_5630.getGameMode() != GameMode.field_9215) {
			this.field_5630.setStep(TutorialStep.field_5653);
		} else {
			if (this.ticks == 1) {
				ClientPlayerEntity clientPlayerEntity = this.field_5630.getClient().field_1724;
				if (clientPlayerEntity != null) {
					for (Block block : MATCHING_BLOCKS) {
						if (clientPlayerEntity.inventory.contains(new ItemStack(block))) {
							this.field_5630.setStep(TutorialStep.field_5655);
							return;
						}
					}

					if (method_4896(clientPlayerEntity)) {
						this.field_5630.setStep(TutorialStep.field_5655);
						return;
					}
				}
			}

			if (this.ticks >= 6000 && this.toast == null) {
				this.toast = new TutorialToast(TutorialToast.Type.field_2235, TITLE, DESCRIPTION, false);
				this.field_5630.getClient().method_1566().add(this.toast);
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
	public void onTarget(ClientWorld clientWorld, HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.field_1332) {
			BlockState blockState = clientWorld.method_8320(((BlockHitResult)hitResult).getBlockPos());
			if (MATCHING_BLOCKS.contains(blockState.getBlock())) {
				this.field_5630.setStep(TutorialStep.field_5649);
			}
		}
	}

	@Override
	public void onSlotUpdate(ItemStack itemStack) {
		for (Block block : MATCHING_BLOCKS) {
			if (itemStack.getItem() == block.asItem()) {
				this.field_5630.setStep(TutorialStep.field_5655);
				return;
			}
		}
	}

	public static boolean method_4896(ClientPlayerEntity clientPlayerEntity) {
		for (Block block : MATCHING_BLOCKS) {
			if (clientPlayerEntity.getStats().getStat(Stats.field_15427.getOrCreateStat(block)) > 0) {
				return true;
			}
		}

		return false;
	}
}
