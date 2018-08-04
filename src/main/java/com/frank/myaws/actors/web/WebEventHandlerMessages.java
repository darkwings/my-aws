package com.frank.myaws.actors.web;

import java.io.Serializable;

/**
 * @author ftorriani
 */
public interface WebEventHandlerMessages {

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

    class Reconnect implements Serializable {

    }
}
