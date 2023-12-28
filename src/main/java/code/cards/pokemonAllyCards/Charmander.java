package code.cards.pokemonAllyCards;

import code.cards.AbstractAllyPokemonCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static code.PokemonRegions.makeID;

public class Charmander extends AbstractAllyPokemonCard {
    public final static String ID = makeID(Charmander.class.getSimpleName());

    public static final int MOVE_1_DAMAGE = 6;
    public static final int MOVE_2_DAMAGE = 8;
    public static final int MOVE_1_STAMINA_COST = 0;
    public static final int MOVE_2_STAMINA_COST = 1;
    public static final int MAX_STAMINA = 5;

    public Charmander() {
        super(ID, 1, CardType.ATTACK, CardRarity.BASIC, CardTarget.NONE);
        this.damage = MOVE_1_DAMAGE;
        this.secondDamage = MOVE_2_DAMAGE;
        this.staminaCost1 = MOVE_1_STAMINA_COST;
        this.staminaCost2 = MOVE_2_STAMINA_COST;
        this.maxStamina = MAX_STAMINA;
        this.currentStamina = MAX_STAMINA;
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0] + ": " + cardStrings.EXTENDED_DESCRIPTION[1] + " NL " + cardStrings.EXTENDED_DESCRIPTION[2] + ": " + cardStrings.EXTENDED_DESCRIPTION[3];
        this.initializeDescription();
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.NONE);
    }
}