package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Wartortle;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.SquirtleAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Squirtle extends AbstractAllyStarterPokemonCard {
    public final static String ID = makeID(Squirtle.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 5;
    public static final int MOVE_2_BLOCK = 8;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Squirtle() {
        super(ID, CardRarity.BASIC);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_BLOCK + DESCRIPTIONS[5];
        initializeDescriptionFromMoves();
    }

    @Override
    public POKEMON_TYPE getType() {
        return POKEMON_TYPE.WATER;
    }

    @Override
    public AbstractAllyStarterPokemonCard getNextStage() {
        return new Wartortle();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new SquirtleAlly(x, y, this);
    }
}