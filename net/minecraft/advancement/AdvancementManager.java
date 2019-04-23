/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class AdvancementManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Identifier, Advancement> advancements = Maps.newHashMap();
    private final Set<Advancement> roots = Sets.newLinkedHashSet();
    private final Set<Advancement> dependents = Sets.newLinkedHashSet();
    private Listener listener;

    @Environment(value=EnvType.CLIENT)
    private void remove(Advancement advancement) {
        for (Advancement advancement2 : advancement.getChildren()) {
            this.remove(advancement2);
        }
        LOGGER.info("Forgot about advancement {}", (Object)advancement.getId());
        this.advancements.remove(advancement.getId());
        if (advancement.getParent() == null) {
            this.roots.remove(advancement);
            if (this.listener != null) {
                this.listener.onRootRemoved(advancement);
            }
        } else {
            this.dependents.remove(advancement);
            if (this.listener != null) {
                this.listener.onDependentRemoved(advancement);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void removeAll(Set<Identifier> set) {
        for (Identifier identifier : set) {
            Advancement advancement = this.advancements.get(identifier);
            if (advancement == null) {
                LOGGER.warn("Told to remove advancement {} but I don't know what that is", (Object)identifier);
                continue;
            }
            this.remove(advancement);
        }
    }

    public void load(Map<Identifier, Advancement.Task> map) {
        Function<Identifier, Object> function = Functions.forMap(this.advancements, null);
        while (!map.isEmpty()) {
            boolean bl = false;
            Iterator<Map.Entry<Identifier, Advancement.Task>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Identifier, Advancement.Task> entry = iterator.next();
                Identifier identifier = entry.getKey();
                Advancement.Task task = entry.getValue();
                if (!task.findParent(function)) continue;
                Advancement advancement = task.build(identifier);
                this.advancements.put(identifier, advancement);
                bl = true;
                iterator.remove();
                if (advancement.getParent() == null) {
                    this.roots.add(advancement);
                    if (this.listener == null) continue;
                    this.listener.onRootAdded(advancement);
                    continue;
                }
                this.dependents.add(advancement);
                if (this.listener == null) continue;
                this.listener.onDependentAdded(advancement);
            }
            if (bl) continue;
            for (Map.Entry<Identifier, Advancement.Task> entry : map.entrySet()) {
                LOGGER.error("Couldn't load advancement {}: {}", (Object)entry.getKey(), (Object)entry.getValue());
            }
        }
        LOGGER.info("Loaded {} advancements", (Object)this.advancements.size());
    }

    public void clear() {
        this.advancements.clear();
        this.roots.clear();
        this.dependents.clear();
        if (this.listener != null) {
            this.listener.onClear();
        }
    }

    public Iterable<Advancement> getRoots() {
        return this.roots;
    }

    public Collection<Advancement> getAdvancements() {
        return this.advancements.values();
    }

    @Nullable
    public Advancement get(Identifier identifier) {
        return this.advancements.get(identifier);
    }

    @Environment(value=EnvType.CLIENT)
    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
        if (listener != null) {
            for (Advancement advancement : this.roots) {
                listener.onRootAdded(advancement);
            }
            for (Advancement advancement : this.dependents) {
                listener.onDependentAdded(advancement);
            }
        }
    }

    public static interface Listener {
        public void onRootAdded(Advancement var1);

        @Environment(value=EnvType.CLIENT)
        public void onRootRemoved(Advancement var1);

        public void onDependentAdded(Advancement var1);

        @Environment(value=EnvType.CLIENT)
        public void onDependentRemoved(Advancement var1);

        public void onClear();
    }
}

