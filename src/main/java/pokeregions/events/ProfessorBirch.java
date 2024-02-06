package pokeregions.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Blastoise;
import pokeregions.cards.pokemonAllyCards.act3.Charizard;
import pokeregions.cards.pokemonAllyCards.act3.Venusaur;

import static pokeregions.PokemonRegions.makeEventPath;
import static pokeregions.PokemonRegions.makeMonsterPath;

public class ProfessorBirch extends AbstractProfessorEvent {

    public static final String ID = PokemonRegions.makeID(ProfessorBirch.class.getSimpleName());
    public ProfessorBirch() {
        eventStrings = CardCrawlGame.languagePack.getEventString(ID);
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        professor = new BetterSpriterAnimation(makeEventPath("Birch/Birch.scml"));
        starter1 = new BetterSpriterAnimation(makeMonsterPath("Venusaur/Venusaur.scml"));
        starter2 = new BetterSpriterAnimation(makeMonsterPath("Blastoise/Blastoise.scml"));
        starter3 = new BetterSpriterAnimation(makeMonsterPath("Charizard/Charizard.scml"));
        starter1Card = new Venusaur();
        starter2Card = new Blastoise();
        starter3Card = new Charizard();
        if (!hasStarter) {
            this.talk(DESCRIPTIONS[0]);
        } else {
            this.talk(DESCRIPTIONS[4]);
        }
    }

}
