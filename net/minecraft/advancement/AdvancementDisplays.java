/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Predicate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;

public class AdvancementDisplays {
    private static final int DISPLAY_DEPTH = 2;

    private static Status getStatus(Advancement advancement, boolean force) {
        AdvancementDisplay advancementDisplay = advancement.getDisplay();
        if (advancementDisplay == null) {
            return Status.HIDE;
        }
        if (force) {
            return Status.SHOW;
        }
        if (advancementDisplay.isHidden()) {
            return Status.HIDE;
        }
        return Status.NO_CHANGE;
    }

    private static boolean shouldDisplay(Stack<Status> statuses) {
        for (int i = 0; i <= 2; ++i) {
            Status status = statuses.peek(i);
            if (status == Status.SHOW) {
                return true;
            }
            if (status != Status.HIDE) continue;
            return false;
        }
        return false;
    }

    private static boolean shouldDisplay(Advancement advancement, Stack<Status> statuses, Predicate<Advancement> donePredicate, ResultConsumer consumer) {
        boolean bl = donePredicate.test(advancement);
        Status status = AdvancementDisplays.getStatus(advancement, bl);
        boolean bl2 = bl;
        statuses.push(status);
        for (Advancement advancement2 : advancement.getChildren()) {
            bl2 |= AdvancementDisplays.shouldDisplay(advancement2, statuses, donePredicate, consumer);
        }
        boolean bl3 = bl2 || AdvancementDisplays.shouldDisplay(statuses);
        statuses.pop();
        consumer.accept(advancement, bl3);
        return bl2;
    }

    public static void calculateDisplay(Advancement advancement, Predicate<Advancement> donePredicate, ResultConsumer consumer) {
        Advancement advancement2 = advancement.getRoot();
        ObjectArrayList<Status> stack = new ObjectArrayList<Status>();
        for (int i = 0; i <= 2; ++i) {
            stack.push(Status.NO_CHANGE);
        }
        AdvancementDisplays.shouldDisplay(advancement2, stack, donePredicate, consumer);
    }

    static enum Status {
        SHOW,
        HIDE,
        NO_CHANGE;

    }

    @FunctionalInterface
    public static interface ResultConsumer {
        public void accept(Advancement var1, boolean var2);
    }
}

