/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.text.Text;

public class HoverEvent {
    private final Action action;
    private final Text value;

    public HoverEvent(Action action, Text value) {
        this.action = action;
        this.value = value;
    }

    public Action getAction() {
        return this.action;
    }

    public Text getValue() {
        return this.value;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        HoverEvent hoverEvent = (HoverEvent)obj;
        if (this.action != hoverEvent.action) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(hoverEvent.value) : hoverEvent.value != null);
    }

    public String toString() {
        return "HoverEvent{action=" + (Object)((Object)this.action) + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode() {
        int i = this.action.hashCode();
        i = 31 * i + (this.value != null ? this.value.hashCode() : 0);
        return i;
    }

    public static enum Action {
        SHOW_TEXT("show_text", true),
        SHOW_ITEM("show_item", true),
        SHOW_ENTITY("show_entity", true);

        private static final Map<String, Action> BY_NAME;
        private final boolean userDefinable;
        private final String name;

        private Action(String name, boolean userDefinable) {
            this.name = name;
            this.userDefinable = userDefinable;
        }

        public boolean isUserDefinable() {
            return this.userDefinable;
        }

        public String getName() {
            return this.name;
        }

        public static Action byName(String name) {
            return BY_NAME.get(name);
        }

        static {
            BY_NAME = Arrays.stream(Action.values()).collect(Collectors.toMap(Action::getName, a -> a));
        }
    }
}

