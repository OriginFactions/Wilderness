package org.originmc.wilderness.factions.v1_6;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.Location;
import org.originmc.wilderness.factions.api.FactionsHelper;

public class FactionsHelperImpl implements FactionsHelper {

    @Override
    public boolean isInTerritory(Location loc) {
        FLocation flocation = new FLocation(loc);
        Faction faction = Board.getInstance().getFactionAt(flocation);
        return !faction.isNone();
    }

}
