package pokeregions.cards.pokemonAllyCards.act3;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act3.allyPokemon.SlakingAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Slaking extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Slaking.class.getSimpleName());
    public static final int MOVE_1_EFFECT = 1;
    public static final int MOVE_2_DAMAGE = 36;

    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 3;
    public static final int MAX_STAMINA = 6;

    public Slaking() {
        super(ID, CardRarity.UNCOMMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_EFFECT + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_DAMAGE + DESCRIPTIONS[5];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new SlakingAlly(x, y, this);
    }
}