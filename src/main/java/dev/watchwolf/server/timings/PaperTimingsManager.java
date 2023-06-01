package dev.watchwolf.server.timings;

import dev.watchwolf.server.ExtendedPetitionManager;
import dev.watchwolf.server.Server;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PaperTimingsManager  extends ExtendedPetitionManager implements TimingsOperator {
    public PaperTimingsManager(Server watchwolf, Plugin _watchwolf) {
        super(watchwolf, _watchwolf);
    }

    public PaperTimingsManager(Server watchwolf) {
        this(watchwolf, watchwolf);
    }

    @Override
    public void startTimings() {
        try {
            this.getWatchWolf().runCommand("timings on");
        } catch (IOException ignore) {}
    }

    @Override
    public String stopTimings() throws TimeoutException {
        String result = null;
        try {
            result = this.getWatchWolf().runCommand("timings report"); // TODO check if error
            // TODO it doesn't return the url immediately
            this.getWatchWolf().runCommand("timings off");
        } catch (IOException ignore) {}
        return result;
    }
}

