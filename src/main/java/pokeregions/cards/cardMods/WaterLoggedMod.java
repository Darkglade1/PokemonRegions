package pokeregions.cards.cardMods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import pokeregions.PokemonRegions;
import pokeregions.util.TexLoader;

import static pokeregions.util.Wiz.atb;

public class WaterLoggedMod extends AbstractCardModifier {

    public static final String ID = PokemonRegions.makeID(WaterLoggedMod.class.getSimpleName());

    public static final String WATER_LOGGED = PokemonRegions.makeUIPath("WaterLogged.png");
    private static final Texture WATER_LOGGED_TEXTURE = TexLoader.getTexture(WATER_LOGGED);
    private static final TextureRegion WATER_LOGGED_TEXTURE_REGION = new TextureRegion(WATER_LOGGED_TEXTURE);

    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean alreadyRetain = false;

    @Override
    public AbstractCardModifier makeCopy() {
        return new WaterLoggedMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.selfRetain) {
            card.selfRetain = true;
        } else {
            alreadyRetain = true;
        }
    }

    @Override
    public void onRetained(AbstractCard card) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                CardModifierManager.removeSpecificModifier(card, WaterLoggedMod.this, false);
                CardModifierManager.addModifier(card, new DampMod());
                this.isDone = true;
            }
        });
    }

    @Override
    public void onRemove(AbstractCard card) {
        if (!alreadyRetain) {
            card.selfRetain = false;
        }
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        return false;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[0] + rawDescription;
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        sb.draw(WATER_LOGGED_TEXTURE_REGION, card.hb.cX - (float) WATER_LOGGED_TEXTURE_REGION.getRegionWidth() / 2, card.hb.cY - (float) WATER_LOGGED_TEXTURE_REGION.getRegionHeight() / 2 + (75.0f * card.drawScale * Settings.scale), (float) WATER_LOGGED_TEXTURE_REGION.getRegionWidth() / 2, (float) WATER_LOGGED_TEXTURE_REGION.getRegionHeight() / 2, WATER_LOGGED_TEXTURE_REGION.getRegionWidth(), WATER_LOGGED_TEXTURE_REGION.getRegionHeight(), Settings.scale * card.drawScale, Settings.scale * card.drawScale, 0.0f);
    }
}
