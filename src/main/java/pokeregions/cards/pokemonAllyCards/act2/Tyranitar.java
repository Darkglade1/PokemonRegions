package pokeregions.cards.pokemonAllyCards.act2;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.TyranitarAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Tyranitar extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Tyranitar.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 24;
    public static final int MOVE_1_EFFECT = 3;
    public static final int MOVE_2_DAMAGE = 9;
    public static final int MOVE_2_BLOCK = 9;

    public static final int MOVE_1_STAMINA_COST = 3;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 6;

    public Tyranitar() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + MOVE_1_EFFECT + DESCRIPTIONS[4];
        this.move2Description = DESCRIPTIONS[5] + MOVE_2_BLOCK + DESCRIPTIONS[6] + MOVE_2_DAMAGE + DESCRIPTIONS[7];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new TyranitarAlly(x, y, this);
    }
}