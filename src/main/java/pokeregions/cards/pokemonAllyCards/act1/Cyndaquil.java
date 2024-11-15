package pokeregions.cards.pokemonAllyCards.act1;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.CyndaquilAlly;
import pokeregions.util.Tags;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Cyndaquil extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Cyndaquil.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 3;
    public static final int MOVE_1_HITS = 2;
    public static final int MOVE_2_DEBUFF = 3;
    public static final int MOVE_2_STATUS = 1;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Cyndaquil() {
        super(ID, CardRarity.BASIC);
        tags.add(Tags.STARTER_POKEMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + MOVE_1_HITS + DESCRIPTIONS[4];
        this.move2Description = DESCRIPTIONS[5] + MOVE_2_DEBUFF + DESCRIPTIONS[6] + MOVE_2_STATUS + DESCRIPTIONS[7];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new CyndaquilAlly(x, y, this);
    }
}