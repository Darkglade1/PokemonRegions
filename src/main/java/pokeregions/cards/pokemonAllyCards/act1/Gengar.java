package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.GengarAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Gengar extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Gengar.class.getSimpleName());
    public static final int MOVE_1_CARDS = 2;
    public static final int MOVE_2_DAMAGE = 13;
    public static final int MOVE_1_STAMINA_COST = 2;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Gengar() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_CARDS + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_DAMAGE + DESCRIPTIONS[5];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new GengarAlly(x, y, this);
    }
}