package pokeregions.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Charmeleon;
import pokeregions.cards.pokemonAllyCards.act2.Ivysaur;
import pokeregions.cards.pokemonAllyCards.act2.Wartortle;

import static pokeregions.PokemonRegions.makeEventPath;
import static pokeregions.PokemonRegions.makeMonsterPath;

public class ProfessorElm extends AbstractProfessorEvent {

    public static final String ID = PokemonRegions.makeID(ProfessorElm.class.getSimpleName());
    public ProfessorElm() {
        eventStrings = CardCrawlGame.languagePack.getEventString(ID);
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        professor = new BetterSpriterAnimation(makeEventPath("Elm/Elm.scml"));
        starter1 = new BetterSpriterAnimation(makeMonsterPath("Ivysaur/Ivysaur.scml"));
        starter2 = new BetterSpriterAnimation(makeMonsterPath("Wartortle/Wartortle.scml"));
        starter3 = new BetterSpriterAnimation(makeMonsterPath("Charmeleon/Charmeleon.scml"));
        starter1Card = new Ivysaur();
        starter2Card = new Wartortle();
        starter3Card = new Charmeleon();
        if (!hasStarter) {
            this.talk(DESCRIPTIONS[0]);
        } else {
            this.talk(DESCRIPTIONS[4]);
        }
    }

}
