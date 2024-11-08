package pokeregions.cards.pokemonAllyCards.act2;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.MagcargoAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Magcargo extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Magcargo.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 3;
    public static final int MOVE_1_HITS = 6;
    public static final int MOVE_2_BLOCK = 14;
    public static final int MOVE_2_STATUS = 1;
    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Magcargo() {
        super(ID, CardRarity.UNCOMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + MOVE_1_HITS + DESCRIPTIONS[4];
        this.move2Description = DESCRIPTIONS[5] + MOVE_2_BLOCK + DESCRIPTIONS[6] + MOVE_2_STATUS + DESCRIPTIONS[7];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new MagcargoAlly(x, y, this);
    }
}