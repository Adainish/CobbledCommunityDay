package io.github.adainish.cobbledcommunityday.storage;

import io.github.adainish.cobbledcommunityday.CobbledCommunityDay;

import java.io.File;

public class EmojiStorage
{

    public static File getEmojiFile(String name)
    {
        File dir = CobbledCommunityDay.getEmojiStorage();
        dir.mkdirs();
        return new File(dir, "%name%.gif".replace("%name%", name));
    }
}
