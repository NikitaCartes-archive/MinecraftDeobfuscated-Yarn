package net.minecraft.advancement;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class AdvancementDisplays {
	private static final int DISPLAY_DEPTH = 2;

	private static AdvancementDisplays.Status getStatus(Advancement advancement, boolean force) {
		Optional<AdvancementDisplay> optional = advancement.display();
		if (optional.isEmpty()) {
			return AdvancementDisplays.Status.HIDE;
		} else if (force) {
			return AdvancementDisplays.Status.SHOW;
		} else {
			return ((AdvancementDisplay)optional.get()).isHidden() ? AdvancementDisplays.Status.HIDE : AdvancementDisplays.Status.NO_CHANGE;
		}
	}

	private static boolean shouldDisplay(Stack<AdvancementDisplays.Status> statuses) {
		for (int i = 0; i <= 2; i++) {
			AdvancementDisplays.Status status = statuses.peek(i);
			if (status == AdvancementDisplays.Status.SHOW) {
				return true;
			}

			if (status == AdvancementDisplays.Status.HIDE) {
				return false;
			}
		}

		return false;
	}

	private static boolean shouldDisplay(
		PlacedAdvancement advancement,
		Stack<AdvancementDisplays.Status> statuses,
		Predicate<PlacedAdvancement> donePredicate,
		AdvancementDisplays.ResultConsumer consumer
	) {
		boolean bl = donePredicate.test(advancement);
		AdvancementDisplays.Status status = getStatus(advancement.getAdvancement(), bl);
		boolean bl2 = bl;
		statuses.push(status);

		for (PlacedAdvancement placedAdvancement : advancement.getChildren()) {
			bl2 |= shouldDisplay(placedAdvancement, statuses, donePredicate, consumer);
		}

		boolean bl3 = bl2 || shouldDisplay(statuses);
		statuses.pop();
		consumer.accept(advancement, bl3);
		return bl2;
	}

	public static void calculateDisplay(PlacedAdvancement advancement, Predicate<PlacedAdvancement> donePredicate, AdvancementDisplays.ResultConsumer consumer) {
		PlacedAdvancement placedAdvancement = advancement.getRoot();
		Stack<AdvancementDisplays.Status> stack = new ObjectArrayList<>();

		for (int i = 0; i <= 2; i++) {
			stack.push(AdvancementDisplays.Status.NO_CHANGE);
		}

		shouldDisplay(placedAdvancement, stack, donePredicate, consumer);
	}

	@FunctionalInterface
	public interface ResultConsumer {
		void accept(PlacedAdvancement advancement, boolean shouldDisplay);
	}

	static enum Status {
		SHOW,
		HIDE,
		NO_CHANGE;
	}
}
