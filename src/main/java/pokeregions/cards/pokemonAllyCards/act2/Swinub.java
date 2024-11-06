package pokeregions.cards.pokemonAllyCards.act2;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.SwinubAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Swinub extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Swinub.class.getSimpleName());
    public static final int MOVE_1_BLOCK = 13;
    public static final int MOVE_2_DRAW = 4;
    public static final int MOVE_2_EXHAUST = 2;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Swinub() {
        super(ID, CardRarity.COMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_BLOCK + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_DRAW + DESCRIPTIONS[5] + MOVE_2_EXHAUST + DESCRIPTIONS[6];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new SwinubAlly(x, y, this);
    }
}