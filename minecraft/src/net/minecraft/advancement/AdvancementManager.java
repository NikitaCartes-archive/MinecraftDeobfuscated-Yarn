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
	private final Set<SimpleAdvancement> field_1154 = Sets.<SimpleAdvancement>newLinkedHashSet();
	private final Set<SimpleAdvancement> field_1156 = Sets.<SimpleAdvancement>newLinkedHashSet();
	private AdvancementManager.class_164 field_1155;

	@Environment(EnvType.CLIENT)
	private void remove(SimpleAdvancement simpleAdvancement) {
		for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
			this.remove(simpleAdvancement2);
		}

		LOGGER.info("Forgot about advancement {}", simpleAdvancement.getId());
		this.advancements.remove(simpleAdvancement.getId());
		if (simpleAdvancement.getParent() == null) {
			this.field_1154.remove(simpleAdvancement);
			if (this.field_1155 != null) {
				this.field_1155.method_720(simpleAdvancement);
			}
		} else {
			this.field_1156.remove(simpleAdvancement);
			if (this.field_1155 != null) {
				this.field_1155.method_719(simpleAdvancement);
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

	public void method_711(Map<Identifier, SimpleAdvancement.Builder> map) {
		Function<Identifier, SimpleAdvancement> function = Functions.forMap(this.advancements, null);

		while (!map.isEmpty()) {
			boolean bl = false;
			Iterator<Entry<Identifier, SimpleAdvancement.Builder>> iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Identifier, SimpleAdvancement.Builder> entry = (Entry<Identifier, SimpleAdvancement.Builder>)iterator.next();
				Identifier identifier = (Identifier)entry.getKey();
				SimpleAdvancement.Builder builder = (SimpleAdvancement.Builder)entry.getValue();
				if (builder.method_700(function)) {
					SimpleAdvancement simpleAdvancement = builder.build(identifier);
					this.advancements.put(identifier, simpleAdvancement);
					bl = true;
					iterator.remove();
					if (simpleAdvancement.getParent() == null) {
						this.field_1154.add(simpleAdvancement);
						if (this.field_1155 != null) {
							this.field_1155.method_723(simpleAdvancement);
						}
					} else {
						this.field_1156.add(simpleAdvancement);
						if (this.field_1155 != null) {
							this.field_1155.method_721(simpleAdvancement);
						}
					}
				}
			}

			if (!bl) {
				for (Entry<Identifier, SimpleAdvancement.Builder> entry : map.entrySet()) {
					LOGGER.error("Couldn't load advancement {}: {}", entry.getKey(), entry.getValue());
				}
				break;
			}
		}

		LOGGER.info("Loaded {} advancements", this.advancements.size());
	}

	public void clear() {
		this.advancements.clear();
		this.field_1154.clear();
		this.field_1156.clear();
		if (this.field_1155 != null) {
			this.field_1155.method_722();
		}
	}

	public Iterable<SimpleAdvancement> method_715() {
		return this.field_1154;
	}

	public Collection<SimpleAdvancement> getAdvancements() {
		return this.advancements.values();
	}

	@Nullable
	public SimpleAdvancement get(Identifier identifier) {
		return (SimpleAdvancement)this.advancements.get(identifier);
	}

	@Environment(EnvType.CLIENT)
	public void method_717(@Nullable AdvancementManager.class_164 arg) {
		this.field_1155 = arg;
		if (arg != null) {
			for (SimpleAdvancement simpleAdvancement : this.field_1154) {
				arg.method_723(simpleAdvancement);
			}

			for (SimpleAdvancement simpleAdvancement : this.field_1156) {
				arg.method_721(simpleAdvancement);
			}
		}
	}

	public interface class_164 {
		void method_723(SimpleAdvancement simpleAdvancement);

		@Environment(EnvType.CLIENT)
		void method_720(SimpleAdvancement simpleAdvancement);

		void method_721(SimpleAdvancement simpleAdvancement);

		@Environment(EnvType.CLIENT)
		void method_719(SimpleAdvancement simpleAdvancement);

		void method_722();
	}
}
