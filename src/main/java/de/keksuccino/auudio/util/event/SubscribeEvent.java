//Copyright (c) 2020 Keksuccino
package de.keksuccino.auudio.util.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SubscribeEvent {
	
	EventPriority priority() default EventPriority.NORMAL;
	
}
