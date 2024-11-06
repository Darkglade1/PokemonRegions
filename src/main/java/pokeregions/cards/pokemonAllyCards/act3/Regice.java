package pokeregions.cards.pokemonAllyCards.act3;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act3.allyPokemon.RegiceAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Regice extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Regice.class.getSimpleName());
    public static final int MOVE_1_BLOCK = 13;
    public static final int MOVE_2_EFFECT = 4;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 2;
    public static final int MAX_STAMINA = 6;

    public Regice() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_BLOCK + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_EFFECT + DESCRIPTIONS[5];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new RegiceAlly(x, y, this);
    }
}