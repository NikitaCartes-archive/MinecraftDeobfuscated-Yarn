package net.minecraft.util.profiling.jfr;

import java.io.IOException;

public interface JfrReport {
	String toString(JfrProfile profile) throws IOException;
}
