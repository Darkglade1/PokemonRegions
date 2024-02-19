package pokeregions.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import pokeregions.PokemonRegions;
import pokeregions.cards.cardMods.DampMod;
import pokeregions.cards.cardMods.WaterLoggedMod;
import pokeregions.util.ImageHelper;

import java.nio.IntBuffer;

import static pokeregions.PokemonRegions.makeVfxPath;

public class DampCardPatch {
    @SpirePatch(clz = AbstractCard.class, method = "render", paramtypez = SpriteBatch.class)
    public static class DripDripDrip {
        public static ShaderProgram dripShader = null;
        private static final FrameBuffer fbo = ImageHelper.createBuffer();

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractCard __instance, SpriteBatch spriteBatch) {
            if(dripShader == null) {
                dripShader = initDripShader(dripShader);
            }
            if (!Settings.hideCards) {
                if (CardModifierManager.hasModifier(__instance, DampMod.ID) || CardModifierManager.hasModifier(__instance, WaterLoggedMod.ID)) {
                    TextureRegion t = cardToTextureRegion(__instance, spriteBatch);
                    spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
                    ShaderProgram oldShader = spriteBatch.getShader();
                    spriteBatch.setShader(dripShader);
                    if (CardModifierManager.hasModifier(__instance, WaterLoggedMod.ID)) {
                        dripShader.setUniformf("u_time", PokemonRegions.time);
                        dripShader.setUniformf("u_dripAmount1", 1.0f);
                        dripShader.setUniformf("u_dripAmount2", 0.9f);
                        dripShader.setUniformf("u_dripSpeed", 0.4f);
                        dripShader.setUniformf("u_dripStrength", 0.4f);
                    } else if (CardModifierManager.hasModifier(__instance, DampMod.ID)) {
                        AbstractCardModifier damp = CardModifierManager.getModifiers(__instance, DampMod.ID).get(0);
                        if (damp instanceof DampMod) {
                            int turns = ((DampMod) damp).turns;
                            if (turns == 2) {
                                dripShader.setUniformf("u_time", PokemonRegions.time);
                                dripShader.setUniformf("u_dripAmount1", 0.65f);
                                dripShader.setUniformf("u_dripAmount2", 0.45f);
                                dripShader.setUniformf("u_dripSpeed", 0.2f);
                                dripShader.setUniformf("u_dripStrength", 0.2f);
                            } else {
                                dripShader.setUniformf("u_time", PokemonRegions.time);
                                dripShader.setUniformf("u_dripAmount1", 0.4f);
                                dripShader.setUniformf("u_dripAmount2", 0.3f);
                                dripShader.setUniformf("u_dripSpeed", 0.1f);
                                dripShader.setUniformf("u_dripStrength", 0.1f);
                            }
                        }
                    }

                    spriteBatch.draw(t, -Settings.VERT_LETTERBOX_AMT, -Settings.HORIZ_LETTERBOX_AMT);
                    spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                    spriteBatch.setShader(oldShader);
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }

        public static ShaderProgram initDripShader(ShaderProgram dripShader) {
            if (dripShader == null) {
                try {
                    dripShader = new ShaderProgram(
                            Gdx.files.internal(makeVfxPath("drip/vertex.vs")),
                            Gdx.files.internal(makeVfxPath("drip/fragment.fs"))
                    );
                    if (!dripShader.isCompiled()) {
                        System.err.println(dripShader.getLog());
                    }
                    if (!dripShader.getLog().isEmpty()) {
                        System.out.println(dripShader.getLog());
                    }
                } catch (GdxRuntimeException e) {
                    System.out.println("ERROR: Failed to init damp shader:");
                    e.printStackTrace();
                }
            }
            return dripShader;
        }

        public static TextureRegion cardToTextureRegion(AbstractCard card, SpriteBatch sb) {
            sb.end();
            ImageHelper.beginBuffer(fbo);
            sb.begin();
            IntBuffer buf_rgb = BufferUtils.newIntBuffer(16);
            IntBuffer buf_a = BufferUtils.newIntBuffer(16);
            Gdx.gl.glGetIntegerv(GL30.GL_BLEND_EQUATION_RGB, buf_rgb);
            Gdx.gl.glGetIntegerv(GL30.GL_BLEND_EQUATION_ALPHA, buf_a);

            Gdx.gl.glBlendEquationSeparate(buf_rgb.get(0), GL30.GL_MAX);
            Gdx.gl.glBlendEquationSeparate(GL30.GL_FUNC_ADD, GL30.GL_MAX);
            card.render(sb, false);
            Gdx.gl.glBlendEquationSeparate(GL30.GL_FUNC_ADD, GL30.GL_FUNC_ADD);
            Gdx.gl.glBlendEquationSeparate(buf_rgb.get(0), buf_a.get(0));

            sb.end();
            fbo.end();
            sb.begin();
            return ImageHelper.getBufferTexture(fbo);
        }
    }
}