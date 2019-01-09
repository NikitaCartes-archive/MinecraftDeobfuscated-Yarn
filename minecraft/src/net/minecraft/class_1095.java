package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.tuple.Pair;

@Environment(EnvType.CLIENT)
public class class_1095 implements class_1087 {
	private final List<Pair<Predicate<class_2680>, class_1087>> field_5427;
	protected final boolean field_5430;
	protected final boolean field_5429;
	protected final class_1058 field_5425;
	protected final class_809 field_5426;
	protected final class_806 field_5428;
	private final Map<class_2680, BitSet> field_5431 = new Object2ObjectOpenCustomHashMap<>(class_156.method_655());

	public class_1095(List<Pair<Predicate<class_2680>, class_1087>> list) {
		this.field_5427 = list;
		class_1087 lv = (class_1087)((Pair)list.iterator().next()).getRight();
		this.field_5430 = lv.method_4708();
		this.field_5429 = lv.method_4712();
		this.field_5425 = lv.method_4711();
		this.field_5426 = lv.method_4709();
		this.field_5428 = lv.method_4710();
	}

	@Override
	public List<class_777> method_4707(@Nullable class_2680 arg, @Nullable class_2350 arg2, Random random) {
		if (arg == null) {
			return Collections.emptyList();
		} else {
			BitSet bitSet = (BitSet)this.field_5431.get(arg);
			if (bitSet == null) {
				bitSet = new BitSet();

				for (int i = 0; i < this.field_5427.size(); i++) {
					Pair<Predicate<class_2680>, class_1087> pair = (Pair<Predicate<class_2680>, class_1087>)this.field_5427.get(i);
					if (pair.getLeft().test(arg)) {
						bitSet.set(i);
					}
				}

				this.field_5431.put(arg, bitSet);
			}

			List<class_777> list = Lists.<class_777>newArrayList();
			long l = random.nextLong();

			for (int j = 0; j < bitSet.length(); j++) {
				if (bitSet.get(j)) {
					list.addAll(((class_1087)((Pair)this.field_5427.get(j)).getRight()).method_4707(arg, arg2, new Random(l)));
				}
			}

			return list;
		}
	}

	@Override
	public boolean method_4708() {
		return this.field_5430;
	}

	@Override
	public boolean method_4712() {
		return this.field_5429;
	}

	@Override
	public boolean method_4713() {
		return false;
	}

	@Override
	public class_1058 method_4711() {
		return this.field_5425;
	}

	@Override
	public class_809 method_4709() {
		return this.field_5426;
	}

	@Override
	public class_806 method_4710() {
		return this.field_5428;
	}

	@Environment(EnvType.CLIENT)
	public static class class_1096 {
		private final List<Pair<Predicate<class_2680>, class_1087>> field_5432 = Lists.<Pair<Predicate<class_2680>, class_1087>>newArrayList();

		public void method_4749(Predicate<class_2680> predicate, class_1087 arg) {
			this.field_5432.add(Pair.of(predicate, arg));
		}

		public class_1087 method_4750() {
			return new class_1095(this.field_5432);
		}
	}
}
