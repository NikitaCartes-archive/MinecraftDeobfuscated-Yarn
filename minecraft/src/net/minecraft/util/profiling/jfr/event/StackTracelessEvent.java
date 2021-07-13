package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.Event;
import jdk.jfr.StackTrace;

@StackTrace(false)
public class StackTracelessEvent extends Event {
}
