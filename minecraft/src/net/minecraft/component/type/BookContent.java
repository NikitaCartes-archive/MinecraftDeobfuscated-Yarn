package net.minecraft.component.type;

import java.util.List;
import net.minecraft.text.RawFilteredPair;

public interface BookContent<T, C> {
	List<RawFilteredPair<T>> pages();

	C withPages(List<RawFilteredPair<T>> pages);
}
