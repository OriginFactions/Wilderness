package org.originmc.wilderness;

import org.bukkit.Location;
import org.originmc.wilderness.factions.api.FactionsHelper;

public final class FactionsManager {

    private final FactionsHelper helper;

    FactionsManager(FactionsHelper helper) {
        this.helper = helper;
    }

    public boolean isInTerritory(Location loc) {
        return helper.isInTerritory(loc);
    }

}
