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
	private final Map<Identifier, SimpleAdvancement> advancements = Maps.<Identifier, SimpleAdvancement>newHashMap();
	private final Set<SimpleAdvancement> roots = Sets.<SimpleAdvancement>newLinkedHashSet();
	private final Set<SimpleAdvancement> dependents = Sets.<SimpleAdvancement>newLinkedHashSet();
	private AdvancementManager.Listener listener;

	@Environment(EnvType.CLIENT)
	private void remove(SimpleAdvancement simpleAdvancement) {
		for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
			this.remove(simpleAdvancement2);
		}

		LOGGER.info("Forgot about advancement {}", simpleAdvancement.getId());
		this.advancements.remove(simpleAdvancement.getId());
		if (simpleAdvancement.getParent() == null) {
			this.roots.remove(simpleAdvancement);
			if (this.listener != null) {
				this.listener.onRootRemoved(simpleAdvancement);
			}
		} else {
			this.dependents.remove(simpleAdvancement);
			if (this.listener != null) {
				this.listener.onDependentRemoved(simpleAdvancement);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public void removeAll(Set<Identifier> set) {
		for (Identifier identifier : set) {
			SimpleAdvancement simpleAdvancement = (SimpleAdvancement)this.advancements.get(identifier);
			if (simpleAdvancement == null) {
				LOGGER.warn("Told to remove advancement {} but I don't know what that is", identifier);
			} else {
				this.remove(simpleAdvancement);
			}
		}
	}

	public void load(Map<Identifier, SimpleAdvancement.Task> map) {
		Function<Identifier, SimpleAdvancement> function = Functions.forMap(this.advancements, null);

		while (!map.isEmpty()) {
			boolean bl = false;
			Iterator<Entry<Identifier, SimpleAdvancement.Task>> iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Identifier, SimpleAdvancement.Task> entry = (Entry<Identifier, SimpleAdvancement.Task>)iterator.next();
				Identifier identifier = (Identifier)entry.getKey();
				SimpleAdvancement.Task task = (SimpleAdvancement.Task)entry.getValue();
				if (task.findParent(function)) {
					SimpleAdvancement simpleAdvancement = task.build(identifier);
					this.advancements.put(identifier, simpleAdvancement);
					bl = true;
					iterator.remove();
					if (simpleAdvancement.getParent() == null) {
						this.roots.add(simpleAdvancement);
						if (this.listener != null) {
							this.listener.onRootAdded(simpleAdvancement);
						}
					} else {
						this.dependents.add(simpleAdvancement);
						if (this.listener != null) {
							this.listener.onDependentAdded(simpleAdvancement);
						}
					}
				}
			}

			if (!bl) {
				for (Entry<Identifier, SimpleAdvancement.Task> entry : map.entrySet()) {
					LOGGER.error("Couldn't load advancement {}: {}", entry.getKey(), entry.getValue());
				}
				break;
			}
		}

		LOGGER.info("Loaded {} advancements", this.advancements.size());
	}

	public void clear() {
		this.advancements.clear();
		this.roots.clear();
		this.dependents.clear();
		if (this.listener != null) {
			this.listener.onClear();
		}
	}

	public Iterable<SimpleAdvancement> getRoots() {
		return this.roots;
	}

	public Collection<SimpleAdvancement> getAdvancements() {
		return this.advancements.values();
	}

	@Nullable
	public SimpleAdvancement get(Identifier identifier) {
		return (SimpleAdvancement)this.advancements.get(identifier);
	}

	@Environment(EnvType.CLIENT)
	public void setListener(@Nullable AdvancementManager.Listener listener) {
		this.listener = listener;
		if (listener != null) {
			for (SimpleAdvancement simpleAdvancement : this.roots) {
				listener.onRootAdded(simpleAdvancement);
			}

			for (SimpleAdvancement simpleAdvancement : this.dependents) {
				listener.onDependentAdded(simpleAdvancement);
			}
		}
	}

	public interface Listener {
		void onRootAdded(SimpleAdvancement simpleAdvancement);

		@Environment(EnvType.CLIENT)
		void onRootRemoved(SimpleAdvancement simpleAdvancement);

		void onDependentAdded(SimpleAdvancement simpleAdvancement);

		@Environment(EnvType.CLIENT)
		void onDependentRemoved(SimpleAdvancement simpleAdvancement);

		void onClear();
	}
}
