package net.minecraft;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_163 {
	private static final Logger field_1158 = LogManager.getLogger();
	private final Map<class_2960, class_161> field_1157 = Maps.<class_2960, class_161>newHashMap();
	private final Set<class_161> field_1154 = Sets.<class_161>newLinkedHashSet();
	private final Set<class_161> field_1156 = Sets.<class_161>newLinkedHashSet();
	private class_163.class_164 field_1155;

	@Environment(EnvType.CLIENT)
	private void method_718(class_161 arg) {
		for (class_161 lv : arg.method_681()) {
			this.method_718(lv);
		}

		field_1158.info("Forgot about advancement {}", arg.method_688());
		this.field_1157.remove(arg.method_688());
		if (arg.method_687() == null) {
			this.field_1154.remove(arg);
			if (this.field_1155 != null) {
				this.field_1155.method_720(arg);
			}
		} else {
			this.field_1156.remove(arg);
			if (this.field_1155 != null) {
				this.field_1155.method_719(arg);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_713(Set<class_2960> set) {
		for (class_2960 lv : set) {
			class_161 lv2 = (class_161)this.field_1157.get(lv);
			if (lv2 == null) {
				field_1158.warn("Told to remove advancement {} but I don't know what that is", lv);
			} else {
				this.method_718(lv2);
			}
		}
	}

	public void method_711(Map<class_2960, class_161.class_162> map) {
		Function<class_2960, class_161> function = Functions.forMap(this.field_1157, null);

		while (!map.isEmpty()) {
			boolean bl = false;
			Iterator<Entry<class_2960, class_161.class_162>> iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<class_2960, class_161.class_162> entry = (Entry<class_2960, class_161.class_162>)iterator.next();
				class_2960 lv = (class_2960)entry.getKey();
				class_161.class_162 lv2 = (class_161.class_162)entry.getValue();
				if (lv2.method_700(function)) {
					class_161 lv3 = lv2.method_695(lv);
					this.field_1157.put(lv, lv3);
					bl = true;
					iterator.remove();
					if (lv3.method_687() == null) {
						this.field_1154.add(lv3);
						if (this.field_1155 != null) {
							this.field_1155.method_723(lv3);
						}
					} else {
						this.field_1156.add(lv3);
						if (this.field_1155 != null) {
							this.field_1155.method_721(lv3);
						}
					}
				}
			}

			if (!bl) {
				for (Entry<class_2960, class_161.class_162> entry : map.entrySet()) {
					field_1158.error("Couldn't load advancement {}: {}", entry.getKey(), entry.getValue());
				}
				break;
			}
		}

		field_1158.info("Loaded {} advancements", this.field_1157.size());
	}

	@Environment(EnvType.CLIENT)
	public void method_714() {
		this.field_1157.clear();
		this.field_1154.clear();
		this.field_1156.clear();
		if (this.field_1155 != null) {
			this.field_1155.method_722();
		}
	}

	public Iterable<class_161> method_715() {
		return this.field_1154;
	}

	public Collection<class_161> method_712() {
		return this.field_1157.values();
	}

	@Nullable
	public class_161 method_716(class_2960 arg) {
		return (class_161)this.field_1157.get(arg);
	}

	@Environment(EnvType.CLIENT)
	public void method_717(@Nullable class_163.class_164 arg) {
		this.field_1155 = arg;
		if (arg != null) {
			for (class_161 lv : this.field_1154) {
				arg.method_723(lv);
			}

			for (class_161 lv : this.field_1156) {
				arg.method_721(lv);
			}
		}
	}

	public interface class_164 {
		void method_723(class_161 arg);

		@Environment(EnvType.CLIENT)
		void method_720(class_161 arg);

		void method_721(class_161 arg);

		@Environment(EnvType.CLIENT)
		void method_719(class_161 arg);

		@Environment(EnvType.CLIENT)
		void method_722();
	}
}
