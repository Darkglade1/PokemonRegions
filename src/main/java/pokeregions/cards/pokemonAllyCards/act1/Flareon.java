package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.FlareonAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Flareon extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Flareon.class.getSimpleName());
    public static final int MOVE_2_DRAW = 4;
    public static final int MOVE_2_EXHAUST = 1;
    public static final int MOVE_1_DAMAGE = 24;
    public static final int MOVE_1_BURN = 1;

    public static final int MOVE_1_STAMINA_COST = 2;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Flareon() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + MOVE_1_BURN + DESCRIPTIONS[4];
        this.move2Description = DESCRIPTIONS[5] + MOVE_2_DRAW + DESCRIPTIONS[6] + MOVE_2_EXHAUST + DESCRIPTIONS[7];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new FlareonAlly(x, y, this);
    }
}