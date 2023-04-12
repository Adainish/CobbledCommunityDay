package io.github.adainish.cobbledcommunityday.task;

import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;

public class VotingCheck implements Runnable{

    @Override
    public void run() {
        if (CobbledCommunityDay.wrapper.isVotingOpen()) {
            if (CobbledCommunityDay.wrapper.shouldVotingBeOpen())
                return;
            CommunityDayWrapper wrapper = CobbledCommunityDay.wrapper;
            wrapper.endVoting();
            wrapper.save();
        } else {
            if (CobbledCommunityDay.wrapper.getSelectedCommunityPokemon() == null) {
                CommunityDayWrapper wrapper = CobbledCommunityDay.wrapper;
                wrapper.selectWinner();
                wrapper.save();
            }
        }
    }
}
