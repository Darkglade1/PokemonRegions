package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.MewAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Mew extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Mew.class.getSimpleName());
    public static final int MAX_STAMINA = 6;
    public Mew() {
        super(ID, CardRarity.RARE);
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.overrideWithDescription = true;
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new MewAlly(x, y, this);
    }
}