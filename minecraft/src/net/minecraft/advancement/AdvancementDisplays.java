package net.minecraft.advancement;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Predicate;

public class AdvancementDisplays {
	private static final int DISPLAY_DEPTH = 2;

	private static AdvancementDisplays.Status getStatus(Advancement advancement, boolean force) {
		AdvancementDisplay advancementDisplay = advancement.getDisplay();
		if (advancementDisplay == null) {
			return AdvancementDisplays.Status.HIDE;
		} else if (force) {
			return AdvancementDisplays.Status.SHOW;
		} else {
			return advancementDisplay.isHidden() ? AdvancementDisplays.Status.HIDE : AdvancementDisplays.Status.NO_CHANGE;
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
		Advancement advancement, Stack<AdvancementDisplays.Status> statuses, Predicate<Advancement> donePredicate, AdvancementDisplays.ResultConsumer consumer
	) {
		boolean bl = donePredicate.test(advancement);
		AdvancementDisplays.Status status = getStatus(advancement, bl);
		boolean bl2 = bl;
		statuses.push(status);

		for (Advancement advancement2 : advancement.getChildren()) {
			bl2 |= shouldDisplay(advancement2, statuses, donePredicate, consumer);
		}

		boolean bl3 = bl2 || shouldDisplay(statuses);
		statuses.pop();
		consumer.accept(advancement, bl3);
		return bl2;
	}

	public static void calculateDisplay(Advancement advancement, Predicate<Advancement> donePredicate, AdvancementDisplays.ResultConsumer consumer) {
		Advancement advancement2 = advancement.getRoot();
		Stack<AdvancementDisplays.Status> stack = new ObjectArrayList<>();

		for (int i = 0; i <= 2; i++) {
			stack.push(AdvancementDisplays.Status.NO_CHANGE);
		}

		shouldDisplay(advancement2, stack, donePredicate, consumer);
	}

	@FunctionalInterface
	public interface ResultConsumer {
		void accept(Advancement advancement, boolean shouldDisplay);
	}

	static enum Status {
		SHOW,
		HIDE,
		NO_CHANGE;
	}
}
