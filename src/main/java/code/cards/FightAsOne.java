package code.cards;

import code.PokemonRegions;
import code.monsters.AbstractPokemonAlly;
import code.patches.PlayerSpireFields;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static code.util.Wiz.adp;
import static code.util.Wiz.atb;
public class FightAsOne extends AbstractEasyCard {

    public static final String ID = PokemonRegions.makeID(FightAsOne.class.getSimpleName());

    public FightAsOne() {
        super(ID, 1, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY, CardColor.COLORLESS);
        setDamage(10, 4);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
        AbstractPokemonAlly pokemon = PlayerSpireFields.activePokemon.get(adp());
        if (pokemon != null) {
            pokemon.takeTurn();
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    pokemon.createIntent();
                    this.isDone = true;
                }
            });
        }
    }

}
