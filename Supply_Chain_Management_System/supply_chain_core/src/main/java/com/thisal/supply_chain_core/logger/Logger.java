package com.thisal.supply_chain_core.logger;

import com.thisal.supply_chain_core.annotation.Console;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class Logger {

    public void log(@Observes String message) {
        System.out.println("Logger: " + message);
    }

    public void consoleLog(@Observes @Console String message) {
        System.out.println("ConsoleLogger: " + message);
    }

}