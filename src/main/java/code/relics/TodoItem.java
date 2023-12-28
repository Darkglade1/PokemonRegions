package code.relics;

import code.actions.UsePreBattleActionAction;
import code.monsters.act1.allyPokemon.Charmander;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;

import static code.PokemonRegions.makeID;
import static code.util.Wiz.atb;

public class TodoItem extends AbstractEasyRelic {
    public static final String ID = makeID("TodoItem");

    public TodoItem() {
        super(ID, RelicTier.STARTER, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        Charmander ally = new Charmander(-700.0f, 0.0f);
        atb(new SpawnMonsterAction(ally, false));
        atb(new UsePreBattleActionAction(ally));
    }
}
