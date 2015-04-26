package org.originmc.wilderness.factions.v2_6;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Location;
import org.originmc.wilderness.factions.api.FactionsHelper;

public class FactionsHelperImpl implements FactionsHelper {

    @Override
    public boolean isInTerritory(Location loc) {
        PS ps = PS.valueOf(loc);
        Faction faction = BoardColls.get().getFactionAt(ps);
        return !faction.isNone();
    }

}
