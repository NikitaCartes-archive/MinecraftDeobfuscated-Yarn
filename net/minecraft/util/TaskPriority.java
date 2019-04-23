/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

public enum TaskPriority {
    EXTREMELY_HIGH(-3),
    VERY_HIGH(-2),
    HIGH(-1),
    NORMAL(0),
    LOW(1),
    VERY_LOW(2),
    EXTREMELY_LOW(3);

    private final int priorityIndex;

    private TaskPriority(int j) {
        this.priorityIndex = j;
    }

    public static TaskPriority getByIndex(int i) {
        for (TaskPriority taskPriority : TaskPriority.values()) {
            if (taskPriority.priorityIndex != i) continue;
            return taskPriority;
        }
        if (i < TaskPriority.EXTREMELY_HIGH.priorityIndex) {
            return EXTREMELY_HIGH;
        }
        return EXTREMELY_LOW;
    }

    public int getPriorityIndex() {
        return this.priorityIndex;
    }
}

