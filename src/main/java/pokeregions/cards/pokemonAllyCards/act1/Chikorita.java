package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Bayleef;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.ChikoritaAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Chikorita extends AbstractAllyStarterPokemonCard {
    public final static String ID = makeID(Chikorita.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 5;
    public static final int MOVE_2_HP_LOSS = 6;
    public static final int MOVE_2_TEMP_HP = 4;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Chikorita() {
        super(ID, CardRarity.BASIC);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_HP_LOSS + DESCRIPTIONS[5] + MOVE_2_TEMP_HP + DESCRIPTIONS[6];
        initializeDescriptionFromMoves();
    }

    @Override
    public POKEMON_TYPE getType() {
        return POKEMON_TYPE.GRASS;
    }

    @Override
    public AbstractAllyStarterPokemonCard getNextStage() {
        return new Bayleef();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new ChikoritaAlly(x, y, this);
    }
}