package pokeregions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.powers.Courage;
import pokeregions.util.Wiz;

import static pokeregions.util.Wiz.*;

public class MomentOfCourage extends AbstractEasyCard {

    public static final String ID = PokemonRegions.makeID(MomentOfCourage.class.getSimpleName());
    private static final int BONUS_DAMAGE = 50;

    public MomentOfCourage() {
        super(ID, 1, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF, CardColor.COLORLESS);
        setBlock(8, 4);
        selfRetain = true;
        this.rawDescription = cardStrings.DESCRIPTION + BONUS_DAMAGE + cardStrings.EXTENDED_DESCRIPTION[0];
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Wiz.block(adp(), block);
        AbstractPokemonAlly pokemon = PlayerSpireFields.activePokemon.get(adp());
        if (pokemon != null) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    pokemon.halfDead = false;
                    this.isDone = true;
                }
            });
            applyToTarget(pokemon, pokemon, new Courage(pokemon, BONUS_DAMAGE));
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    pokemon.halfDead = true;
                    this.isDone = true;
                }
            });
        }
    }

}
