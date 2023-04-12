package io.github.adainish.cobbledcommunityday.obj;

public class DiscordAccount
{
    private long discordUserID;
    private boolean voted;
    private String selectedPokemon;

    public DiscordAccount(long userID) {
        setDiscordUserID(userID);
    }

    public long getDiscordUserID() {
        return discordUserID;
    }

    public void setDiscordUserID(long discordUserID) {
        this.discordUserID = discordUserID;
    }

    public boolean hasVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public String getSelectedPokemon() {
        return selectedPokemon;
    }

    public void setSelectedPokemon(String selectedPokemon) {
        this.selectedPokemon = selectedPokemon;
    }
}
