package net.minecraft.util.snooper;

public interface SnooperListener {
	void addSnooperInfo(Snooper snooper);

	void addInitialSnooperInfo(Snooper snooper);

	boolean isSnooperEnabled();
}
