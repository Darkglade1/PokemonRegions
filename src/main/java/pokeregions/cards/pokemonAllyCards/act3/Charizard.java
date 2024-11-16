package pokeregions.cards.pokemonAllyCards.act3;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act3.allyPokemon.CharizardAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Charizard extends AbstractAllyStarterPokemonCard {
    public final static String ID = makeID(Charizard.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 10;
    public static final int MOVE_1_DEBUFF = 1;
    public static final int MOVE_2_DAMAGE = 15;
    public static final int MOVE_2_DEBUFF = 2;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Charizard() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + MOVE_1_DEBUFF + DESCRIPTIONS[4];
        this.move2Description = DESCRIPTIONS[5] + MOVE_2_DAMAGE + DESCRIPTIONS[6] + MOVE_2_DEBUFF + DESCRIPTIONS[7];
        initializeDescriptionFromMoves();
    }

    @Override
    public POKEMON_TYPE getType() {
        return POKEMON_TYPE.FIRE;
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new CharizardAlly(x, y, this);
    }
}