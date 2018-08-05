package com.frank.myaws.actors.client;

import java.io.Serializable;

/**
 * @author ftorriani
 */
public interface ClientActionHandlerMessages {

    class ActionPerformed implements Serializable {
        private final String description;

        public ActionPerformed( String description ) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    class Disconnect implements Serializable {

    }

    class Connect implements Serializable {

    }

    class InternalConnect implements Serializable {

    }

    class Reconnect implements Serializable {

    }
}
