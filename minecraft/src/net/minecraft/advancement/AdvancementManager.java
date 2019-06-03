package net.minecraft.advancement;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<Identifier, Advancement> advancements = Maps.<Identifier, Advancement>newHashMap();
	private final Set<Advancement> roots = Sets.<Advancement>newLinkedHashSet();
	private final Set<Advancement> dependents = Sets.<Advancement>newLinkedHashSet();
	private AdvancementManager.Listener listener;

	@Environment(EnvType.CLIENT)
	private void remove(Advancement advancement) {
		for (Advancement advancement2 : advancement.getChildren()) {
			this.remove(advancement2);
		}

		LOGGER.info("Forgot about advancement {}", advancement.getId());
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

	@Environment(EnvType.CLIENT)
	public void removeAll(Set<Identifier> set) {
		for (Identifier identifier : set) {
			Advancement advancement = (Advancement)this.advancements.get(identifier);
			if (advancement == null) {
				LOGGER.warn("Told to remove advancement {} but I don't know what that is", identifier);
			} else {
				this.remove(advancement);
			}
		}
	}

	public void load(Map<Identifier, Advancement.Task> map) {
		Function<Identifier, Advancement> function = Functions.forMap(this.advancements, null);

		while (!map.isEmpty()) {
			boolean bl = false;
			Iterator<Entry<Identifier, Advancement.Task>> iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Identifier, Advancement.Task> entry = (Entry<Identifier, Advancement.Task>)iterator.next();
				Identifier identifier = (Identifier)entry.getKey();
				Advancement.Task task = (Advancement.Task)entry.getValue();
				if (task.findParent(function)) {
					Advancement advancement = task.build(identifier);
					this.advancements.put(identifier, advancement);
					bl = true;
					iterator.remove();
					if (advancement.getParent() == null) {
						this.roots.add(advancement);
						if (this.listener != null) {
							this.listener.onRootAdded(advancement);
						}
					} else {
						this.dependents.add(advancement);
						if (this.listener != null) {
							this.listener.onDependentAdded(advancement);
						}
					}
				}
			}

			if (!bl) {
				for (Entry<Identifier, Advancement.Task> entry : map.entrySet()) {
					LOGGER.error("Couldn't load advancement {}: {}", entry.getKey(), entry.getValue());
				}
				break;
			}
		}

		LOGGER.info("Loaded {} advancements", this.advancements.size());
	}

	@Environment(EnvType.CLIENT)
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
		return (Advancement)this.advancements.get(identifier);
	}

	@Environment(EnvType.CLIENT)
	public void setListener(@Nullable AdvancementManager.Listener listener) {
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

	public interface Listener {
		void onRootAdded(Advancement advancement);

		@Environment(EnvType.CLIENT)
		void onRootRemoved(Advancement advancement);

		void onDependentAdded(Advancement advancement);

		@Environment(EnvType.CLIENT)
		void onDependentRemoved(Advancement advancement);

		@Environment(EnvType.CLIENT)
		void onClear();
	}
}
