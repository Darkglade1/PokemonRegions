package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.ArbokAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Arbok extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Arbok.class.getSimpleName());
    public static final int MOVE_2_DAMAGE = 6;
    public static final int MOVE_1_TOXIC = 5;
    public static final int MOVE_2_WEAK = 2;
    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 6;

    public Arbok() {
        super(ID, CardRarity.UNCOMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_TOXIC + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_DAMAGE + DESCRIPTIONS[5] + MOVE_2_WEAK + DESCRIPTIONS[6];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new ArbokAlly(x, y, this);
    }
}