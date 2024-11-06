package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.GastlyAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Gastly extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Gastly.class.getSimpleName());
    public static final int MOVE_1_DRAW = 2;
    public static final int MOVE_1_EFFECT = 3;
    public static final int MOVE_2_HP_COST = 5;
    public static final int MOVE_2_INTANGIBLE = 1;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 3;
    public static final int MAX_STAMINA = 4;

    public Gastly() {
        super(ID, CardRarity.UNCOMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DRAW + DESCRIPTIONS[3] + DESCRIPTIONS[4] + MOVE_1_EFFECT + DESCRIPTIONS[5];
        this.move2Description = DESCRIPTIONS[6] + MOVE_2_HP_COST + DESCRIPTIONS[7] + DESCRIPTIONS[8] + MOVE_2_INTANGIBLE + DESCRIPTIONS[9];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new GastlyAlly(x, y, this);
    }
}