package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.VaporeonAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Vaporeon extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Vaporeon.class.getSimpleName());
    public static final int MOVE_2_BLOCK = 6;
    public static final int MOVE_1_BLOCK = 15;
    public static final int MOVE_1_HP_LOSS = 3;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 0;
    public static final int MAX_STAMINA = 5;

    public Vaporeon() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_HP_LOSS + DESCRIPTIONS[3] + DESCRIPTIONS[4] + MOVE_1_BLOCK + DESCRIPTIONS[5];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_BLOCK + DESCRIPTIONS[5];
        this.move2isLimited = true;
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new VaporeonAlly(x, y, this);
    }
}