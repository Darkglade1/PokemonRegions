package pokeregions.cards.pokemonAllyCards.act2;

import basemod.helpers.TooltipInfo;
import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.util.Tags;

import java.util.List;

import static pokeregions.PokemonRegions.makeID;

@NoPools
public class Charmeleon extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Charmeleon.class.getSimpleName());
    public static final int MOVE_1_DAMAGE = 8;
    public static final int MOVE_2_DAMAGE = 9;
    public static final int MOVE_2_DEBUFF = 1;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 4;

    public Charmeleon() {
        super(ID, CardRarity.UNCOMMON);
        tags.add(Tags.STARTER_POKEMON);
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.misc = this.maxStamina = this.currentStamina = MAX_STAMINA;
        this.move1Description = DESCRIPTIONS[2] + MOVE_1_DAMAGE + DESCRIPTIONS[3];
        this.move2Description = DESCRIPTIONS[4] + MOVE_2_DAMAGE + DESCRIPTIONS[5] + MOVE_2_DEBUFF + DESCRIPTIONS[6];
        initializeDescriptionFromMoves();
    }

    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public List<TooltipInfo> getCustomTooltips() {
        return getStarterKeyword();
    }

    @Override
    public AbstractPokemonAlly getAssociatedPokemon(float x, float y) {
        return new pokeregions.monsters.act2.allyPokemon.Charmeleon(x, y, this);
    }
}