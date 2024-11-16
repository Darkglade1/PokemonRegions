package pokeregions.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyStarterPokemonCard;

import static pokeregions.PokemonRegions.makeEventPath;

public class ProfessorElm extends AbstractProfessorEvent {

    public static final String ID = PokemonRegions.makeID(ProfessorElm.class.getSimpleName());
    public ProfessorElm() {
        eventStrings = CardCrawlGame.languagePack.getEventString(ID);
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        professor = new BetterSpriterAnimation(makeEventPath("Elm/Elm.scml"));
        starterIDs = AbstractAllyStarterPokemonCard.selectStarters(AbstractAllyStarterPokemonCard.getTier2StarterIDsGrass(), AbstractAllyStarterPokemonCard.getTier2StarterIDsWater(), AbstractAllyStarterPokemonCard.getTier2StarterIDsFire());
        populateStarterAnimations();
        if (!hasStarter) {
            this.talk(DESCRIPTIONS[0]);
        } else {
            this.talk(DESCRIPTIONS[4]);
        }
    }

}
