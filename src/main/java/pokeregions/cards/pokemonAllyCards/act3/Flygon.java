package pokeregions.cards.pokemonAllyCards.act3;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act3.allyPokemon.FlygonAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Flygon extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Flygon.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 8;
    public static final int MOVE_1_DEBUFF = 2;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 3;
    public static final int MAX_STAMINA = 5;

    public Flygon() {
        super(ID, CardRarity.UNCOMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + MOVE_1_DEBUFF + DESCRIPTIONS[4];
        this.move2Description = DESCRIPTIONS[5];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new FlygonAlly(x, y, this);
    }
}