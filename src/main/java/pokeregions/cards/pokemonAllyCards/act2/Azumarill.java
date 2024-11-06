package pokeregions.cards.pokemonAllyCards.act2;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.AzumarillAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Azumarill extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Azumarill.class.getSimpleName());
    public static final int MOVE_1_EFFECT = 150;
    public static final int MOVE_2_DAMAGE = 12;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 6;

    public Azumarill() {
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
        return new AzumarillAlly(x, y, this);
    }
}