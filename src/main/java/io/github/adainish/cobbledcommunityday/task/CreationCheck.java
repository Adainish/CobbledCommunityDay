package io.github.adainish.cobbledcommunityday.task;

import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;

public class CreationCheck implements Runnable{

    @Override
    public void run() {
        if (CobbledCommunityDay.wrapper.shouldCreateNewCommunityDay()) {
            CommunityDayWrapper wrapper = CobbledCommunityDay.wrapper;
            wrapper.createNewCommunityDay();
            wrapper.save();
        }
    }
}
