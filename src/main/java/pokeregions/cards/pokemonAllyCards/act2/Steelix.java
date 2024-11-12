package pokeregions.cards.pokemonAllyCards.act2;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.SteelixAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Steelix extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Steelix.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 8;
    public static final int MOVE_1_EFFECT = 50;
    public static final int MOVE_2_EFFECT = 1;

    public static final int MOVE_1_STAMINA_COST = 1;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Steelix() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + MOVE_1_EFFECT + DESCRIPTIONS[4];
        this.move2Description = DESCRIPTIONS[5] + MOVE_2_EFFECT + DESCRIPTIONS[6];
        move2isLimited = true;
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new SteelixAlly(x, y, this);
    }
}