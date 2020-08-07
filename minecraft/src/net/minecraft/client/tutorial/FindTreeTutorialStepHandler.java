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
	private static final Set<Block> TREE_BLOCKS = Sets.<Block>newHashSet(
		Blocks.field_10431,
		Blocks.field_10037,
		Blocks.field_10511,
		Blocks.field_10306,
		Blocks.field_10533,
		Blocks.field_10010,
		Blocks.field_22111,
		Blocks.field_22118,
		Blocks.field_10126,
		Blocks.field_10155,
		Blocks.field_10307,
		Blocks.field_10303,
		Blocks.field_9999,
		Blocks.field_10178,
		Blocks.field_22503,
		Blocks.field_22505,
		Blocks.field_10503,
		Blocks.field_9988,
		Blocks.field_10539,
		Blocks.field_10335,
		Blocks.field_10098,
		Blocks.field_10035,
		Blocks.field_10541,
		Blocks.field_22115
	);
	private static final Text TITLE = new TranslatableText("tutorial.find_tree.title");
	private static final Text DESCRIPTION = new TranslatableText("tutorial.find_tree.description");
	private final TutorialManager manager;
	private TutorialToast toast;
	private int ticks;

	public FindTreeTutorialStepHandler(TutorialManager manager) {
		this.manager = manager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.manager.getGameMode() != GameMode.field_9215) {
			this.manager.setStep(TutorialStep.field_5653);
		} else {
			if (this.ticks == 1) {
				ClientPlayerEntity clientPlayerEntity = this.manager.getClient().player;
				if (clientPlayerEntity != null) {
					for (Block block : TREE_BLOCKS) {
						if (clientPlayerEntity.inventory.contains(new ItemStack(block))) {
							this.manager.setStep(TutorialStep.field_5655);
							return;
						}
					}

					if (hasBrokenTreeBlocks(clientPlayerEntity)) {
						this.manager.setStep(TutorialStep.field_5655);
						return;
					}
				}
			}

			if (this.ticks >= 6000 && this.toast == null) {
				this.toast = new TutorialToast(TutorialToast.Type.field_2235, TITLE, DESCRIPTION, false);
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
	public void onTarget(ClientWorld world, HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.field_1332) {
			BlockState blockState = world.getBlockState(((BlockHitResult)hitResult).getBlockPos());
			if (TREE_BLOCKS.contains(blockState.getBlock())) {
				this.manager.setStep(TutorialStep.field_5649);
			}
		}
	}

	@Override
	public void onSlotUpdate(ItemStack stack) {
		for (Block block : TREE_BLOCKS) {
			if (stack.getItem() == block.asItem()) {
				this.manager.setStep(TutorialStep.field_5655);
				return;
			}
		}
	}

	public static boolean hasBrokenTreeBlocks(ClientPlayerEntity player) {
		for (Block block : TREE_BLOCKS) {
			if (player.getStatHandler().getStat(Stats.field_15427.getOrCreateStat(block)) > 0) {
				return true;
			}
		}

		return false;
	}
}
