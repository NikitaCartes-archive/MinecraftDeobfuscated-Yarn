package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public class class_3004 {
	private final MinecraftServer field_13448;
	private final Map<class_2960, class_3002> field_13447 = Maps.<class_2960, class_3002>newHashMap();

	public class_3004(MinecraftServer minecraftServer) {
		this.field_13448 = minecraftServer;
	}

	@Nullable
	public class_3002 method_12971(class_2960 arg) {
		return (class_3002)this.field_13447.get(arg);
	}

	public class_3002 method_12970(class_2960 arg, class_2561 arg2) {
		class_3002 lv = new class_3002(arg, arg2);
		this.field_13447.put(arg, lv);
		return lv;
	}

	public void method_12973(class_3002 arg) {
		this.field_13447.remove(arg.method_12959());
	}

	public Collection<class_2960> method_12968() {
		return this.field_13447.keySet();
	}

	public Collection<class_3002> method_12969() {
		return this.field_13447.values();
	}

	public class_2487 method_12974() {
		class_2487 lv = new class_2487();

		for (class_3002 lv2 : this.field_13447.values()) {
			lv.method_10566(lv2.method_12959().toString(), lv2.method_12963());
		}

		return lv;
	}

	public void method_12972(class_2487 arg) {
		for (String string : arg.method_10541()) {
			class_2960 lv = new class_2960(string);
			this.field_13447.put(lv, class_3002.method_12966(arg.method_10562(string), lv));
		}
	}

	public void method_12975(class_3222 arg) {
		for (class_3002 lv : this.field_13447.values()) {
			lv.method_12957(arg);
		}
	}

	public void method_12976(class_3222 arg) {
		for (class_3002 lv : this.field_13447.values()) {
			lv.method_12961(arg);
		}
	}
}
