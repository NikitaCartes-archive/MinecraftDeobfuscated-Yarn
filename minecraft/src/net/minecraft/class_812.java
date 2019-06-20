package net.minecraft;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_812 implements class_815 {
	private final Iterable<? extends class_815> field_4324;

	public class_812(Iterable<? extends class_815> iterable) {
		this.field_4324 = iterable;
	}

	@Override
	public Predicate<class_2680> getPredicate(class_2689<class_2248, class_2680> arg) {
		List<Predicate<class_2680>> list = (List<Predicate<class_2680>>)Streams.stream(this.field_4324)
			.map(arg2 -> arg2.getPredicate(arg))
			.collect(Collectors.toList());
		return argx -> list.stream().allMatch(predicate -> predicate.test(argx));
	}
}
