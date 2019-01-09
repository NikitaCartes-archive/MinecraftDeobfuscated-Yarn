package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_632 {
	private static final Logger field_3686 = LogManager.getLogger();
	private final class_310 field_3684;
	private final class_163 field_3683 = new class_163();
	private final Map<class_161, class_167> field_3681 = Maps.<class_161, class_167>newHashMap();
	@Nullable
	private class_632.class_633 field_3682;
	@Nullable
	private class_161 field_3685;

	public class_632(class_310 arg) {
		this.field_3684 = arg;
	}

	public void method_2861(class_2779 arg) {
		if (arg.method_11924()) {
			this.field_3683.method_714();
			this.field_3681.clear();
		}

		this.field_3683.method_713(arg.method_11926());
		this.field_3683.method_711(arg.method_11928());

		for (Entry<class_2960, class_167> entry : arg.method_11927().entrySet()) {
			class_161 lv = this.field_3683.method_716((class_2960)entry.getKey());
			if (lv != null) {
				class_167 lv2 = (class_167)entry.getValue();
				lv2.method_727(lv.method_682(), lv.method_680());
				this.field_3681.put(lv, lv2);
				if (this.field_3682 != null) {
					this.field_3682.method_2865(lv, lv2);
				}

				if (!arg.method_11924() && lv2.method_740() && lv.method_686() != null && lv.method_686().method_823()) {
					this.field_3684.method_1566().method_1999(new class_367(lv));
				}
			} else {
				field_3686.warn("Server informed client about progress for unknown advancement {}", entry.getKey());
			}
		}
	}

	public class_163 method_2863() {
		return this.field_3683;
	}

	public void method_2864(@Nullable class_161 arg, boolean bl) {
		class_634 lv = this.field_3684.method_1562();
		if (lv != null && arg != null && bl) {
			lv.method_2883(class_2859.method_12418(arg));
		}

		if (this.field_3685 != arg) {
			this.field_3685 = arg;
			if (this.field_3682 != null) {
				this.field_3682.method_2866(arg);
			}
		}
	}

	public void method_2862(@Nullable class_632.class_633 arg) {
		this.field_3682 = arg;
		this.field_3683.method_717(arg);
		if (arg != null) {
			for (Entry<class_161, class_167> entry : this.field_3681.entrySet()) {
				arg.method_2865((class_161)entry.getKey(), (class_167)entry.getValue());
			}

			arg.method_2866(this.field_3685);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_633 extends class_163.class_164 {
		void method_2865(class_161 arg, class_167 arg2);

		void method_2866(@Nullable class_161 arg);
	}
}
