package de.keksuccino.auudio.util.event.events;

import de.keksuccino.auudio.util.event.EventBase;

public class ClientTickEvent extends EventBase {

    @Override
    public boolean isCancelable() {
        return false;
    }

}
