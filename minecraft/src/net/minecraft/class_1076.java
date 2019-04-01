package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1076 implements class_4013 {
	private static final Logger field_5325 = LogManager.getLogger();
	protected static final class_1078 field_5322 = new class_1078();
	private String field_5323;
	private final Map<String, class_1077> field_5324 = Maps.<String, class_1077>newHashMap();

	public class_1076(String string) {
		this.field_5323 = string;
		class_1074.method_4661(field_5322);
	}

	public void method_4670(List<class_3262> list) {
		this.field_5324.clear();

		for (class_3262 lv : list) {
			try {
				class_1082 lv2 = lv.method_14407(class_1082.field_5343);
				if (lv2 != null) {
					for (class_1077 lv3 : lv2.method_4694()) {
						if (!this.field_5324.containsKey(lv3.getCode())) {
							this.field_5324.put(lv3.getCode(), lv3);
						}
					}
				}
			} catch (IOException | RuntimeException var7) {
				field_5325.warn("Unable to parse language metadata section of resourcepack: {}", lv.method_14409(), var7);
			}
		}
	}

	@Override
	public void method_14491(class_3300 arg) {
		List<String> list = Lists.<String>newArrayList("en_us");
		if (!"en_us".equals(this.field_5323)) {
			list.add(this.field_5323);
		}

		field_5322.method_4675(arg, list);
		class_2477.method_10515(field_5322.field_5330);
	}

	public boolean method_4666() {
		return this.method_4669() != null && this.method_4669().method_4672();
	}

	public void method_4667(class_1077 arg) {
		this.field_5323 = arg.getCode();
	}

	public class_1077 method_4669() {
		String string = this.field_5324.containsKey(this.field_5323) ? this.field_5323 : "en_us";
		return (class_1077)this.field_5324.get(string);
	}

	public SortedSet<class_1077> method_4665() {
		return Sets.<class_1077>newTreeSet(this.field_5324.values());
	}

	public class_1077 method_4668(String string) {
		return (class_1077)this.field_5324.get(string);
	}
}
