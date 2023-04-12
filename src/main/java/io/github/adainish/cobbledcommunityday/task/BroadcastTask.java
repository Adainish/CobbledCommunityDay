package io.github.adainish.cobbledcommunityday.task;

import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.util.Util;

public class BroadcastTask implements Runnable {
    @Override
    public void run() {
        if (!CobbledCommunityDay.wrapper.isCommunityDay()) {
            return;
        }
        if (CobbledCommunityDay.wrapper.getSelectedCommunityPokemon() == null) {
            return;
        }
        String pkmn = CobbledCommunityDay.wrapper.getSelectedCommunityPokemon().getPokemonName();
        String bc = CobbledCommunityDay.config.broadCast;
        if (bc == null) {
            CobbledCommunityDay.getLog().error("Could not retrieve broadcast message");
            return;
        }
        String msg = bc.replace("%pkmn%", pkmn);
        Util.broadcast(msg);
    }

}
