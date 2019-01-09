package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1328 implements class_1324 {
	private final class_1325 field_6346;
	private final class_1320 field_6350;
	private final Map<class_1322.class_1323, Set<class_1322>> field_6347 = Maps.newEnumMap(class_1322.class_1323.class);
	private final Map<String, Set<class_1322>> field_6345 = Maps.<String, Set<class_1322>>newHashMap();
	private final Map<UUID, class_1322> field_6343 = Maps.<UUID, class_1322>newHashMap();
	private double field_6349;
	private boolean field_6344 = true;
	private double field_6348;

	public class_1328(class_1325 arg, class_1320 arg2) {
		this.field_6346 = arg;
		this.field_6350 = arg2;
		this.field_6349 = arg2.method_6169();

		for (class_1322.class_1323 lv : class_1322.class_1323.values()) {
			this.field_6347.put(lv, Sets.newHashSet());
		}
	}

	@Override
	public class_1320 method_6198() {
		return this.field_6350;
	}

	@Override
	public double method_6201() {
		return this.field_6349;
	}

	@Override
	public void method_6192(double d) {
		if (d != this.method_6201()) {
			this.field_6349 = d;
			this.method_6217();
		}
	}

	@Override
	public Collection<class_1322> method_6193(class_1322.class_1323 arg) {
		return (Collection<class_1322>)this.field_6347.get(arg);
	}

	@Override
	public Collection<class_1322> method_6195() {
		Set<class_1322> set = Sets.<class_1322>newHashSet();

		for (class_1322.class_1323 lv : class_1322.class_1323.values()) {
			set.addAll(this.method_6193(lv));
		}

		return set;
	}

	@Nullable
	@Override
	public class_1322 method_6199(UUID uUID) {
		return (class_1322)this.field_6343.get(uUID);
	}

	@Override
	public boolean method_6196(class_1322 arg) {
		return this.field_6343.get(arg.method_6189()) != null;
	}

	@Override
	public void method_6197(class_1322 arg) {
		if (this.method_6199(arg.method_6189()) != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		} else {
			Set<class_1322> set = (Set<class_1322>)this.field_6345.computeIfAbsent(arg.method_6185(), string -> Sets.newHashSet());
			((Set)this.field_6347.get(arg.method_6182())).add(arg);
			set.add(arg);
			this.field_6343.put(arg.method_6189(), arg);
			this.method_6217();
		}
	}

	protected void method_6217() {
		this.field_6344 = true;
		this.field_6346.method_6211(this);
	}

	@Override
	public void method_6202(class_1322 arg) {
		for (class_1322.class_1323 lv : class_1322.class_1323.values()) {
			((Set)this.field_6347.get(lv)).remove(arg);
		}

		Set<class_1322> set = (Set<class_1322>)this.field_6345.get(arg.method_6185());
		if (set != null) {
			set.remove(arg);
			if (set.isEmpty()) {
				this.field_6345.remove(arg.method_6185());
			}
		}

		this.field_6343.remove(arg.method_6189());
		this.method_6217();
	}

	@Override
	public void method_6200(UUID uUID) {
		class_1322 lv = this.method_6199(uUID);
		if (lv != null) {
			this.method_6202(lv);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_6203() {
		Collection<class_1322> collection = this.method_6195();
		if (collection != null) {
			for (class_1322 lv : Lists.newArrayList(collection)) {
				this.method_6202(lv);
			}
		}
	}

	@Override
	public double method_6194() {
		if (this.field_6344) {
			this.field_6348 = this.method_6220();
			this.field_6344 = false;
		}

		return this.field_6348;
	}

	private double method_6220() {
		double d = this.method_6201();

		for (class_1322 lv : this.method_6218(class_1322.class_1323.field_6328)) {
			d += lv.method_6186();
		}

		double e = d;

		for (class_1322 lv2 : this.method_6218(class_1322.class_1323.field_6330)) {
			e += d * lv2.method_6186();
		}

		for (class_1322 lv2 : this.method_6218(class_1322.class_1323.field_6331)) {
			e *= 1.0 + lv2.method_6186();
		}

		return this.field_6350.method_6165(e);
	}

	private Collection<class_1322> method_6218(class_1322.class_1323 arg) {
		Set<class_1322> set = Sets.<class_1322>newHashSet(this.method_6193(arg));

		for (class_1320 lv = this.field_6350.method_6166(); lv != null; lv = lv.method_6166()) {
			class_1324 lv2 = this.field_6346.method_6205(lv);
			if (lv2 != null) {
				set.addAll(lv2.method_6193(arg));
			}
		}

		return set;
	}
}
