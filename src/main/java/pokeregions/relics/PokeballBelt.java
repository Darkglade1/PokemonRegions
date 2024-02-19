package pokeregions.relics;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pokeregions.actions.HealPokemonCampfireOption;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.ui.PokemonTeamButton;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.CoffeeDripper;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class PokeballBelt extends AbstractEasyRelic implements CustomSavable<String> {
    public static final String ID = makeID(PokeballBelt.class.getSimpleName());
    public static final int STARTING_POKEBALLS = 6;

    public PokeballBelt() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
        this.counter = STARTING_POKEBALLS;
        fixDescription();
    }

    @Override
    public void onEquip() {
        PokemonTeamButton pokemonTeam = new PokemonTeamButton();
        BaseMod.addTopPanelItem(pokemonTeam);
    }

    public void increment(int amount) {
        if (this.counter < 0) {
            this.counter = 0;
        }
        this.counter += amount;
        this.flash();
        fixDescription();
    }

    @Override
    public void setCounter(int counter) {
        super.setCounter(counter);
        fixDescription();
    }

    @Override
    public void fixDescription() {
        description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        options.add(new HealPokemonCampfireOption());
    }

    @Override
    public boolean canUseCampfireOption(AbstractCampfireOption option) {
        if (option instanceof HealPokemonCampfireOption && adp().hasRelic(CoffeeDripper.ID)) {
            ((HealPokemonCampfireOption) option).updateUsability(false);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + this.counter + DESCRIPTIONS[1];
    }

    @Override
    public String onSave() {
        return PlayerSpireFields.mostRecentlyUsedPokemonCardID.get(adp());
    }

    @Override
    public void onLoad(String cardID) {
        PlayerSpireFields.mostRecentlyUsedPokemonCardID.set(adp(), cardID);
    }
}
