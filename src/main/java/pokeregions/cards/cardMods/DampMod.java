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

public class DampMod extends AbstractCardModifier {

    public static final String ID = PokemonRegions.makeID(DampMod.class.getSimpleName());

    public static final String DAMP1 = PokemonRegions.makeUIPath("Damp1.png");
    private static final Texture DAMP1_TEXTURE = TexLoader.getTexture(DAMP1);
    private static final TextureRegion DAMP1_TEXTURE_REGION = new TextureRegion(DAMP1_TEXTURE);

    public static final String DAMP2 = PokemonRegions.makeUIPath("Damp2.png");
    private static final Texture DAMP2_TEXTURE = TexLoader.getTexture(DAMP2);
    private static final TextureRegion DAMP2_TEXTURE_REGION = new TextureRegion(DAMP2_TEXTURE);

    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean alreadyRetain = false;
    private boolean alreadyExhaust = false;
    private int turns = 2;

    @Override
    public AbstractCardModifier makeCopy() {
        return new DampMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.selfRetain) {
            card.selfRetain = true;
        } else {
            alreadyRetain = true;
        }
        if (!card.exhaust) {
            card.exhaust = true;
        } else {
            alreadyExhaust = true;
        }
    }

    @Override
    public void onRetained(AbstractCard card) {
        turns--;
        if (turns <= 0) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    CardModifierManager.removeSpecificModifier(card, DampMod.this, false);
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void onRemove(AbstractCard card) {
        if (!alreadyRetain) {
            card.selfRetain = false;
        }
        if (!alreadyExhaust) {
            card.exhaust = false;
        }
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
        if (turns == 1) {
            sb.draw(DAMP2_TEXTURE_REGION, card.hb.cX - (float) DAMP2_TEXTURE_REGION.getRegionWidth() / 2, card.hb.cY - (float) DAMP2_TEXTURE_REGION.getRegionHeight() / 2, (float) DAMP2_TEXTURE_REGION.getRegionWidth() / 2, (float) DAMP2_TEXTURE_REGION.getRegionHeight() / 2, DAMP2_TEXTURE_REGION.getRegionWidth(), DAMP2_TEXTURE_REGION.getRegionHeight(), Settings.scale * card.drawScale, Settings.scale * card.drawScale, 0.0f);
        } else {
            sb.draw(DAMP1_TEXTURE_REGION, card.hb.cX - (float) DAMP1_TEXTURE_REGION.getRegionWidth() / 2, card.hb.cY - (float) DAMP1_TEXTURE_REGION.getRegionHeight() / 2, (float) DAMP1_TEXTURE_REGION.getRegionWidth() / 2, (float) DAMP1_TEXTURE_REGION.getRegionHeight() / 2, DAMP1_TEXTURE_REGION.getRegionWidth(), DAMP1_TEXTURE_REGION.getRegionHeight(), Settings.scale * card.drawScale, Settings.scale * card.drawScale, 0.0f);
        }
    }
}
