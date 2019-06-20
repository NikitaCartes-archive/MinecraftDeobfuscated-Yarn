package net.minecraft;

import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1355 {
	private static final Logger field_6466 = LogManager.getLogger();
	private static final class_4135 field_18410 = new class_4135(Integer.MAX_VALUE, new class_1352() {
		@Override
		public boolean method_6264() {
			return false;
		}
	}) {
		@Override
		public boolean method_19056() {
			return false;
		}
	};
	private final Map<class_1352.class_4134, class_4135> field_18411 = new EnumMap(class_1352.class_4134.class);
	private final Set<class_4135> field_6461 = Sets.<class_4135>newLinkedHashSet();
	private final class_3695 field_6463;
	private final EnumSet<class_1352.class_4134> field_6462 = EnumSet.noneOf(class_1352.class_4134.class);
	private int field_6464 = 3;

	public class_1355(class_3695 arg) {
		this.field_6463 = arg;
	}

	public void method_6277(int i, class_1352 arg) {
		this.field_6461.add(new class_4135(i, arg));
	}

	public void method_6280(class_1352 arg) {
		this.field_6461.stream().filter(arg2 -> arg2.method_19058() == arg).filter(class_4135::method_19056).forEach(class_4135::method_6270);
		this.field_6461.removeIf(arg2 -> arg2.method_19058() == arg);
	}

	public void method_6275() {
		this.field_6463.method_15396("goalCleanup");
		this.method_19048()
			.filter(arg -> !arg.method_19056() || arg.method_6271().stream().anyMatch(this.field_6462::contains) || !arg.method_6266())
			.forEach(class_1352::method_6270);
		this.field_18411.forEach((arg, arg2) -> {
			if (!arg2.method_19056()) {
				this.field_18411.remove(arg);
			}
		});
		this.field_6463.method_15407();
		this.field_6463.method_15396("goalUpdate");
		this.field_6461
			.stream()
			.filter(arg -> !arg.method_19056())
			.filter(arg -> arg.method_6271().stream().noneMatch(this.field_6462::contains))
			.filter(arg -> arg.method_6271().stream().allMatch(arg2 -> ((class_4135)this.field_18411.getOrDefault(arg2, field_18410)).method_19055(arg)))
			.filter(class_4135::method_6264)
			.forEach(arg -> {
				arg.method_6271().forEach(arg2 -> {
					class_4135 lv = (class_4135)this.field_18411.getOrDefault(arg2, field_18410);
					lv.method_6270();
					this.field_18411.put(arg2, arg);
				});
				arg.method_6269();
			});
		this.field_6463.method_15407();
		this.field_6463.method_15396("goalTick");
		this.method_19048().forEach(class_4135::method_6268);
		this.field_6463.method_15407();
	}

	public Stream<class_4135> method_19048() {
		return this.field_6461.stream().filter(class_4135::method_19056);
	}

	public void method_6274(class_1352.class_4134 arg) {
		this.field_6462.add(arg);
	}

	public void method_6273(class_1352.class_4134 arg) {
		this.field_6462.remove(arg);
	}

	public void method_6276(class_1352.class_4134 arg, boolean bl) {
		if (bl) {
			this.method_6273(arg);
		} else {
			this.method_6274(arg);
		}
	}
}
