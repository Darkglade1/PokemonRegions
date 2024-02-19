package pokeregions.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Bulbasaur;
import pokeregions.cards.pokemonAllyCards.act1.Charmander;
import pokeregions.cards.pokemonAllyCards.act1.Squirtle;

import static pokeregions.PokemonRegions.makeEventPath;
import static pokeregions.PokemonRegions.makeMonsterPath;

public class ProfessorOak extends AbstractProfessorEvent {

    public static final String ID = PokemonRegions.makeID(ProfessorOak.class.getSimpleName());
    public ProfessorOak() {
        eventStrings = CardCrawlGame.languagePack.getEventString(ID);
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        professor = new BetterSpriterAnimation(makeEventPath("Oak/Oak.scml"));
        starter1 = new BetterSpriterAnimation(makeMonsterPath("Bulbasaur/Bulbasaur.scml"));
        starter2 = new BetterSpriterAnimation(makeMonsterPath("Squirtle/Squirtle.scml"));
        starter3 = new BetterSpriterAnimation(makeMonsterPath("Charmander/Charmander.scml"));
        starter1Card = new Bulbasaur();
        starter2Card = new Squirtle();
        starter3Card = new Charmander();
        if (!hasStarter) {
            this.talk(DESCRIPTIONS[0]);
        } else {
            this.talk(DESCRIPTIONS[4]);
        }
    }

}
