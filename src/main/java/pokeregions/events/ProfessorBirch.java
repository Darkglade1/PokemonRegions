package pokeregions.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyStarterPokemonCard;

import static pokeregions.PokemonRegions.makeEventPath;

public class ProfessorBirch extends AbstractProfessorEvent {

    public static final String ID = PokemonRegions.makeID(ProfessorBirch.class.getSimpleName());
    public ProfessorBirch() {
        eventStrings = CardCrawlGame.languagePack.getEventString(ID);
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        professor = new BetterSpriterAnimation(makeEventPath("Birch/Birch.scml"));
        starterIDs = AbstractAllyStarterPokemonCard.selectStarters(AbstractAllyStarterPokemonCard.getTier3StarterIDsGrass(), AbstractAllyStarterPokemonCard.getTier3StarterIDsWater(), AbstractAllyStarterPokemonCard.getTier3StarterIDsFire());
        populateStarterAnimations();
        if (!hasStarter) {
            this.talk(DESCRIPTIONS[0]);
        } else {
            this.talk(DESCRIPTIONS[4]);
        }
    }

}
