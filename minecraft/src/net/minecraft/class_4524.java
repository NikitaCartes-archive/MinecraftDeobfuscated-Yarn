package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;

public class class_4524 {
	private final Collection<class_4517> field_20577 = Lists.<class_4517>newArrayList();
	@Nullable
	private class_4518 field_20578;

	public class_4524() {
	}

	public class_4524(Collection<class_4517> collection) {
		this.field_20577.addAll(collection);
	}

	public void method_22230(class_4517 arg) {
		this.field_20577.add(arg);
		if (this.field_20578 != null) {
			arg.method_22167(this.field_20578);
		}
	}

	public void method_22231(class_4518 arg) {
		this.field_20578 = arg;
		this.field_20577.forEach(arg2 -> arg2.method_22167(arg));
	}

	public int method_22229() {
		return (int)this.field_20577.stream().filter(class_4517::method_22178).filter(class_4517::method_22183).count();
	}

	public int method_22234() {
		return (int)this.field_20577.stream().filter(class_4517::method_22178).filter(class_4517::method_22184).count();
	}

	public int method_22235() {
		return (int)this.field_20577.stream().filter(class_4517::method_22180).count();
	}

	public boolean method_22236() {
		return this.method_22229() > 0;
	}

	public boolean method_22237() {
		return this.method_22234() > 0;
	}

	public int method_22238() {
		return this.field_20577.size();
	}

	public boolean method_22239() {
		return this.method_22235() == this.method_22238();
	}

	public String method_22240() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('[');
		this.field_20577.forEach(arg -> {
			if (!arg.method_22179()) {
				stringBuffer.append(' ');
			} else if (arg.method_22177()) {
				stringBuffer.append('√');
			} else if (arg.method_22178()) {
				stringBuffer.append((char)(arg.method_22183() ? 'X' : 'x'));
			} else {
				stringBuffer.append('_');
			}
		});
		stringBuffer.append(']');
		return stringBuffer.toString();
	}

	public String toString() {
		return this.method_22240();
	}
}
