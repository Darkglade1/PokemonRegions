package pokeregions.cards.pokemonAllyCards.act3;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act3.allyPokemon.KyogreAlly;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Kyogre extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Kyogre.class.getSimpleName());
    public static final int MOVE_1_HEAL = 15;
    public static final int MOVE_2_DEBUFF = 2;

    public static final int MOVE_1_STAMINA_COST = 2;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 6;

    public Kyogre() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_HEAL + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_DEBUFF + DESCRIPTIONS[5];
        initializeDescriptionFromMoves();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new KyogreAlly(x, y, this);
    }
}