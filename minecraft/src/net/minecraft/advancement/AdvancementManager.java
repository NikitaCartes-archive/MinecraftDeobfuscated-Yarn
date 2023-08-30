package net.minecraft.advancement;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class AdvancementManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<Identifier, PlacedAdvancement> advancements = new Object2ObjectOpenHashMap<>();
	private final Set<PlacedAdvancement> roots = new ObjectLinkedOpenHashSet<>();
	private final Set<PlacedAdvancement> dependents = new ObjectLinkedOpenHashSet<>();
	@Nullable
	private AdvancementManager.Listener listener;

	private void remove(PlacedAdvancement advancement) {
		for (PlacedAdvancement placedAdvancement : advancement.getChildren()) {
			this.remove(placedAdvancement);
		}

		LOGGER.info("Forgot about advancement {}", advancement.getAdvancementEntry());
		this.advancements.remove(advancement.getAdvancementEntry().id());
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

	public void removeAll(Set<Identifier> advancements) {
		for (Identifier identifier : advancements) {
			PlacedAdvancement placedAdvancement = (PlacedAdvancement)this.advancements.get(identifier);
			if (placedAdvancement == null) {
				LOGGER.warn("Told to remove advancement {} but I don't know what that is", identifier);
			} else {
				this.remove(placedAdvancement);
			}
		}
	}

	public void addAll(Collection<AdvancementEntry> advancements) {
		List<AdvancementEntry> list = new ArrayList(advancements);

		while (!list.isEmpty()) {
			if (!list.removeIf(this::tryAdd)) {
				LOGGER.error("Couldn't load advancements: {}", list);
				break;
			}
		}

		LOGGER.info("Loaded {} advancements", this.advancements.size());
	}

	private boolean tryAdd(AdvancementEntry advancement) {
		Optional<Identifier> optional = advancement.value().parent();
		PlacedAdvancement placedAdvancement = (PlacedAdvancement)optional.map(this.advancements::get).orElse(null);
		if (placedAdvancement == null && optional.isPresent()) {
			return false;
		} else {
			PlacedAdvancement placedAdvancement2 = new PlacedAdvancement(advancement, placedAdvancement);
			if (placedAdvancement != null) {
				placedAdvancement.addChild(placedAdvancement2);
			}

			this.advancements.put(advancement.id(), placedAdvancement2);
			if (placedAdvancement == null) {
				this.roots.add(placedAdvancement2);
				if (this.listener != null) {
					this.listener.onRootAdded(placedAdvancement2);
				}
			} else {
				this.dependents.add(placedAdvancement2);
				if (this.listener != null) {
					this.listener.onDependentAdded(placedAdvancement2);
				}
			}

			return true;
		}
	}

	public void clear() {
		this.advancements.clear();
		this.roots.clear();
		this.dependents.clear();
		if (this.listener != null) {
			this.listener.onClear();
		}
	}

	public Iterable<PlacedAdvancement> getRoots() {
		return this.roots;
	}

	public Collection<PlacedAdvancement> getAdvancements() {
		return this.advancements.values();
	}

	@Nullable
	public PlacedAdvancement get(Identifier id) {
		return (PlacedAdvancement)this.advancements.get(id);
	}

	@Nullable
	public PlacedAdvancement get(AdvancementEntry advancement) {
		return (PlacedAdvancement)this.advancements.get(advancement.id());
	}

	public void setListener(@Nullable AdvancementManager.Listener listener) {
		this.listener = listener;
		if (listener != null) {
			for (PlacedAdvancement placedAdvancement : this.roots) {
				listener.onRootAdded(placedAdvancement);
			}

			for (PlacedAdvancement placedAdvancement : this.dependents) {
				listener.onDependentAdded(placedAdvancement);
			}
		}
	}

	public interface Listener {
		void onRootAdded(PlacedAdvancement root);

		void onRootRemoved(PlacedAdvancement root);

		void onDependentAdded(PlacedAdvancement dependent);

		void onDependentRemoved(PlacedAdvancement dependent);

		void onClear();
	}
}
