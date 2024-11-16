package pokeregions.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyStarterPokemonCard;

import static pokeregions.PokemonRegions.makeEventPath;

public class ProfessorOak extends AbstractProfessorEvent {

    public static final String ID = PokemonRegions.makeID(ProfessorOak.class.getSimpleName());
    public ProfessorOak() {
        eventStrings = CardCrawlGame.languagePack.getEventString(ID);
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        professor = new BetterSpriterAnimation(makeEventPath("Oak/Oak.scml"));
        starterIDs = AbstractAllyStarterPokemonCard.selectStarters(AbstractAllyStarterPokemonCard.getTier1StarterIDsGrass(), AbstractAllyStarterPokemonCard.getTier1StarterIDsWater(), AbstractAllyStarterPokemonCard.getTier1StarterIDsFire());
        populateStarterAnimations();
        if (!hasStarter) {
            this.talk(DESCRIPTIONS[0]);
        } else {
            this.talk(DESCRIPTIONS[4]);
        }
    }

}
