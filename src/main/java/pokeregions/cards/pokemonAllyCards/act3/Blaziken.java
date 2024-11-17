package pokeregions.cards.pokemonAllyCards.act3;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act3.allyPokemon.BlazikenAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Blaziken extends AbstractAllyStarterPokemonCard {
    public final static String ID = makeID(Blaziken.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 11;
    public static final int MOVE_1_EFFECT = 1;
    public static final int MOVE_2_DAMAGE = 14;
    public static final int MOVE_2_EFFECT = 2;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Blaziken() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + DESCRIPTIONS[4] + MOVE_1_EFFECT + DESCRIPTIONS[5];
        this.move2Description = DESCRIPTIONS[2] + MOVE_2_DAMAGE + DESCRIPTIONS[3] + DESCRIPTIONS[6] + MOVE_2_EFFECT + DESCRIPTIONS[7];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new BlazikenAlly(x, y, this);
    }
}