package io.github.adainish.cobbledcommunityday.task;

import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;
import io.github.adainish.cobbledcommunityday.wrapper.CommunityDayWrapper;

public class AnnouncementCheck implements Runnable{
    @Override
    public void run() {
        if (CobbledCommunityDay.wrapper.isCommunityDay()) {
            CommunityDayWrapper wrapper = CobbledCommunityDay.wrapper;
            if (wrapper.hasBeenAnnounced())
                return;
            wrapper.announceCommunityDay();
            wrapper.save();
        }
    }

}
