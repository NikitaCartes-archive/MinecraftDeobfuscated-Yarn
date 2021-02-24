/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
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
    public void removeAll(Set<Identifier> advancements) {
        for (Identifier identifier : advancements) {
            Advancement advancement = this.advancements.get(identifier);
            if (advancement == null) {
                LOGGER.warn("Told to remove advancement {} but I don't know what that is", (Object)identifier);
                continue;
            }
            this.remove(advancement);
        }
    }

    public void load(Map<Identifier, Advancement.Task> map) {
        HashMap<Identifier, Advancement.Task> map2 = Maps.newHashMap(map);
        while (!map2.isEmpty()) {
            boolean bl = false;
            Iterator iterator = map2.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                Identifier identifier = (Identifier)entry.getKey();
                Advancement.Task task = (Advancement.Task)entry.getValue();
                if (!task.findParent(this.advancements::get)) continue;
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
            for (Map.Entry entry : map2.entrySet()) {
                LOGGER.error("Couldn't load advancement {}: {}", entry.getKey(), entry.getValue());
            }
        }
        LOGGER.info("Loaded {} advancements", (Object)this.advancements.size());
    }

    @Environment(value=EnvType.CLIENT)
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
    public Advancement get(Identifier id) {
        return this.advancements.get(id);
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

        @Environment(value=EnvType.CLIENT)
        public void onClear();
    }
}

