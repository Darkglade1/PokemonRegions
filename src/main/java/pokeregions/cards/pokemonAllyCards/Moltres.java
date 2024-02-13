package pokeregions.cards.pokemonAllyCards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Moltres extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Moltres.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 4;
    public static final int MOVE_1_BURN = 1;
    public static final int MOVE_1_HITS = 3;
    public static final int MOVE_2_EXHAUST = 4;
    public static final int MOVE_1_STAMINA_COST = 2;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Moltres() {
        super(ID, CardRarity.RARE);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3] + MOVE_1_BURN + DESCRIPTIONS[4] + MOVE_1_HITS + DESCRIPTIONS[5];
        this.move2Description = DESCRIPTIONS[6] + MOVE_2_EXHAUST + DESCRIPTIONS[7];
        initializeDescriptionFromMoves();
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new pokeregions.monsters.act1.allyPokemon.Moltres(x, y, this);
    }
}